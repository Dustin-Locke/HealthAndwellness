import { Component, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { CommonModule, formatDate } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { lastValueFrom } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { AuthService } from '@app/core/services/auth.services';
import { ModalComponent } from '@shared/modal/modal.component';


@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDividerModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDatepickerModule,
    MatNativeDateModule,
    ModalComponent,
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent implements AfterViewChecked{
  @ViewChild('codeInput') verificationInput!: ElementRef<HTMLInputElement>;
  firstName = '';
  lastName = '';
  email = '';
  password = '';
  dob: Date | string | null = null;
  heightFt: number | null = null;
  heightIn: number | null = null;
  initialWeight: number | null = null;
  weight: number | null = null;
  goalWeight: number | null = null;

  maxDate = new Date();

  errorMsg = '';
  isSubmitting = false;

  showVerifyModal = false;
  step = 1; // 1 = edit email, 2 = enter code
  tempEmail = '';
  verificationCode = '';

  private shouldFocusCodeInput = false;

  // ⭐ Added to temporarily store registration payload until email verified
  pendingRegistrationPayload: any = null;

  constructor(private auth: AuthService, private router: Router) {}

  ngAfterViewChecked(): void {
    if (this.shouldFocusCodeInput && this.verificationInput) {
      this.verificationInput.nativeElement.focus();
      this.shouldFocusCodeInput = false;
    }
  }

  private normalizeDob(value: Date | string | null): Date | null {
    if (!value) return null;
    if (value instanceof Date) return isNaN(value.getTime()) ? null : value;
    if (typeof value === 'string') {
      const s = value.trim();
      if (!s) return null;
      const d = new Date(s);
      return isNaN(d.getTime()) ? null : d;
    }
    return null;
  }

  private toInches(ft: number | null, inch: number | null): number {
    return (Number(ft) || 0) * 12 + (Number(inch) || 0);
  }

  // ⭐ Confirm email simply resends code
  async confirmEmail() {
    try {
      await lastValueFrom(this.auth.preRegister(this.pendingRegistrationPayload));
      this.step = 2;
      this.showVerifyModal = true;

      this.shouldFocusCodeInput = true;
    } catch (e) {
      console.error('Error sending verification code:', e);
      this.errorMsg = 'Could not send verification code.';
    }
  }


  async verifyCode() {
    this.errorMsg = '';

    try {
      const payload = {
        email: this.tempEmail,
        code: this.verificationCode,
        ...this.pendingRegistrationPayload
      };

      // 1. Verify the code (optional now)
      await lastValueFrom(this.auth.verifyCode(payload));

      // 2. Complete registration and get result
      const res: any = await lastValueFrom(
        this.auth.completeRegistration(payload)
      );

      // 3. Store token + user info
      localStorage.setItem("token", res.jwt);
      localStorage.setItem("userId", res.userId);
      localStorage.setItem("firstName", res.firstName);

      // 4. Close modal & redirect
      this.closeModal();
      this.router.navigate(['/dashboard']);

    } catch (err: any) {
      this.errorMsg = err?.error?.message || 'Invalid verification code';
    }
  }


  async resendCode() {
    try {
      this.errorMsg = '';

      await lastValueFrom(
        this.auth.preRegister(this.pendingRegistrationPayload)
      );
      this.shouldFocusCodeInput = true;

      this.errorMsg = 'A new verification code has been sent to your email.';
      setTimeout(() => (this.errorMsg = ''), 4000);

    } catch (err) {
      console.error('Resend error:', err);
      this.errorMsg = 'Unable to resend code. Please try again.';
    }
  }


  closeModal() {
    this.showVerifyModal = false;
    this.step = 1;
    this.verificationCode = '';
    // Do NOT clear form values
  }

  async onSubmit() {
    this.errorMsg = '';
    this.isSubmitting = true;

    try {
      if (!this.firstName.trim() || !this.lastName.trim() || !this.email.trim() || !this.password) {
        this.errorMsg = 'First name, last name, email, and password are required.';
        return;
      }

      const dobDate = this.normalizeDob(this.dob);
      if (!dobDate) {
        this.errorMsg = 'Please enter a valid date of birth.';
        return;
      }

      const payload = {
        firstName: this.firstName.trim(),
        lastName: this.lastName.trim(),
        email: this.email.trim().toLowerCase(),
        password: this.password,
        dateOfBirth: formatDate(dobDate, 'yyyy-MM-dd', 'en-US'),
        height: this.toInches(this.heightFt, this.heightIn),
        initialWeight: Number(this.initialWeight ?? 0),
        weight: Number(this.initialWeight ?? 0),
        goalWeight: Number(this.goalWeight ?? 0),

      };

      // ⭐ Store payload temporarily until verification
      this.pendingRegistrationPayload = payload;
      this.tempEmail = payload.email;

      // Show Step 1 modal instead of sending code immediately
      this.step = 1;
      this.showVerifyModal = true;

    } catch (err: any) {
      console.error('Registration error:', err);
      const status = err?.status;
      const serverMsg =
        err?.error?.message ||
        err?.error?.error ||
        (typeof err?.error === 'string' ? err.error : '');

      if (status === 409)
        this.errorMsg = serverMsg || 'Email already exists. Try a different email.';
      else if (status === 400)
        this.errorMsg = serverMsg || 'Invalid input. Check DOB and required fields.';
      else this.errorMsg = serverMsg || 'Could not create account';
    } finally {
      this.isSubmitting = false;
    }
  }

  clear() {
    this.firstName = '';
    this.lastName = '';
    this.email = '';
    this.password = '';
    this.dob = null;
    this.heightFt = null;
    this.heightIn = null;
    this.initialWeight = null;
    this.goalWeight = null;
    this.errorMsg = '';
  }
}
