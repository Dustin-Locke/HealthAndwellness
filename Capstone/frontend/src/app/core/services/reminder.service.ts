import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ReminderDTO {
  id?: number;
  userId: number;
  type: 'WORKOUT' | 'REST_DAY' | 'DRINK_WATER' | 'WEIGH_IN' | 'MEAL_LOG' | 'BEDTIME';
  enabled: boolean;
  frequency: 'ONCE' | 'DAILY' | 'WEEKLY' | 'WEEKDAYS' | 'WEEKENDS' | 'MONTHLY' | 'YEARLY';
  notifyTime: string; // e.g., "18:30"
  notifyDate?: string;
  lastNotified?: string;
  message: string;
  title?: string;
}

@Injectable({ providedIn: 'root' })
export class ReminderService {
  private base = `${environment.apiUrl}/api/reminders`;

  constructor(private http: HttpClient) {}

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

  getAllForUser(userId: number): Observable<ReminderDTO[]> {
    return this.http.get<ReminderDTO[]>(`${this.base}/user/${userId}`, this.getAuthHeaders());
  }

  getEnabledForUser(userId: number): Observable<ReminderDTO[]> {
    return this.http.get<ReminderDTO[]>(
      `${this.base}/user/${userId}/enabled`,
      this.getAuthHeaders()
    );
  }

  getById(id: number): Observable<ReminderDTO> {
    return this.http.get<ReminderDTO>(`${this.base}/${id}`, this.getAuthHeaders());
  }

  addReminder(reminder: ReminderDTO): Observable<ReminderDTO> {
    return this.http.post<ReminderDTO>(`${this.base}`, reminder, this.getAuthHeaders());
  }

  updateReminder(id: number, reminder: ReminderDTO): Observable<ReminderDTO> {
    return this.http.put<ReminderDTO>(`${this.base}/${id}`, reminder, this.getAuthHeaders());
  }

  deleteReminder(id: number): Observable<any> {
    return this.http.delete(`${this.base}/${id}`, this.getAuthHeaders());
  }

  markNotified(id: number, date: string): Observable<ReminderDTO> {
    return this.http.post<ReminderDTO>(
      `${this.base}/${id}/notified?date=${date}`,
      {},
      this.getAuthHeaders()
    );
  }
}
