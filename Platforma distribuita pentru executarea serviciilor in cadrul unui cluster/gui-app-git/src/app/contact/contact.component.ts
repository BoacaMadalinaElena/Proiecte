import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { FormsModule } from '@angular/forms';
import { TranslationService } from '../translation.service';
import { CookiesService } from '../cookies.service';
import { FeedbackService } from '../feedback.service';
import { Router } from '@angular/router';
import { PopupService } from '../popup.service';
import { PopupComponent } from '../popup/popup.component';
import { LoaderComponent } from '../loader/loader.component';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [CommonModule, NavbarComponent, FormsModule, PopupComponent,LoaderComponent],
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.css'
})
export class ContactComponent {
  feedbackData = {
    subject: '',
    content: ''
  };
  translations: any;
  language: string | null = null;
  messageFromServer = '';
  loaderFlag = false;

  constructor(private translationService: TranslationService, public cookies: CookiesService, private feedbackService: FeedbackService, private router: Router, private popupService: PopupService) { }

  ngOnInit() {
    let v = this.cookies.getByName("language");
    this.language = v != null && v != undefined && v.length > 0 ? v : "ro";
    try {
      this.translations = this.translationService.getTranslations();
    } catch (error) {
      this.popupService.showPopup(JSON.stringify(error), null)
    }
  }

  submitFeedback() {
    console.log('Trimis feedback:', this.feedbackData);
    let authorizationHeader = this.cookies.getByName("token");
    if (authorizationHeader == null) {
      this.cookies.deleteAll();
      this.router.navigate(["/login"]);
    } else {
      this.loaderFlag = true;
      this.feedbackService.feedbackPost(authorizationHeader, this.feedbackData).subscribe(
        response => {
          this.loaderFlag = false;
          this.messageFromServer = ''
          this.feedbackData.content = ''
          this.feedbackData.subject = ''
          this.popupService.showPopup(this.translations.contact.succes, "/contact")
        },
        error => {
          this.loaderFlag = false;
          if (error.status === 401 || error.status === 422) {
            if (this.language === "ro")
              this.messageFromServer = error.error.messageRo;
            else
              this.messageFromServer = error.error.messageEng;
          } else {
            this.popupService.showPopup(JSON.stringify(error), null)
          }
        }
      )
    }
  }
}
