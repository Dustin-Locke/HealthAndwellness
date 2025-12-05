import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { HistoryComponent } from './pages/history/history.component';
import { LogComponent } from './pages/log/log.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { RemindersComponent } from './pages/reminders/reminders.component';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';
import {ChangePasswordComponent} from '@app/pages/change-password/change-password.component';
import {ModalComponent} from '@shared/modal/modal.component';

export const routes: Routes = [
  // Protected app pages
  { path: '', component: HomeComponent, canActivate: [authGuard] },
  { path: 'history', component: HistoryComponent, canActivate: [authGuard] },
  { path: 'log', component: LogComponent, canActivate: [authGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: 'reminders', component: RemindersComponent, canActivate: [authGuard] },
  { path: 'change-password', component: ChangePasswordComponent, canActivate: [authGuard] },



  // Auth-only pages (blocked when logged in)
  { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [guestGuard] },

  { path: '**', redirectTo: '' },
];
