import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { TranslationService } from '../translation.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './about.component.html',
  styleUrl: './about.component.css'
})
export class AboutComponent {
  // array cu limba 
  isVisible = false;
  translations: any;
  message = '';
  constructor(private translationService: TranslationService, private router: Router) { }

  ngOnInit() {
    try {
      this.translations = this.translationService.getTranslations();
    } catch (error) {
      if (error instanceof Error) {
        this.message = error.message;
      } else {
       this.message =  JSON.stringify(error);
      }
    }
  }

  navigate() {
    this.router.navigate(["/info"])
  }

  closePopup() {
    this.isVisible = false
  }
}
