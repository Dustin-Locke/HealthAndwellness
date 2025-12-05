import { MeasurementUnit } from '../services/log.api';

export interface MealHistoryItem {
  mealId: number; // Added for deletion
  mealFoodId: number; // Added for deletion
  date: string;
  type: string;
  item: string;
  amount: number;
  unit: string;
  servings: number;
  kcal: number;
}

export interface ExerciseHistoryItem {
  id: number; // Added for deletion
  date: string;
  kind: string;
  amount: string;
  kcal: number;
}

export interface WeighInHistoryItem {
  weighInId: number; // Added for deletion
  date: string;
  weight: number;
  notes?: string | null;
}

// Backend DTOs as returned by your API
export interface MealDTO {
  mealId: number;
  userId: number;
  type: 'BREAKFAST' | 'LUNCH' | 'DINNER' | 'SNACK';
  date: string;
}

export interface MealFoodDTO {
  id: number;
  mealId: number;
  foodId: number;
  servings: number;
}

export interface FoodDTO {
  id: number;
  name: string;
  calories: number;
  amount: number;
  unit: MeasurementUnit;
  servings: number;
}

export interface UserExerciseDTO {
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

export interface WeighIn {
  userId: number;
  weighInId: number;
  date: string;
  height: number;
  weight: number;
  notes?: string;
}
