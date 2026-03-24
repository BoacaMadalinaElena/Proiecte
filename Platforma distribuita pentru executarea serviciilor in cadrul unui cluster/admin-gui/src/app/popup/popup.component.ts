import { Component } from '@angular/core';
import { PopupService } from '../popup.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { TranslationService } from '../translation.service';

@Component({
  selector: 'app-popup',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './popup.component.html',
  styleUrl: './popup.component.css'
})
export class PopupComponent {
  isVisible = false;
  message = '';
  translations: any;
  nextUrl = null;

  constructor(private popupService: PopupService, private router: Router, private translationService: TranslationService) {
    this.popupService.getPopupObservable().subscribe(
      data => {
        if (data.type === 'show') {
          this.ngOnInit();
          this.message = data.message;
          this.isVisible = true;
          this.nextUrl = data.nextUrl;
        } else {
          this.isVisible = false;
        }
      }
    );
  }

  ngOnInit() {
    try {
      this.translations = this.translationService.getTranslations();
    } catch (error) {
      console.error('Error fetching translations:', error);
    }
  }

  closePopup() {
    this.popupService.hidePopup();
    if (this.nextUrl != null) {
      this.router.navigate([this.nextUrl]);
    }
  }
}
