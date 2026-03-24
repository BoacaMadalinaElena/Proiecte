import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { TranslationService } from '../translation.service';
import { CookiesService } from '../cookies.service';
import { CommonModule } from '@angular/common';
import { PopupComponent } from '../popup/popup.component';
import { PopupService } from '../popup.service';

@Component({
  selector: 'app-start-up',
  standalone: true,
  imports: [CommonModule, PopupComponent],
  templateUrl: './start-up.component.html',
  styleUrl: './start-up.component.css'
})
export class StartUpComponent {

  translations: any;
  language: string | null = null;

  constructor(private router: Router, private translationService: TranslationService, public cookies: CookiesService, private popupService: PopupService) { }

  ngOnInit() {
    let v = this.cookies.getByName("language");
    this.language = v != null && v != undefined && v.length > 0 ? v : "ro";
    try {
      this.translations = this.translationService.getTranslations();
    } catch (error) {
      this.popupService.showPopup(JSON.stringify(error), null)
    }
  }

  navigateToLogin() {
    this.cookies.deleteAll();
    this.router.navigate(["/login"])
  }

  async onSelectionChange(event: any) {
    console.log(this.translations)
    const selectedOption = event.target.value;
    console.log(selectedOption)
    let v = this.cookies.getByName("language");
    this.language = v != null && v != undefined && v.length > 0 ? v : "ro";
    if (selectedOption.length > 0)
      this.cookies.setCookie("language", selectedOption);
    this.translations = await this.translationService.getTranslations();
  }
}
