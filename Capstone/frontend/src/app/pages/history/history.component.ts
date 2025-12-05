import { Component, computed, signal, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { HistoryService } from '@app/core/services/history.service';
import { AuthService } from '@app/core/services/auth.services';
import {
  MealHistoryItem,
  ExerciseHistoryItem,
  WeighInHistoryItem,
} from '@app/core/models/history.models';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [
    CommonModule,
    MatTabsModule,
    MatTableModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css'],
})
export class HistoryComponent implements OnInit {
  private historyService = inject(HistoryService);
  private authService = inject(AuthService);

  // Signals for date range
  start = signal<Date | null>(new Date(Date.now() - 7 * 86400000)); // 7 days ago
  end = signal<Date | null>(new Date());

  // Signals for data
  meals = signal<MealHistoryItem[]>([]);
  exercises = signal<ExerciseHistoryItem[]>([]);
  weighIns = signal<WeighInHistoryItem[]>([]);

  // Loading states
  loadingMeals = signal(false);
  loadingExercises = signal(false);
  loadingWeighIns = signal(false);

  // Error states
  errorMeals = signal<string | null>(null);
  errorExercises = signal<string | null>(null);
  errorWeighIns = signal<string | null>(null);

  filteredMeals = computed(() => this.meals());
  filteredExercises = computed(() => this.exercises());
  filteredWeigh = computed(() => this.weighIns());

  // Get user ID from localStorage (same method as log.component)
  private get userId(): number {
    const userIdStr = localStorage.getItem('hw_user_id');
    if (!userIdStr || userIdStr === '0') {
      console.warn('User ID not found in localStorage');
      return 1; // Fallback
    }
    const id = Number(userIdStr);
    console.log('Using user ID:', id);
    return id;
  }

  ngOnInit() {
    console.log('History component initialized');
    console.log('Current user ID:', this.userId);
    this.loadData();
  }

  private loadData() {
    const start = this.start();
    const end = this.end();

    console.log('Loading data for date range:', start, 'to', end);

    if (!start || !end) {
      console.warn('Start or end date is null');
      return;
    }

    this.loadMeals(start, end);
    this.loadExercises(start, end);
    this.loadWeighIns(start, end);
  }

  private loadMeals(start: Date, end: Date) {
    console.log('Loading meals...');
    this.loadingMeals.set(true);
    this.errorMeals.set(null);

    this.historyService.getMealHistory(this.userId, start, end).subscribe({
      next: (data) => {
        console.log('Meals loaded successfully:', data);
        this.meals.set(data);
        this.loadingMeals.set(false);
      },
      error: (err) => {
        console.error('Error loading meals:', err);
        this.errorMeals.set('Failed to load meal history');
        this.loadingMeals.set(false);
      },
    });
  }

  private loadExercises(start: Date, end: Date) {
    console.log('Loading exercises...');
    this.loadingExercises.set(true);
    this.errorExercises.set(null);

    this.historyService.getExerciseHistory(this.userId, start, end).subscribe({
      next: (data) => {
        console.log('Exercises loaded successfully:', data);
        this.exercises.set(data);
        this.loadingExercises.set(false);
      },
      error: (err) => {
        console.error('Error loading exercises:', err);
        this.errorExercises.set('Failed to load exercise history');
        this.loadingExercises.set(false);
      },
    });
  }

  private loadWeighIns(start: Date, end: Date) {
    console.log('Loading weigh-ins...');
    this.loadingWeighIns.set(true);
    this.errorWeighIns.set(null);

    this.historyService.getWeighInHistory(this.userId, start, end).subscribe({
      next: (data) => {
        console.log('Weigh-ins loaded successfully:', data);
        this.weighIns.set(data);
        this.loadingWeighIns.set(false);
      },
      error: (err) => {
        console.error('Error loading weigh-ins:', err);
        this.errorWeighIns.set('Failed to load weigh-in history');
        this.loadingWeighIns.set(false);
      },
    });
  }

  applyRange(start?: Date | null, end?: Date | null) {
    if (start !== undefined) this.start.set(start);
    if (end !== undefined) this.end.set(end);

    this.loadData();
  }

  private daysAgo(n: number): Date {
    const d = new Date();
    d.setDate(d.getDate() - n);
    return d;
  }

  applyLast7Days() {
    this.applyRange(this.daysAgo(7), new Date());
  }

  // ---------- DELETE METHODS ----------

  deleteMealItem(item: MealHistoryItem) {
    if (!confirm(`Delete ${item.item} from ${item.type}?`)) {
      return;
    }

    console.log('Deleting meal-food:', item.mealFoodId);
    this.historyService.deleteMeal(item.mealFoodId).subscribe({
      next: () => {
        console.log('Meal item deleted successfully');
        // Reload meals to reflect deletion
        const start = this.start();
        const end = this.end();
        if (start && end) {
          this.loadMeals(start, end);
        }
      },
      error: (err) => {
        console.error('Error deleting meal item:', err);
        alert('Failed to delete meal item. Please try again.');
      },
    });
  }

  deleteExerciseItem(item: ExerciseHistoryItem) {
    if (!confirm(`Delete ${item.kind} exercise?`)) {
      return;
    }

    console.log('Deleting exercise:', item.id);
    this.historyService.deleteExercise(item.id).subscribe({
      next: () => {
        console.log('Exercise deleted successfully');
        // Reload exercises to reflect deletion
        const start = this.start();
        const end = this.end();
        if (start && end) {
          this.loadExercises(start, end);
        }
      },
      error: (err) => {
        console.error('Error deleting exercise:', err);
        alert('Failed to delete exercise. Please try again.');
      },
    });
  }

  deleteWeighInItem(item: WeighInHistoryItem) {
    if (!confirm(`Delete weigh-in of ${item.weight} lbs?`)) {
      return;
    }

    console.log('Deleting weigh-in:', item.weighInId);
    this.historyService.deleteWeighIn(item.weighInId).subscribe({
      next: () => {
        console.log('Weigh-in deleted successfully');
        // Reload weigh-ins to reflect deletion
        const start = this.start();
        const end = this.end();
        if (start && end) {
          this.loadWeighIns(start, end);
        }
      },
      error: (err) => {
        console.error('Error deleting weigh-in:', err);
        alert('Failed to delete weigh-in. Please try again.');
      },
    });
  }
}
