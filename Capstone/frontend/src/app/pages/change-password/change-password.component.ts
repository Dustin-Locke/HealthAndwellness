import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { PasswordApi, PasswordUpdateDTO } from '@core/services/password.api';
import {firstValueFrom} from 'rxjs';
import {MatCard} from '@angular/material/card';


@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCard
  ],
  templateUrl: './change-password.component.html',
  styleUrls: ['../profile/profile.component.css']
})
export class ChangePasswordComponent {
  form: FormGroup;
  isSubmitting = false;
  errorMsg = '';
  successMsg = '';

  constructor(
    private fb: FormBuilder,
    private api: PasswordApi,
    private router: Router
  ) {
    this.form = this.fb.group({
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirm: ['', [Validators.required]]
    });
  }

  get newPassword() { return this.form.get('newPassword'); }
  get confirm() { return this.form.get('confirm'); }

  async submit() {
    this.errorMsg = '';
    this.successMsg = '';
    this.isSubmitting = true;

    try {
      if (this.newPassword?.value !== this.confirm?.value) {
        this.errorMsg = 'Passwords do not match.';
        return;
      }

      const dto: PasswordUpdateDTO = {
        newPassword: this.newPassword?.value
      };

      await firstValueFrom(this.api.updatePassword(dto));
      this.successMsg = 'Password updated successfully!';

      // optional redirect
      setTimeout(() => this.router.navigate(['/profile']), 1200);

    } catch (err: any) {
      console.error(err);
      this.errorMsg = err?.error?.message || 'Could not update password.';
    } finally {
      this.isSubmitting = false;
    }
  }

  cancel() {
    this.router.navigate(['/profile']);
  }
}
