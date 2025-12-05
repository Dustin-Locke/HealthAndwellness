import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, of } from 'rxjs';
import { map, switchMap, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface DashboardStats {
  caloriesIn: number;
  caloriesOut: number;
  latestWeighIn: number;
  weightHistory: number[]; // Last 7 days
  caloriesHistory: number[]; // Last 7 days (net calories)
}

interface MealDTO {
  mealId: number;
  userId: number;
  type: string;
  date: string;
}

interface MealFoodDTO {
  id: number;
  mealId: number;
  foodId: number;
  servings: number;
}

interface FoodDTO {
  id: number;
  name: string;
  calories: number;
  amount: number;
  unit: string;
  servings: number;
}

interface ExerciseDTO {
  id: number;
  userId: number;
  exerciseId: number;
  exerciseName: string;
  date: string;
  durationMinutes?: number;
  reps?: number;
  sets?: number;
  intensity: string;
  caloriesBurned: number;
  complete: boolean;
}

interface WeighInDTO {
  weighInId: number;
  userId: number;
  date: string;
  weight: number;
  height: number;
  notes?: string;
}

interface MealWithCalories {
  date: string;
  totalCalories: number;
}

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private http = inject(HttpClient);
  private baseUrl = environment.apiUrl;

  getDashboardStats(userId: number): Observable<DashboardStats> {
    const today = this.toDateString(new Date());
    const sevenDaysAgo = this.toDateString(this.daysAgo(7));

    console.log('Loading dashboard for user:', userId, 'from', sevenDaysAgo, 'to', today);

    return forkJoin({
      meals: this.getMealsForRange(userId, sevenDaysAgo, today),
      exercises: this.getExercisesForRange(userId, sevenDaysAgo, today),
      weighIns: this.getWeighInsForRange(userId, sevenDaysAgo, today),
    }).pipe(
      map(({ meals, exercises, weighIns }) => {
        console.log(
          'Got data - Meals:',
          meals.length,
          'Exercises:',
          exercises.length,
          'WeighIns:',
          weighIns.length
        );

        // Calculate today's calories in
        const todayMeals = meals.filter((m) => this.extractDateString(m.date) === today);
        const caloriesIn = todayMeals.reduce((sum, m) => sum + m.totalCalories, 0);

        // Calculate today's calories out
        const todayExercises = exercises.filter((e) => this.extractDateString(e.date) === today);
        const caloriesOut = todayExercises.reduce((sum, e) => sum + e.caloriesBurned, 0);

        // Get latest weigh-in (sort by DATE to get most recent, not by ID)
        const sortedWeighIns = weighIns.sort((a, b) => {
          const dateA = this.extractDateString(a.date);
          const dateB = this.extractDateString(b.date);
          return dateB.localeCompare(dateA); // Most recent date first
        });
        const latestWeighIn = sortedWeighIns.length > 0 ? sortedWeighIns[0].weight : 0;

        // Build weight history (last 7 days)
        const weightHistory = this.buildWeightHistory(weighIns);

        // Build calories history (last 7 days)
        const caloriesHistory = this.buildCaloriesHistory(meals, exercises);

        return {
          caloriesIn: Math.round(caloriesIn),
          caloriesOut: Math.round(caloriesOut),
          latestWeighIn: Math.round(latestWeighIn * 10) / 10,
          weightHistory,
          caloriesHistory,
        };
      }),
      catchError((error) => {
        console.error('Error in getDashboardStats:', error);
        return of({
          caloriesIn: 0,
          caloriesOut: 0,
          latestWeighIn: 0,
          weightHistory: [],
          caloriesHistory: [],
        });
      })
    );
  }

  private getMealsForRange(
    userId: number,
    startDate: string,
    endDate: string
  ): Observable<MealWithCalories[]> {
    return this.http.get<MealDTO[]>(`${this.baseUrl}/meals/user/${userId}`).pipe(
      // Filter meals by date range
      map((meals) =>
        meals.filter((m) => {
          const dateStr = this.extractDateString(m.date);
          return dateStr >= startDate && dateStr <= endDate;
        })
      ),
      // For each meal, get its foods and calculate total calories
      switchMap((meals) => {
        if (meals.length === 0) return of([]);

        const mealCalorieRequests = meals.map((meal) => this.calculateMealCalories(meal));

        return forkJoin(mealCalorieRequests);
      }),
      catchError((error) => {
        console.error('Error loading meals:', error);
        return of([]);
      })
    );
  }

  private calculateMealCalories(meal: MealDTO): Observable<MealWithCalories> {
    return this.http.get<MealFoodDTO[]>(`${this.baseUrl}/meal-food/meal/${meal.mealId}`).pipe(
      switchMap((mealFoods) => {
        if (mealFoods.length === 0) {
          return of({ date: meal.date, totalCalories: 0 });
        }

        const foodCalorieRequests = mealFoods.map((mf) =>
          this.http.get<FoodDTO>(`${this.baseUrl}/foods/${mf.foodId}`).pipe(
            map((food) => food.calories * mf.servings),
            catchError(() => of(0))
          )
        );

        return forkJoin(foodCalorieRequests).pipe(
          map((calories) => ({
            date: meal.date,
            totalCalories: calories.reduce((sum, cal) => sum + cal, 0),
          }))
        );
      }),
      catchError(() => of({ date: meal.date, totalCalories: 0 }))
    );
  }

  private getExercisesForRange(
    userId: number,
    startDate: string,
    endDate: string
  ): Observable<ExerciseDTO[]> {
    return this.http.get<ExerciseDTO[]>(`${this.baseUrl}/user-exercises`).pipe(
      map((exercises) =>
        exercises.filter((e) => {
          const dateStr = this.extractDateString(e.date);
          return e.userId === userId && dateStr >= startDate && dateStr <= endDate;
        })
      ),
      catchError((error) => {
        console.error('Error loading exercises:', error);
        return of([]);
      })
    );
  }

  private getWeighInsForRange(
    userId: number,
    startDate: string,
    endDate: string
  ): Observable<WeighInDTO[]> {
    return this.http.get<WeighInDTO[]>(`${this.baseUrl}/api/weighin/user/${userId}`).pipe(
      map((weighIns) =>
        weighIns.filter((w) => {
          const dateStr = this.extractDateString(w.date);
          return dateStr >= startDate && dateStr <= endDate;
        })
      ),
      catchError((error) => {
        console.error('Error loading weigh-ins:', error);
        return of([]);
      })
    );
  }

  private buildWeightHistory(weighIns: WeighInDTO[]): number[] {
    const last7Days = this.getLast7Days();
    const weightMap = new Map<string, number>();

    // Map weigh-ins to dates
    weighIns.forEach((w) => {
      const dateStr = this.extractDateString(w.date);
      weightMap.set(dateStr, w.weight);
    });

    const history: number[] = [];
    let lastWeight = 0;

    for (const date of last7Days) {
      if (weightMap.has(date)) {
        lastWeight = weightMap.get(date)!;
      }
      history.push(lastWeight);
    }

    return history;
  }

  private buildCaloriesHistory(meals: MealWithCalories[], exercises: ExerciseDTO[]): number[] {
    const last7Days = this.getLast7Days();
    const caloriesMap = new Map<string, { in: number; out: number }>();

    // Initialize all days
    last7Days.forEach((date) => caloriesMap.set(date, { in: 0, out: 0 }));

    // Add meal calories
    meals.forEach((m) => {
      const dateStr = this.extractDateString(m.date);
      if (caloriesMap.has(dateStr)) {
        caloriesMap.get(dateStr)!.in += m.totalCalories;
      }
    });

    // Add exercise calories
    exercises.forEach((e) => {
      const dateStr = this.extractDateString(e.date);
      if (caloriesMap.has(dateStr)) {
        caloriesMap.get(dateStr)!.out += e.caloriesBurned;
      }
    });

    // Calculate net calories for each day
    return last7Days.map((date) => {
      const day = caloriesMap.get(date)!;
      return day.in - day.out;
    });
  }

  private getLast7Days(): string[] {
    const days: string[] = [];
    for (let i = 6; i >= 0; i--) {
      days.push(this.toDateString(this.daysAgo(i)));
    }
    return days;
  }

  private daysAgo(n: number): Date {
    const d = new Date();
    d.setDate(d.getDate() - n);
    return d;
  }

  private toDateString(d: Date): string {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private extractDateString(value: string | Date): string {
    if (value instanceof Date) {
      return this.toDateString(value);
    }
    if (typeof value === 'string') {
      return value.split('T')[0];
    }
    return '';
  }
}
