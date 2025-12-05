import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'environments/environment';
import { Router } from '@angular/router';
// import { tap, catchError } from 'rxjs/operators';
// import { throwError } from 'rxjs';
// import {constructor} from 'jasmine';
import {AuthService} from '@core/services/auth.services';

export interface UserDTO {
  firstName: string;
  lastName: string;
  email: string;
  dateOfBirth: string;
  age: number;
  initialWeight: number;
  weight: number;
  goalWeight: number;
  height: number;
  MeasurementSystem: string;
}




@Injectable({ providedIn: 'root' })
export class ProfileApi {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/api/profile`;

  constructor( private router: Router, private authService: AuthService ) {}

  private getHeaders() {
    const token = localStorage.getItem('hw_jwt_token');
    return {
      headers: new HttpHeaders({
        Authorization: token ? `Bearer ${token}` : '',
      }),
    };
  }

  getMe() {
    return this.http.get<UserDTO>(`${this.base}/me`, this.getHeaders());
  }

  update(body: UserDTO) {
    return this.http.put<void>(this.base, body, this.getHeaders());
  }


}
