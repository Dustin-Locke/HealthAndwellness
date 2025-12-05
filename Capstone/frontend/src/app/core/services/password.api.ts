import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'environments/environment';
import { Observable } from 'rxjs';

export interface PasswordUpdateDTO {
  newPassword: string;
}

@Injectable({ providedIn: 'root' })
export class PasswordApi {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/me/password`;

  private getHeaders() {
    const token = localStorage.getItem('hw_jwt_token');
    return {
      headers: new HttpHeaders({
        Authorization: token ? `Bearer ${token}` : '',
      }),
    };
  }

  updatePassword(dto: PasswordUpdateDTO) {
    return this.http.put(`${this.base}`, dto, {
      ...this.getHeaders(),
      responseType: 'text' as const
    });
  }

}
