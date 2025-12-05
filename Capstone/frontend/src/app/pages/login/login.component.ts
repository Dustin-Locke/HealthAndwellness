import {Component, inject, ViewChild, ElementRef, AfterViewChecked} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '@core/services/auth.services';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCard } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import {lastValueFrom} from 'rxjs';
import { ModalComponent } from '@shared/modal/modal.component';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MatInputModule,
    MatButtonModule,
    MatCard,
    MatIcon,
    ModalComponent,
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements AfterViewChecked{
  @ViewChild('fpCodeInput') fpCodeInput!: ElementRef<HTMLInputElement>;
  @ViewChild('fpEmailInput') fpEmailInput!: ElementRef<HTMLInputElement>;


  private auth = inject(AuthService);
  private router = inject(Router);
  email = '';
  password = '';
  err = '';

  // Email verification modal state
  showForgotModal = false;
  fpStep = 1; // 1 = edit email, 2 = enter code
  fpEmail = '';
  fpCode = '';
  fpError = '';
  private shouldFocusCodeInput = false;
  private shouldFocusEmailInput = false;

  // Reset password modal state
  showResetModal = false;
  newPassword = '';
  confirmPassword = '';
  resetError = '';

  passwordResetSuccess = '';


  ngAfterViewChecked(): void {
    if (this.shouldFocusCodeInput && this.fpCodeInput) {
      this.fpCodeInput.nativeElement.focus();
      this.shouldFocusCodeInput = false;
    }

    if (this.shouldFocusEmailInput && this.fpEmailInput) {
      this.fpEmailInput.nativeElement.focus();
      this.shouldFocusEmailInput = false;
    }
  }

  submit() {
    this.err = '';

    this.auth.login(this.email.trim(), this.password).subscribe({
      next: (r) => {
        if (r?.ok) {
          this.router.navigateByUrl('/');
        } else {
          this.err = 'Invalid credentials';
        }
      },
      error: (err) => {
        console.error('[DEBUG] Login error:', err?.error?.message);
        this.err = err?.error?.message;
      }
    });
  }

  async forgotPassword(event: Event) {
    event.preventDefault(); // prevent link from navigating
    this.fpError = '';

    // Open modal first — it will prefill email if available
    this.openForgotModal();

    // If the email is already filled in from the login form, send the code automatically
    if (this.fpStep === 2 && this.fpEmail) {
      try {
        await lastValueFrom(this.auth.forgotPassword(this.fpEmail));
      } catch (err: any) {
        console.error('Forgot password error:', err);
        if (err.status === 404 || err?.error?.message?.toLowerCase().includes('not found')) {
          this.fpError = 'Verification code could not be sent.';
        } else {
          this.fpError = err?.error?.message || 'Could not send verification code';
        }
      }
    }
  }


  openForgotModal() {
    this.showForgotModal = true;
    this.passwordResetSuccess = '';
    this.resetError = '';

    // Prefill email if the user entered it on the login form
    if (this.email?.trim()) {
      this.fpEmail = this.email.trim().toLowerCase();
      this.fpStep = 2; // skip step 1, go directly to code entry
    } else {
      this.fpEmail = '';
      this.fpStep = 1; // user needs to enter email
    }

    this.fpCode = '';
    this.fpError = '';
  }


  closeForgotModal() {
    this.showForgotModal = false;
    this.fpStep = 1;
    this.fpCode = '';
  }

  async sendFpCode() {
    if (!this.fpEmail.trim()) {
      this.fpError = 'Please enter your email.';
      return;
    }

    try {
      await lastValueFrom(this.auth.forgotPassword(this.fpEmail.trim().toLowerCase()));
      this.fpStep = 2;

      // Focus code input
      setTimeout(() => this.fpCodeInput?.nativeElement.focus(), 50);

    } catch (err: any) {
      console.error('Send code error:', this.fpError);
      this.fpError = err?.error?.message || 'Could not send verification code';
    }
  }

  async verifyFpCode() {
    try {
      const payload = { email: this.fpEmail, code: this.fpCode };
      await lastValueFrom(this.auth.verifyResetCode(payload));

      this.fpStep = 3;
      this.newPassword = '';
      this.confirmPassword = '';
      this.fpError = '';

      console.log('Verification successful, ready for password reset');

      setTimeout(() => this.fpCodeInput?.nativeElement.focus(), 50);

    } catch (err: any) {
      console.error('Verification error:', err);
      this.fpError = err?.error?.message || 'Invalid verification code';
    }
  }

  async resendFpCode() {
    try {
      await lastValueFrom(this.auth.preRegister({ email: this.fpEmail }));
      setTimeout(() => this.fpCodeInput?.nativeElement.focus(), 50);
      console.log('Verification code resent');
      this.passwordResetSuccess = 'Verification code resent';
    } catch (err: any) {
      console.error('Resend code error:', err);
      this.fpError = err?.error?.message || 'Could not resend code';
    }
  }

  async submitNewPassword() {
    this.resetError = '';

    if (!this.newPassword || !this.confirmPassword) {
      this.resetError = 'Please fill out both fields.';
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.resetError = 'Passwords do not match.';
      return;
    }

    try {
      await lastValueFrom(this.auth.resetPassword({
        email: this.fpEmail,
        password: this.newPassword
      }));

      this.showResetModal = false;
      console.log('Password reset successfully');
      this.router.navigateByUrl('/login');
      this.passwordResetSuccess = 'Your password has been reset successfully. You may now log in.';

    } catch (err: any) {
      console.error('Reset password error:', err);
      this.resetError = err?.error?.message || 'Could not reset password';
    }

    this.closeForgotModal();
  }
}
