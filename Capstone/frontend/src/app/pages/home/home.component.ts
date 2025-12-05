import { Component, OnInit, OnDestroy, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '@app/core/services/auth.services';
import { DashboardService } from '@app/core/services/dashboard.service';
import { QuotesService } from '@app/core/services/quotes.service';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { Router, NavigationEnd } from '@angular/router';
import { filter, tap, catchError } from 'rxjs/operators';
import { Subscription, Observable, of } from 'rxjs';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit, OnDestroy {
  public auth = inject(AuthService);
  private dashboardService = inject(DashboardService);
  private quotesService = inject(QuotesService);
  private http = inject(HttpClient);
  private router = inject(Router);

  private routerSubscription?: Subscription;

  today = new Date();

  caloriesIn = signal(0);
  caloriesOut = signal(0);
  latestWeighIn = signal(0);
  weightHistory = signal<number[]>([]);
  rawCaloriesHistory = signal<number[]>([]);

  initialWeight = signal(0);
  goalWeight = signal(0);
  firstName = signal('');

  loading = signal(true);
  quote = this.quotesService.getRandomQuote(); // Get random quote on load

  // Computed values
  netCalories = computed(() => this.caloriesIn() - this.caloriesOut());

  weightChange = computed(() => {
    const history = this.weightHistory().filter((w) => w > 0);
    if (history.length === 0) return 0;

    // If only one weigh-in, compare to initial weight
    if (history.length === 1) {
      const initial = this.initialWeight();
      if (initial > 0) {
        return Math.round((history[0] - initial) * 10) / 10;
      }
      return 0;
    }

    // Compare first and last weigh-in in the 7-day period
    const change = history[history.length - 1] - history[0];
    return Math.round(change * 10) / 10;
  });

  goalProgress = computed(() => {
    const initial = this.initialWeight();
    const current = this.latestWeighIn();
    const goal = this.goalWeight();

    // Return 0 if no data or no weigh-ins logged yet
    if (initial === 0 || goal === 0 || initial === goal || current === 0) return 0;

    const totalLoss = initial - goal;
    const currentLoss = initial - current;
    const progress = (currentLoss / totalLoss) * 100;

    return Math.max(0, Math.min(100, Math.round(progress)));
  });

  remainingWeight = computed(() => {
    const current = this.latestWeighIn();
    const goal = this.goalWeight();

    // If no weigh-ins yet, show distance from initial weight to goal
    if (current === 0) {
      const initial = this.initialWeight();
      return Math.max(0, Math.round((initial - goal) * 10) / 10);
    }

    const remaining = current - goal;
    return Math.max(0, Math.round(remaining * 10) / 10);
  });

  daysActive = computed(() => {
    const history = this.weightHistory();
    return history.filter((w) => w > 0).length;
  });

  caloriesData = computed(() => this.normalizeCaloriesData(this.rawCaloriesHistory()));

  rawCaloriesData = computed(() => this.rawCaloriesHistory());

  get weightBars(): number[] {
    const arr = this.weightHistory();
    if (arr.length === 0) return [];
    const filtered = arr.filter((v) => v > 0);
    if (filtered.length === 0) return [];
    const min = Math.min(...filtered);
    const max = Math.max(...filtered);
    if (max === min) return arr.map((v) => (v > 0 ? 70 : 0));
    return arr.map((v) => (v > 0 ? 35 + ((v - min) / (max - min)) * 65 : 0));
  }

  private get userId(): number {
    const userIdStr = localStorage.getItem('hw_user_id');
    if (!userIdStr || userIdStr === '0') {
      console.warn('User ID not found in localStorage');
      return 1;
    }
    return Number(userIdStr);
  }

  ngOnInit() {
    this.loadUserProfile().subscribe({
      next: () => {
        this.loadDashboardData();
      },
      error: () => {
        this.loadDashboardData();
      },
    });

    this.routerSubscription = this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        if (event.url === '/home' || event.url === '/') {
          this.quote = this.quotesService.getRandomQuote();
          this.loadUserProfile().subscribe(() => {
            this.loadDashboardData();
          });
        }
      });
  }

  ngOnDestroy() {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
  }

  loadUserProfile(): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/api/profile/me`).pipe(
      tap((user) => {
        const initial = user.initialWeight || user.weight || 0;
        this.initialWeight.set(initial);
        this.goalWeight.set(user.goalWeight || 0);
        this.firstName.set(user.firstName || 'there');
      }),
      catchError((err) => {
        console.error('Error loading user profile:', err);
        this.initialWeight.set(0);
        this.goalWeight.set(0);
        this.firstName.set('there');
        return of(null);
      })
    );
  }

  loadDashboardData() {
    this.loading.set(true);
    this.dashboardService.getDashboardStats(this.userId).subscribe({
      next: (stats) => {
        this.caloriesIn.set(stats.caloriesIn);
        this.caloriesOut.set(stats.caloriesOut);
        this.latestWeighIn.set(stats.latestWeighIn);
        this.weightHistory.set(stats.weightHistory);
        this.rawCaloriesHistory.set(stats.caloriesHistory);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error loading dashboard stats:', err);
        this.loading.set(false);
      },
    });
  }

  private normalizeCaloriesData(data: number[]): number[] {
    if (data.length === 0) return [];
    const maxAbs = Math.max(...data.map(Math.abs));
    if (maxAbs === 0) return data.map(() => 50);

    return data.map((v) => {
      const normalized = Math.abs(v) / maxAbs;
      return 35 + normalized * 65;
    });
  }

  getDayLabel(index: number): string {
    const days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
    const date = new Date();
    date.setDate(date.getDate() - (6 - index));
    return days[date.getDay()];
  }

  getDayLabelsArray(): string[] {
    return Array.from({ length: 7 }, (_, i) => this.getDayLabel(i));
  }

  // Number formatting helper
  formatNumber(num: number): number {
    if (num === 0) return 0;
    // Round to 1 decimal place and remove trailing zeros
    return Math.round(num * 10) / 10;
  }

  // Simple bar chart height calculations
  getWeightBarHeight(index: number): number {
    const weights = this.weightHistory().filter((w) => w > 0);
    if (weights.length === 0) return 0;

    const weight = this.weightHistory()[index];
    if (weight === 0) return 0;

    const min = Math.min(...weights);
    const max = Math.max(...weights);

    // If all weights are the same, show at 60% height
    if (max === min) return 60;

    // Scale between 20% and 90%
    const normalized = (weight - min) / (max - min);
    return 20 + normalized * 70;
  }

  getCaloriesBarHeight(index: number): number {
    const calories = this.rawCaloriesHistory();
    const cal = calories[index];

    if (cal === 0) return 5; // Minimum visible bar

    // Find the max absolute value for scaling
    const maxAbs = Math.max(...calories.map(Math.abs));
    if (maxAbs === 0) return 5;

    // Scale between 5% and 90%
    const normalized = Math.abs(cal) / maxAbs;
    return 5 + normalized * 85;
  }

  onLogout() {
    this.auth.logout();
  }
}
