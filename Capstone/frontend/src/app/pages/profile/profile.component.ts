import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ProfileApi, UserDTO } from '@core/services/profile.api';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatButtonModule } from '@angular/material/button';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatDividerModule,
  ],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
  private api = inject(ProfileApi);
  private fb = inject(FormBuilder);
  private router = inject(Router);

  loading = signal(true);
  error = signal<string | null>(null);

  form!: FormGroup;

  ngOnInit() {
    this.form = this.fb.group({
      firstName: [{ value: '', disabled: true }],
      lastName: [{ value: '', disabled: true }],
      dateOfBirth: [{ value: '', disabled: true }],
      email: [{ value: '', disabled: true }],
      heightFt: [0, [Validators.min(0)]],
      heightIn: [0, [Validators.min(0), Validators.max(11)]],
      initialWeight: [{ value: 0, disabled: true }], // Read-only - starting weight
      weight: [{ value: 0, disabled: true }], // Read-only - update via weigh-in log only
      goalWeight: [0, [Validators.min(0)]],
      measurementSystem: ['IMPERIAL'],
    });

    this.loadProfile();
  }

  private toFeetInches(totalInches: number) {
    const inches = totalInches || 0;
    const ft = Math.floor(inches / 12);
    const inch = Math.round(inches - ft * 12);
    return { ft, inch };
  }

  private toInches(ft: number, inch: number) {
    return (Number(ft) || 0) * 12 + (Number(inch) || 0);
  }

  async loadProfile() {
    this.loading.set(true);
    this.error.set(null);

    try {
      const p = await firstValueFrom(this.api.getMe());
      if (!p) throw new Error('No profile found');

      console.log('👤 Profile loaded:', p);
      console.log('   - initialWeight:', p.initialWeight);
      console.log('   - weight:', p.weight);
      console.log('   - goalWeight:', p.goalWeight);

      const h = this.toFeetInches(p.height ?? 0);

      // If weight is 0 but initialWeight exists, use initialWeight as current weight
      const currentWeight = (p.weight ?? 0) || (p.initialWeight ?? 0);

      this.form.patchValue({
        firstName: p.firstName ?? '',
        lastName: p.lastName ?? '',
        dateOfBirth: (p.dateOfBirth ?? '').substring(0, 10),
        email: p.email ?? '',
        heightFt: h.ft,
        heightIn: h.inch,
        initialWeight: p.initialWeight ?? 0,
        goalWeight: p.goalWeight ?? 0,
        weight: currentWeight,
        MeasurementSystem: p.MeasurementSystem ?? 'IMPERIAL',
      });
    } catch (e) {
      console.error('Error loading profile', e);
      this.error.set('Failed to load profile');
    } finally {
      this.loading.set(false);
    }
  }

  async save() {
    if (this.form.invalid) return;

    const v = this.form.getRawValue();

    const payload: UserDTO = {
      firstName: v.firstName,
      lastName: v.lastName,
      email: v.email,
      dateOfBirth: v.dateOfBirth,
      age: this.calculateAge(v.dateOfBirth),
      height: this.toInches(v.heightFt, v.heightIn),
      initialWeight: Number(v.initialWeight ?? 0),
      goalWeight: Number(v.goalWeight ?? 0),
      weight: Number(v.weight ?? 0),
      MeasurementSystem: v.measurementSystem,
    };

    try {
      await firstValueFrom(this.api.update(payload));
      this.router.navigateByUrl('/');
    } catch (e) {
      console.error('Error saving profile', e);
      this.error.set('Could not save changes');
    }
  }

  cancel() {
    this.router.navigateByUrl('/');
  }

  private calculateAge(dob: string): number {
    if (!dob) return 0;

    const birth = new Date(dob);
    const today = new Date();
    let age = today.getFullYear() - birth.getFullYear();

    const m = today.getMonth() - birth.getMonth();
    if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) {
      age--;
    }

    return age;
  }

  changePassword() {
    this.router.navigate(['/change-password']);
  }

}
