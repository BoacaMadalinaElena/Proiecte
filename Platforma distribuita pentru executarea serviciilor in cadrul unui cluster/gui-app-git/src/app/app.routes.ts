import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { AvaiableServiceComponent } from './avaiable-service/avaiable-service.component';
import { RunCodeComponent } from './run-code/run-code.component';
import { ResultComponent } from './result/result.component';
import { ContactComponent } from './contact/contact.component';
import { AboutComponent } from './about/about.component';
import { SettingsComponent } from './settings/settings.component';
import { CreateAccountComponent } from './create-account/create-account.component';
import { StartUpComponent } from './start-up/start-up.component';
import { LoginComponent } from './login/login.component';
import { AuthGuard } from './auth.guard';
import { RunCodeInfoComponent } from './run-code-info/run-code-info.component';
import { SaveProgramComponent } from './save-program/save-program.component';
import { FullSavedServiceComponent } from './full-saved-service/full-saved-service.component';
import { SendCodeComponent } from './send-code/send-code.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { CheckCodeResetPasswordComponent } from './check-code-reset-password/check-code-reset-password.component';
import { ResetPasswordPageComponent } from './reset-password-page/reset-password-page.component';
import { InfoComponent } from './info/info.component';
import { PublicInfoComponent } from './public-info/public-info.component';

export const routes: Routes = [
    { path: '', component: HomeComponent, canActivate: [AuthGuard] },
    { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },
    { path: 'avaiable-service/:page/:title', component: AvaiableServiceComponent, canActivate: [AuthGuard] },
    { path: 'avaiable-service/:page', component: AvaiableServiceComponent, canActivate: [AuthGuard] },
    { path: 'run-code', component: RunCodeComponent, canActivate: [AuthGuard] },
    { path: 'result', component: ResultComponent, canActivate: [AuthGuard] },
    { path: 'contact', component: ContactComponent, canActivate: [AuthGuard] },
    { path: 'about', component: AboutComponent, canActivate: [AuthGuard] },
    { path: "settings", component: SettingsComponent, canActivate: [AuthGuard] },
    { path: "create-account", component: CreateAccountComponent },
    { path: "start-up", component: StartUpComponent },
    { path: "login", component: LoginComponent },
    { path: "run-code-info", component: RunCodeInfoComponent, canActivate: [AuthGuard] },
    { path: "save-program", component: SaveProgramComponent, canActivate: [AuthGuard] },
    { path: "full-saved-service/:id", component: FullSavedServiceComponent, canActivate: [AuthGuard] },
    { path: "send-code/:email", component: SendCodeComponent },
    { path: "reset-password", component: ResetPasswordComponent },
    { path: "check-code-reset-password/:email", component: CheckCodeResetPasswordComponent },
    { path: "reset-password-page", component: ResetPasswordPageComponent },
    { path: "info", component: InfoComponent,canActivate: [AuthGuard]  },
    { path: "public-info", component: PublicInfoComponent  }
];
