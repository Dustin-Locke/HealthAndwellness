import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

interface LoginRes {
  ok: boolean;
  message: string;
  userId: number;
  firstName: string;
  jwt: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private tokenKey = 'hw_jwt_token';
  private nameKey = 'hw_name';
  private userIdKey = 'hw_user_id';
  private base = `${environment.apiUrl}/api/auth`;

  constructor(private http: HttpClient) {}

  login(email: string, password: string) {
    return this.http.post<LoginRes>(`${this.base}/login`, { email, password }).pipe(
      tap((r) => {
        if (r?.ok && r.jwt) {
          localStorage.setItem(this.tokenKey, r.jwt);
          localStorage.setItem(this.nameKey, r.firstName || '');
          localStorage.setItem(this.userIdKey, r.userId.toString());
          console.log('User logged in. ID:', r.userId);
        }
      })
    );
  }

  register(user: any) {
    return this.http.post(`${this.base}/register`, user).pipe(
      tap((r: any) => {
        if (r?.ok && r.jwt) {
          localStorage.setItem(this.tokenKey, r.jwt);
          localStorage.setItem(this.nameKey, r.firstName || '');
          localStorage.setItem(this.userIdKey, r.userId?.toString() || '0');
          console.log('User registered. ID:', r.userId);
        }
      })
    );
  }

  preRegister(payload: any) {
    return this.http.post(`${this.base}/pre-register`, payload);
  }

  verifyCode(payload: { email: string, code: string }) {
    return this.http.post(`${this.base}/verify-code`, payload);
  }

  verifyResetCode(payload: { email: string; code: string }) {
    return this.http.post(`${this.base}/verify-reset-code`, payload);
  }

  resetPassword(payload: { email: string, password: string }) {
    return this.http.post(`${this.base}/reset-password`, payload);
  }

  // ⭐ NEW: Complete registration after user enters verification code
  completeRegistration(payload: { email: string }) {
    return this.http.post(`${this.base}/complete-registration`, payload).pipe(
      tap((r: any) => {
        if (r?.ok && r.jwt) {
          localStorage.setItem(this.tokenKey, r.jwt);
          localStorage.setItem(this.nameKey, r.firstName || '');
          localStorage.setItem(this.userIdKey, r.userId?.toString() || '0');
        }
      })
    );
  }

  forgotPassword(email: string) {
    return this.http.post(`${this.base}/forgot-password`, { email });
  }


  get isLoggedIn(): boolean {
    return !!localStorage.getItem(this.tokenKey);
  }

  getUserId(): number | null {
    const id = localStorage.getItem(this.userIdKey);
    return id ? parseInt(id, 10) : null;
  }

  logout() {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.nameKey);
    localStorage.removeItem(this.userIdKey);
  }
}
