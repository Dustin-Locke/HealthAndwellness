import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  FormArray,
  ReactiveFormsModule,
  Validators,
  AbstractControl,
} from '@angular/forms';
import { MatTabsModule } from '@angular/material/tabs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import {
  LogApi,
  CreateUserExerciseRequest,
  ExerciseIntensity,
  CreateWeighInRequest,
} from '@app/core/services/log.api';
import { forkJoin, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { MeasurementUnit } from '@app/core/services/log.api';

@Component({
  selector: 'app-log',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatTabsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatAutocompleteModule,
  ],
  templateUrl: './log.component.html',
  styleUrls: ['./log.component.css'],
})
export class LogComponent implements OnInit {
  private fb = inject(FormBuilder);
  private logApi = inject(LogApi);

  mealForm!: FormGroup;
  exerciseForm!: FormGroup;
  weighInForm!: FormGroup;

  foodSuggestions: { [index: number]: any[] } = {};
  searchingFood: { [index: number]: boolean } = {};

  availableExercises: any[] = [];
  loadingExercises = false;

  savingExercise = false;
  savingMeal = false;
  savingWeighIn = false;

  private get userId(): number {
    const userIdStr = localStorage.getItem('hw_user_id');
    if (!userIdStr || userIdStr === '0') {
      console.warn('User ID not found in localStorage');
      return 1;
    }
    return Number(userIdStr);
  }

  ngOnInit() {
    console.log('Log component initialized');
    console.log('Current user ID:', this.userId);

    this.loadExercises();
    this.initializeMealForm();
    this.initializeExerciseForm();
    this.initializeWeighInForm();
  }

  private initializeMealForm() {
    this.mealForm = this.fb.group({
      date: [new Date(), Validators.required],
      type: ['BREAKFAST', Validators.required],
      items: this.fb.array([] as FormGroup[]),
    });
    this.mealAddBlankRow();
  }

  private initializeExerciseForm() {
    this.exerciseForm = this.fb.group({
      date: [new Date(), Validators.required],
      items: this.fb.array([] as FormGroup[]),
    });
    this.exerciseAddBlankRow();
  }

  private initializeWeighInForm() {
    this.weighInForm = this.fb.group({
      date: [new Date(), Validators.required],
      weight: [null, [Validators.required, Validators.min(1)]],
      notes: [''],
    });
  }

  private loadExercises() {
    this.loadingExercises = true;
    this.logApi.getAllExercises().subscribe({
      next: (exercises) => {
        console.log('Loaded exercises:', exercises);
        this.availableExercises = exercises;
        this.loadingExercises = false;
      },
      error: (err) => {
        console.error('Error loading exercises:', err);
        this.loadingExercises = false;
        alert('Failed to load exercises. Please refresh the page.');
      },
    });
  }

  private uuid(): string {
    if (typeof crypto !== 'undefined' && 'randomUUID' in crypto) {
      return (crypto as any).randomUUID();
    }
    return 'uid-' + Math.random().toString(36).slice(2) + Date.now().toString(36);
  }

  trackByUid = (_: number, ctrl: AbstractControl) => ctrl.get('uid')?.value;

  private formatDate(date: Date | string): string {
    // If it's already a string in YYYY-MM-DD format, return it
    if (typeof date === 'string') {
      // Check if it matches YYYY-MM-DD format
      if (/^\d{4}-\d{2}-\d{2}$/.test(date)) {
        console.log('Date already formatted:', date);
        return date;
      }
      // Otherwise try to parse it
      date = new Date(date);
    }

    if (!(date instanceof Date) || isNaN(date.getTime())) {
      console.error('Invalid date:', date);
      // Fallback to today
      date = new Date();
    }

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    const formatted = `${year}-${month}-${day}`;
    console.log('Formatted date:', date, '→', formatted);

    return formatted;
  }

  get mealItems(): FormArray<FormGroup> {
    return this.mealForm.get('items') as FormArray<FormGroup>;
  }

