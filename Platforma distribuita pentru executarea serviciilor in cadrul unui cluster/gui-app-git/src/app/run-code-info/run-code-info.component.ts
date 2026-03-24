import { Component } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { TranslationService } from '../translation.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-run-code-info',
  standalone: true,
  imports: [NavbarComponent],
  templateUrl: './run-code-info.component.html',
  styleUrl: './run-code-info.component.css'
})
export class RunCodeInfoComponent {
  constructor(private router: Router, private translationService: TranslationService) { }

  translations: any;


}
