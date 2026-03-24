import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { TranslationService } from '../translation.service';
import { PopupComponent } from '../popup/popup.component';
import { PopupService } from '../popup.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, NavbarComponent,PopupComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  translations: any;
  constructor(private translationService: TranslationService,private popupService: PopupService) { }

   ngOnInit() {
    try {
      this.translations =  this.translationService.getTranslations();
    } catch (error) {
      this.popupService.showPopup(JSON.stringify(error), null)
    }
  }
}