  private mealBuildRow(
    initial?: Partial<{
      food: string;
      amount: number | null;
      unit: MeasurementUnit | null;
      servings: number | null;
      kcal: number | null;
    }>
  ) {
    return this.fb.group({
      uid: [this.uuid()],
      food: [initial?.food ?? '', [Validators.required]],
      amount: [initial?.amount ?? null, [Validators.required, Validators.min(0.000001)]],
      unit: [initial?.unit ?? 'GRAM', [Validators.required]],
      servings: [initial?.servings ?? 1, [Validators.required, Validators.min(0.000001)]],
      kcal: [initial?.kcal ?? null, [Validators.required, Validators.min(0.000001)]],
    });
  }

  private mealAddBlankRow() {
    this.mealItems.push(this.mealBuildRow());
  }

  addMealRowClick() {
    this.mealAddBlankRow();
  }

  removeMealRow(index: number) {
    if (this.mealItems.length > 1) {
      this.mealItems.removeAt(index);
      delete this.foodSuggestions[index];
      delete this.searchingFood[index];
    } else {
      this.mealItems.at(0).reset({
        uid: this.uuid(),
        food: '',
        amount: null,
        unit: 'GRAM',
        servings: 1,
        kcal: null,
      });
      this.foodSuggestions[0] = [];
      this.searchingFood[0] = false;
    }
  }

  private mealMarkAllTouched() {
    this.mealItems.controls.forEach((g) => (g as FormGroup).markAllAsTouched());
  }

  private mealValidRows(): any[] {
    return (this.mealItems.controls as FormGroup[]).filter((g) => g.valid).map((g) => g.value);
  }

  private resetMealFormToDefaults() {
    this.mealForm.patchValue({
      date: new Date(),
      type: 'BREAKFAST',
    });
    const arr = this.fb.array([] as FormGroup[]);
    arr.push(this.mealBuildRow());
    this.mealForm.setControl('items', arr);

    this.foodSuggestions = {};
    this.searchingFood = {};
  }

  onFoodSearch(rowIndex: number, event: Event): void {
    const input = event.target as HTMLInputElement;
    const searchTerm = input.value?.trim();

    console.log('Food search for row', rowIndex, ':', searchTerm);

    if (!searchTerm || searchTerm.length < 2) {
      this.foodSuggestions[rowIndex] = [];
      return;
    }

    this.searchingFood[rowIndex] = true;

    this.logApi.searchFoodByName(searchTerm).subscribe({
      next: (foods) => {
        console.log('Found foods:', foods);
        this.foodSuggestions[rowIndex] = foods;
        this.searchingFood[rowIndex] = false;
      },
      error: (err) => {
        console.error('Error searching foods:', err);
        this.foodSuggestions[rowIndex] = [];
        this.searchingFood[rowIndex] = false;
      },
    });
  }

  onFoodSelected(rowIndex: number, event: any): void {
    const selectedFood = event.option.value;
    console.log('Food selected for row', rowIndex, ':', selectedFood);

    const row = this.mealItems.at(rowIndex) as FormGroup;

    row.patchValue({
      food: selectedFood.name,
      kcal: selectedFood.calories,
      amount: selectedFood.amount,
      unit: selectedFood.unit,
      servings: selectedFood.servings ?? 1,
    });

    console.log('Auto-filled row:', row.value);
  }

  displayFoodName(food: any): string {
    if (!food) return '';
    if (typeof food === 'string') return food;
    if (food && typeof food === 'object' && 'name' in food) {
      return food.name;
    }
    return '';
  }

