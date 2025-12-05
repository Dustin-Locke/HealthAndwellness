import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.services';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent {
  private router = inject(Router);
  constructor(public auth: AuthService) {}
  today = new Date();

  get isAuthRoute() {
    const url = this.router.url;
    return url.startsWith('/login') || url.startsWith('/register');
  }

  onLogout() {
    this.auth.logout();
    this.router.navigate(['/login'], { replaceUrl: true });
  }
}
