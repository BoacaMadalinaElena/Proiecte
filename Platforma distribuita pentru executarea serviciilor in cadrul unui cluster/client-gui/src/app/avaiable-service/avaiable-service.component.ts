import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-avaiable-service',
  standalone: true,
  imports: [CommonModule,NavbarComponent],
  templateUrl: './avaiable-service.component.html',
  styleUrl: './avaiable-service.component.css'
})
export class AvaiableServiceComponent {

}