  saveMeal() {
    console.log('Attempting to save meal...');
    this.mealMarkAllTouched();

    const validRows = this.mealValidRows();
    if (this.mealItems.controls.some((g) => (g as FormGroup).invalid) || validRows.length === 0) {
      alert('Please complete all meal fields (Food, Amount, Unit, kcal) with valid numbers.');
      return;
    }

    const mealDate = this.formatDate(this.mealForm.value.date);
    const mealType = this.mealForm.value.type;

    console.log('Meal details:', { date: mealDate, type: mealType, items: validRows });

    this.savingMeal = true;

    this.logApi
      .createMeal({
        userId: this.userId,
        type: mealType,
        date: mealDate,
      })
      .subscribe({
        next: (mealResponse) => {
          console.log('Meal created:', mealResponse);
          const mealId = mealResponse.mealId;
          this.processMealFoods(mealId, validRows);
        },
        error: (err) => {
          console.error('Error creating meal:', err);
          this.savingMeal = false;
          alert('Failed to create meal. Check console for details.');
        },
      });
  }

  private processMealFoods(mealId: number, foodItems: any[]) {
    console.log('Processing foods for meal:', mealId, foodItems);

    const linkRequests = foodItems.map((item) => {
      const foodName = typeof item.food === 'string' ? item.food : item.food.name;

      return this.logApi.searchFoodByName(foodName).pipe(
        switchMap((foods) => {
          const exactMatch = foods.find((f) => f.name.toLowerCase() === foodName.toLowerCase());

          if (exactMatch) {
            console.log('Found existing food:', exactMatch);
            return of(exactMatch);
          } else {
            console.log('Creating new food:', foodName);
            return this.logApi.createFood({
              name: foodName,
              calories: item.kcal,
              amount: item.amount,
              unit: item.unit,
              servings: item.servings ?? 1,
            });
          }
        }),
        switchMap((food) => {
          console.log('Linking food to meal:', {
            mealId,
            foodId: food.id,
            servings: item.unit,
          });
          return this.logApi.addFoodToMeal({
            mealId: mealId,
            foodId: food.id,
            servings: item.servings ?? 1,
          });
        })
      );
    });

    forkJoin(linkRequests).subscribe({
      next: (results) => {
        console.log('All foods linked to meal:', results);
        this.savingMeal = false;
        this.resetMealFormToDefaults();
        alert(`Meal with ${results.length} food item(s) logged successfully!`);
      },
      error: (err) => {
        console.error('Error processing meal foods:', err);
        this.savingMeal = false;
        alert('Meal created but failed to add some foods. Check console.');
      },
    });
  }

  get exerciseItems(): FormArray<FormGroup> {
    return this.exerciseForm.get('items') as FormArray<FormGroup>;
  }

  private exerciseBuildRow(
    initial?: Partial<{
      exerciseId: number | null;
      durationMinutes: number | null;
      reps: number | null;
      sets: number | null;
      intensity: string;
    }>
  ) {
    return this.fb.group({
      uid: [this.uuid()],
      exerciseId: [initial?.exerciseId ?? null, [Validators.required]],
      durationMinutes: [initial?.durationMinutes ?? null, [Validators.min(0.1)]],
      reps: [initial?.reps ?? null, [Validators.min(1)]],
      sets: [initial?.sets ?? null, [Validators.min(1)]],
      intensity: [initial?.intensity ?? 'MODERATE', [Validators.required]],
    });
  }

  private exerciseAddBlankRow() {
    this.exerciseItems.push(this.exerciseBuildRow());
  }

  addExerciseRowClick() {
    this.exerciseAddBlankRow();
  }

  removeExerciseRow(index: number) {
    if (this.exerciseItems.length > 1) {
      this.exerciseItems.removeAt(index);
    } else {
      this.exerciseItems.at(0).reset({
        uid: this.uuid(),
        exerciseId: null,
        durationMinutes: null,
        reps: null,
        sets: null,
        intensity: 'MODERATE',
      });
    }
  }

  private exerciseMarkAllTouched() {
    this.exerciseItems.controls.forEach((g) => (g as FormGroup).markAllAsTouched());
  }

  private exerciseValidRows(): any[] {
    return (this.exerciseItems.controls as FormGroup[])
      .filter((g) => g.valid && g.value.exerciseId)
      .map((g) => g.value);
  }

