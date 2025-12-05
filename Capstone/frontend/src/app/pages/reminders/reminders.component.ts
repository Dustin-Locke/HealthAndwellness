import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ReminderService, ReminderDTO } from '../../core/services/reminder.service';

@Component({
  selector: 'app-reminders',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonToggleModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
  ],
  templateUrl: './reminders.component.html',
  styleUrls: ['./reminders.component.css'],
})
export class RemindersComponent implements OnInit {
  filter = signal<'All' | 'Active' | 'Done'>('All');
  reminders = signal<ReminderDTO[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);
  newForm!: FormGroup;

  constructor(private fb: FormBuilder, private reminderService: ReminderService) {}

  // Get user ID from localStorage (same pattern as other components)
  private get userId(): number {
    const userIdStr = localStorage.getItem('hw_user_id');
    if (!userIdStr || userIdStr === '0') {
      console.warn('User ID not found in localStorage');
      return 0;
    }
    return Number(userIdStr);
  }

  ngOnInit(): void {
    this.newForm = this.fb.group({
      title: ['', Validators.required],
      time: ['', Validators.required],
      recurrence: ['ONCE', Validators.required], // Changed default to match backend enum
      type: ['WORKOUT', Validators.required],
    });

    this.loadReminders();
  }

  loadReminders(): void {
    if (this.userId === 0) {
      this.error.set('Please log in to view reminders');
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    this.reminderService.getAllForUser(this.userId).subscribe({
      next: (res) => {
        console.log('Loaded reminders:', res);
        this.reminders.set(res);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load reminders:', err);
        this.error.set('Failed to load reminders. Please try again.');
        this.loading.set(false);
      },
    });
  }

  filtered = computed(() => {
    const f = this.filter();
    if (f === 'Active') return this.reminders().filter((r) => r.enabled);
    if (f === 'Done') return this.reminders().filter((r) => !r.enabled);
    return this.reminders();
  });

  addReminder(): void {
    if (this.newForm.invalid) {
      console.warn('Form is invalid:', this.newForm.errors);
      return;
    }

    if (this.userId === 0) {
      this.error.set('Please log in to add reminders');
      return;
    }

    const v = this.newForm.value;
    console.log('Form values:', v);

    const dto: ReminderDTO = {
      userId: this.userId,
      type: v.type,
      enabled: true,
      frequency: v.recurrence, // Already uppercase from select options
      notifyTime: this.convertTo24Hour(v.time),
      notifyDate: new Date().toISOString().split('T')[0],
      message: '', // Will be set by backend based on type
      title: v.title,
    };

    console.log('Sending reminder DTO:', dto);

    this.reminderService.addReminder(dto).subscribe({
      next: (res) => {
        console.log('Reminder saved:', res);
        this.reminders.update((list) => [...list, res]);
        this.newForm.reset({ title: '', time: '', recurrence: 'ONCE', type: 'WORKOUT' });
        this.error.set(null);
      },
      error: (err) => {
        console.error('Failed to save reminder:', err);
        this.error.set('Failed to save reminder. Please check your input.');
      },
    });
  }

  toggle(r: ReminderDTO): void {
    const updated = { ...r, enabled: !r.enabled };
    this.reminderService.updateReminder(r.id!, updated).subscribe({
      next: (res) => {
        console.log('Reminder toggled:', res);
        this.reminders.update((list) => list.map((x) => (x.id === r.id ? res : x)));
      },
      error: (err) => console.error('Failed to update reminder:', err),
    });
  }

  remove(r: ReminderDTO): void {
    if (!confirm('Are you sure you want to delete this reminder?')) {
      return;
    }

    this.reminderService.deleteReminder(r.id!).subscribe({
      next: () => {
        console.log('Reminder deleted:', r.id);
        this.reminders.update((list) => list.filter((x) => x.id !== r.id));
      },
      error: (err) => console.error('Failed to delete reminder:', err),
    });
  }

  convertTo24Hour(time: string): string {
    if (!time) return '12:00';

    // Check if already in 24-hour format (e.g., "14:30")
    if (
      !time.includes('AM') &&
      !time.includes('PM') &&
      !time.includes('am') &&
      !time.includes('pm')
    ) {
      // Assume it's already 24-hour format or just return as-is
      return time;
    }

    const upperTime = time.toUpperCase();
    const [raw, modifier] = upperTime.split(' ');
    let [hours, minutes] = raw.split(':').map(Number);

    if (isNaN(hours) || isNaN(minutes)) {
      console.warn('Invalid time format:', time);
      return '12:00';
    }

    if (modifier === 'PM' && hours < 12) hours += 12;
    if (modifier === 'AM' && hours === 12) hours = 0;

    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`;
  }

  formatTime12Hour(time24: string): string {
    if (!time24) return '';

    // Handle if time24 is already an object or has seconds
    const timePart = time24.toString().split(':').slice(0, 2).join(':');
    let [hours, minutes] = timePart.split(':').map(Number);

    if (isNaN(hours) || isNaN(minutes)) return time24;

    const ampm = hours >= 12 ? 'PM' : 'AM';
    hours = hours % 12 || 12;
    return `${hours}:${minutes.toString().padStart(2, '0')} ${ampm}`;
  }

  // Helper to get icon based on reminder type
  getTypeIcon(type: string): string {
    switch (type) {
      case 'WORKOUT':
        return 'fitness_center';
      case 'REST_DAY':
        return 'hotel';
      case 'DRINK_WATER':
        return 'local_drink';
      case 'WEIGH_IN':
        return 'monitor_weight';
      case 'MEAL_LOG':
        return 'restaurant';
      case 'BEDTIME':
        return 'bedtime';
      default:
        return 'notifications';
    }
  }

  // Helper to format frequency for display
  formatFrequency(frequency: string): string {
    switch (frequency) {
      case 'DAILY':
        return 'Daily';
      case 'WEEKLY':
        return 'Weekly';
      case 'ONCE':
        return 'One-time';
      case 'WEEKDAYS':
        return 'Weekdays';
      case 'WEEKENDS':
        return 'Weekends';
      default:
        return frequency;
    }
  }
}
