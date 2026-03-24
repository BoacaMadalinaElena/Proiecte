import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { AvaiableServiceComponent } from './avaiable-service/avaiable-service.component';
import { RunCodeComponent } from './run-code/run-code.component';
import { ResultComponent } from './result/result.component';
import { ContactComponent } from './contact/contact.component';
import { AboutComponent } from './about/about.component';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'home', component: HomeComponent },
    { path: 'avaiable-service', component: AvaiableServiceComponent },
    { path: 'run-code', component: RunCodeComponent },
    { path: 'result', component: ResultComponent },
    { path: 'contact', component: ContactComponent },
    { path: 'about', component: AboutComponent },
];
