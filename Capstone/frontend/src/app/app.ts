import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterOutlet } from '@angular/router';
import { AuthService } from './core/services/auth.services';
import { HeaderComponent } from './shared/components/header/header.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterOutlet, HeaderComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.css'],
})
export class AppComponent {
  constructor(public auth: AuthService) {}

  onLogout() {
    this.auth.logout();
  }
}
