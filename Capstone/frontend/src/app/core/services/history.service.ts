import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import {
  MealDTO,
  MealFoodDTO,
  FoodDTO,
  UserExerciseDTO,
  WeighIn,
  MealHistoryItem,
  ExerciseHistoryItem,
  WeighInHistoryItem,
} from '../models/history.models';

@Injectable({ providedIn: 'root' })
export class HistoryService {
  private http = inject(HttpClient);
  private baseUrl = environment.apiUrl;

  // ---------- MEALS ----------
  getMealHistory(userId: number, startDate: Date, endDate: Date): Observable<MealHistoryItem[]> {
    console.log('🍽️ Fetching meal history for user:', userId, 'from', startDate, 'to', endDate);

    // Convert range to date-only strings to avoid timezone issues
    const fromStr = this.toDateString(startDate);
    const toStr = this.toDateString(endDate);

    return this.http.get<MealDTO[]>(`${this.baseUrl}/meals/user/${userId}`).pipe(
      // Filter meals by date range using "YYYY-MM-DD" string comparison
      map((meals) =>
        meals.filter((meal) => {
          const mealStr = this.extractDateString(meal.date);
          const inRange = mealStr >= fromStr && mealStr <= toStr;
          // console.log('🔎 Meal', meal.date, '→', mealStr, 'range', fromStr, 'to', toStr, 'inRange?', inRange);
          return inRange;
        })
      ),
      // For each meal, load its foods and flatten into MealHistoryItem[]
      switchMap((filteredMeals) => {
        if (filteredMeals.length === 0) return of([]);

        const mealRequests = filteredMeals.map((meal) =>
          this.http.get<MealFoodDTO[]>(`${this.baseUrl}/meal-food/meal/${meal.mealId}`).pipe(
            switchMap((mealFoods) => {
              if (mealFoods.length === 0) return of([]);

              const foodRequests = mealFoods.map((mf) =>
                this.http.get<FoodDTO>(`${this.baseUrl}/foods/${mf.foodId}`).pipe(
                  map(
                    (food) =>
                      ({
                        mealId: meal.mealId, // Added for deletion
                        mealFoodId: mf.id, // Added for deletion
                        date: meal.date,
                        type: this.formatMealType(meal.type),
                        item: food.name,
                        amount: food.amount,
                        unit: food.unit,
                        servings: mf.servings,
                        kcal: Math.round(food.calories * mf.servings),
                      } as MealHistoryItem)
                  )
                )
              );

              return forkJoin(foodRequests);
            })
          )
        );

        return forkJoin(mealRequests).pipe(map((results) => results.flat()));
      })
    );
  }

  // ---------- EXERCISES ----------
  getExerciseHistory(
    userId: number,
    startDate: Date,
    endDate: Date
  ): Observable<ExerciseHistoryItem[]> {
    console.log('🏃 Fetching exercise history for user:', userId, 'from', startDate, 'to', endDate);

    const fromStr = this.toDateString(startDate);
    const toStr = this.toDateString(endDate);

    return this.http.get<UserExerciseDTO[]>(`${this.baseUrl}/user-exercises`).pipe(
      map((exercises) => {
        const filtered = exercises.filter((ex) => {
          const exStr = this.extractDateString(ex.date);
          return ex.userId === userId && exStr >= fromStr && exStr <= toStr;
        });

        return filtered.map(
          (ex) =>
            ({
              id: ex.id, // Added for deletion
              date: ex.date,
              kind: ex.exerciseName || 'Unknown',
              amount: this.formatExerciseAmount(ex),
              kcal: Math.round(ex.caloriesBurned || 0),
            } as ExerciseHistoryItem)
        );
      })
    );
  }

  // ---------- WEIGH-INS ----------
  getWeighInHistory(
    userId: number,
    startDate: Date,
    endDate: Date
  ): Observable<WeighInHistoryItem[]> {
    console.log('⚖️ Fetching weigh-in history for user:', userId, 'from', startDate, 'to', endDate);

    const fromStr = this.toDateString(startDate);
    const toStr = this.toDateString(endDate);

    // Use user-specific endpoint: /api/weighin/user/{userId}
    return this.http.get<WeighIn[]>(`${this.baseUrl}/api/weighin/user/${userId}`).pipe(
      map((weighIns) =>
        weighIns
          .filter((w) => {
            const wStr = this.extractDateString(w.date);
            return wStr >= fromStr && wStr <= toStr;
          })
          .map(
            (w) =>
              ({
                weighInId: w.weighInId, // Added for deletion
                date: w.date,
                weight: w.weight,
                notes: w.notes ?? null,
              } as WeighInHistoryItem)
          )
          .sort((a, b) =>
            this.extractDateString(b.date).localeCompare(this.extractDateString(a.date))
          )
      )
    );
  }

  // ---------- HELPERS ----------
  private formatMealType(type: string): string {
    switch (type) {
      case 'BREAKFAST':
        return 'Breakfast';
      case 'LUNCH':
        return 'Lunch';
      case 'DINNER':
        return 'Dinner';
      case 'SNACK':
        return 'Snack';
      default:
        return type;
    }
  }

  private formatExerciseAmount(ex: UserExerciseDTO): string {
    if (ex.durationMinutes) {
      return `${ex.durationMinutes} min`;
    } else if (ex.reps && ex.sets) {
      return `${ex.sets} sets × ${ex.reps} reps`;
    } else if (ex.reps) {
      return `${ex.reps} reps`;
    }
    return 'N/A';
  }

  /** Convert a Date to "YYYY-MM-DD" in local time */
  private toDateString(d: Date): string {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  /** Extract "YYYY-MM-DD" from a string or Date */
  private extractDateString(value: string | Date): string {
    if (value instanceof Date) {
      return this.toDateString(value);
    }
    if (typeof value === 'string') {
      // Handles "2025-11-11" and "2025-11-11T00:00:00Z"
      return value.split('T')[0];
    }
    return '';
  }

  // ---------- DELETE METHODS ----------

  /**
   * Delete a meal-food entry by ID
   * Uses MealFoodController DELETE /meal-food/{id}
   */
  deleteMeal(mealFoodId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/meal-food/${mealFoodId}`);
  }

  /**
   * Delete a user exercise by ID
   */
  deleteExercise(exerciseId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/user-exercises/${exerciseId}`);
  }

  /**
   * Delete a weigh-in by ID
   */
  deleteWeighIn(weighInId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/api/weighin/${weighInId}`);
  }
}
