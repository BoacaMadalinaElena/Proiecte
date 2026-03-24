import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { AuthGuard } from './auth.guard';
import { LogsComponent } from './logs/logs.component';
import { FeedbackPageComponent } from './feedback-page/feedback-page.component';

export const routes: Routes = [
    { path: '', component: HomeComponent, canActivate: [AuthGuard] },
    { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },
    { path: "login", component: LoginComponent },
    { path: "logs", component: LogsComponent, canActivate: [AuthGuard] },
    { path: "feedback-page", component: FeedbackPageComponent,canActivate: [AuthGuard]  },
];
