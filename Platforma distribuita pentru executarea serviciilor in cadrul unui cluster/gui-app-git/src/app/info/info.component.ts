import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { TranslationService } from '../translation.service';
import { Router } from '@angular/router';
import { PopupComponent } from '../popup/popup.component';
import { PopupService } from '../popup.service';


@Component({
  selector: 'app-info',
  standalone: true,
  imports: [CommonModule, NavbarComponent, PopupComponent],
  templateUrl: './info.component.html',
  styleUrl: './info.component.css'
})
export class InfoComponent {
  translations: any;
  constructor(private translationService: TranslationService, private router: Router, private popupService: PopupService) { }

  ngOnInit() {
    try {
      this.translations = this.translationService.getTranslations();
    } catch (error) {
      this.popupService.showPopup(JSON.stringify(error), null)
    }
  }

  navigate() {
    this.router.navigate(["/info"])
  }
}