  private resetExerciseFormToDefaults() {
    this.exerciseForm.patchValue({
      date: new Date(),
    });
    const arr = this.fb.array([] as FormGroup[]);
    arr.push(this.exerciseBuildRow());
    this.exerciseForm.setControl('items', arr);
  }

  saveExercise() {
    console.log('Attempting to save exercises...');
    this.exerciseMarkAllTouched();

    const validRows = this.exerciseValidRows();
    console.log('Valid exercise rows:', validRows);

    if (
      this.exerciseItems.controls.some((g) => (g as FormGroup).invalid) ||
      validRows.length === 0
    ) {
      alert('Please select an exercise and enter either duration OR reps/sets.');
      return;
    }

    const hasValidAmount = validRows.every((row) => {
      const hasDuration = row.durationMinutes && row.durationMinutes > 0;
      const hasRepsAndSets = row.reps && row.reps > 0 && row.sets && row.sets > 0;
      return hasDuration || hasRepsAndSets;
    });

    if (!hasValidAmount) {
      alert('Each exercise must have either duration (minutes) OR reps and sets.');
      return;
    }

    const exerciseDate = this.formatDate(this.exerciseForm.value.date);
    const requests: CreateUserExerciseRequest[] = validRows.map((row) => {
      const exercise = this.availableExercises.find((ex) => ex.id === row.exerciseId);

      if (!exercise) {
        console.error('Exercise not found:', row.exerciseId);
        throw new Error('Exercise not found');
      }

      return {
        userId: this.userId,
        exerciseId: exercise.id,
        date: exerciseDate,
        durationMinutes: row.durationMinutes || null,
        reps: row.reps || null,
        sets: row.sets || null,
        intensity: row.intensity as ExerciseIntensity,
        caloriesBurned: null,
        complete: true,
      };
    });

    console.log('Sending exercise requests:', requests);

    this.savingExercise = true;

    const saveRequests = requests.map((req) => this.logApi.createUserExercise(req));

    forkJoin(saveRequests).subscribe({
      next: (results) => {
        console.log('Exercises saved successfully:', results);
        this.savingExercise = false;
        this.resetExerciseFormToDefaults();
        alert(`${results.length} exercise(s) logged successfully!`);
      },
      error: (err) => {
        console.error('Error saving exercises:', err);
        this.savingExercise = false;

        if (err.status === 400) {
          alert('Invalid data. Please check all fields and try again.');
        } else if (err.status === 404) {
          alert('Exercise or user not found. Please refresh and try again.');
        } else if (err.status === 500) {
          alert('Server error. Please check the backend logs.');
        } else {
          alert('Failed to log exercises. Check console for details.');
        }
      },
    });
  }

  saveWeighIn() {
    console.log('Attempting to save weigh-in...');

    if (this.weighInForm.invalid) {
      alert('Please enter a valid weight (must be greater than 0).');
      this.weighInForm.markAllAsTouched();
      return;
    }

    const formValue = this.weighInForm.value;

    const payload: CreateWeighInRequest = {
      userId: this.userId,
      date: this.formatDate(formValue.date),
      weight: formValue.weight,
      notes: formValue.notes || null,
      measurement: 'IMPERIAL',
    };

    console.log('Sending weigh-in request:', payload);

    this.savingWeighIn = true;

    this.logApi.createWeighIn(payload).subscribe({
      next: (result) => {
        console.log('Weigh-in saved successfully:', result);
        this.savingWeighIn = false;

        this.weighInForm.reset({
          date: new Date(),
          weight: null,
          notes: '',
        });

        alert('Weigh-in logged successfully!');
      },
      error: (err) => {
        console.error('Error saving weigh-in:', err);
        this.savingWeighIn = false;

        if (err.status === 400) {
          alert('Invalid data. Please check all fields and try again.');
        } else if (err.status === 500) {
          alert('Server error. Please check the backend logs.');
        } else {
          alert('Failed to log weigh-in. Check console for details.');
        }
      },
    });
  }
}
