import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

// Type definitions
export type MealType = 'BREAKFAST' | 'LUNCH' | 'DINNER' | 'SNACK';
export type MeasurementSystem = 'METRIC' | 'IMPERIAL';
export type ExerciseIntensity = 'LIGHT' | 'MODERATE' | 'VIGOROUS';
export type MeasurementUnit =
  | 'OUNCE_WEIGHT'
  | 'POUND'
  | 'GRAM'
  | 'KILOGRAM'
  | 'OUNCE_VOL'
  | 'TEASPOON'
  | 'TABLESPOON'
  | 'CUP'
  | 'MILLILITER'
  | 'LITER';

export interface CreateMealRequest {
  userId: number;
  type: MealType;
  date: string; // YYYY-MM-DD
}

export interface CreateFoodRequest {
  name: string;
  calories: number;
  amount: number;
  unit: MeasurementUnit;
  servings: number;
}

export interface AddFoodToMealRequest {
  mealId: number;
  foodId: number;
  servings: number;
}

export interface CreateUserExerciseRequest {
  userId: number;
  exerciseId: number;
  date: string;
  durationMinutes?: number | null;
  reps?: number | null;
  sets?: number | null;
  intensity: ExerciseIntensity;
  caloriesBurned?: number | null;
  complete: boolean;
}

export interface CreateWeighInRequest {
  userId: number;
  date: string;
  weight: number;
  height?: number | null;
  notes?: string | null;
  measurement?: MeasurementSystem;
}

@Injectable({ providedIn: 'root' })
export class LogApi {
  private http = inject(HttpClient);
  private baseUrl = environment.apiUrl;

  /**
   * Get headers with JWT token for authenticated requests
   */
  private getAuthHeaders() {
    const token = localStorage.getItem('hw_jwt_token');
    return {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: token ? `Bearer ${token}` : '',
      }),
    };
  }

  // ========== MEAL ENDPOINTS ==========

  /**
   * Create a new meal
   * POST /meals
   */
  createMeal(payload: CreateMealRequest): Observable<any> {
    console.log('API: Creating meal', payload);
    return this.http.post(`${this.baseUrl}/meals`, payload, this.getAuthHeaders());
  }

  // ========== FOOD ENDPOINTS ==========

  /**
   * Create a new food item
   * POST /foods
   */
  createFood(payload: CreateFoodRequest): Observable<any> {
    console.log('API: Creating food', payload);
    return this.http.post(`${this.baseUrl}/foods`, payload, this.getAuthHeaders());
  }

  /**
   * Search for food by name
   * GET /foods/search?q={name}
   */
  searchFoodByName(name: string): Observable<any[]> {
    console.log('API: Searching for food:', name);
    return this.http.get<any[]>(`${this.baseUrl}/foods/search?q=${encodeURIComponent(name)}`);
  }

  /**
   * Get food by ID
   * GET /foods/{id}
   */
  getFoodById(id: number): Observable<any> {
    console.log('API: Getting food by ID:', id);
    return this.http.get(`${this.baseUrl}/foods/${id}`);
  }

  // ========== MEAL-FOOD ENDPOINTS ==========

  /**
   * Link a food item to a meal
   * POST /meal-food
   */
  addFoodToMeal(payload: AddFoodToMealRequest): Observable<any> {
    console.log('API: Adding food to meal', payload);
    return this.http.post(`${this.baseUrl}/meal-food`, payload, this.getAuthHeaders());
  }

  // ========== EXERCISE ENDPOINTS ==========

  /**
   * Search for exercise by name
   * GET /exercises/name/{name}
   */
  searchExerciseByName(name: string): Observable<any[]> {
    console.log('API: Searching for exercise:', name);
    return this.http.get<any[]>(`${this.baseUrl}/exercises/name/${encodeURIComponent(name)}`);
  }

  /**
   * Get all exercises
   * GET /exercises
   */
  getAllExercises(): Observable<any[]> {
    console.log('API: Getting all exercises');
    return this.http.get<any[]>(`${this.baseUrl}/exercises`);
  }

  // ========== USER EXERCISE ENDPOINTS ==========

  /**
   * Create a user exercise entry
   * POST /user-exercises
   */
  createUserExercise(payload: CreateUserExerciseRequest): Observable<any> {
    console.log('API: Creating user exercise', payload);
    return this.http.post(`${this.baseUrl}/user-exercises`, payload, this.getAuthHeaders());
  }

  // ========== WEIGH-IN ENDPOINTS ==========

  /**
   * Create a weigh-in entry
   * POST /api/weighin
   */
  createWeighIn(payload: CreateWeighInRequest): Observable<any> {
    console.log('API: Creating weigh-in', payload);
    return this.http.post(`${this.baseUrl}/api/weighin`, payload, this.getAuthHeaders());
  }
}
