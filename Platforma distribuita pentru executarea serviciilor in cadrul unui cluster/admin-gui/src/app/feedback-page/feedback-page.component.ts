import { Component } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { TranslationService } from '../translation.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FeedbackService } from '../feedback.service';
import { CookiesService } from '../cookies.service';
import { PopupService } from '../popup.service';
import { Router } from '@angular/router';
import { PopupComponent } from '../popup/popup.component';
import { LoaderComponent } from '../loader/loader.component';

@Component({
  selector: 'app-feedback-page',
  standalone: true,
  imports: [NavbarComponent, FormsModule, CommonModule, PopupComponent,LoaderComponent],
  templateUrl: './feedback-page.component.html',
  styleUrl: './feedback-page.component.css'
})
export class FeedbackPageComponent {
  translations: any;
  logs: any[] = [];
  page = 0;
  prevPage = false;
  nextPage = false;
  email = ''
  loaderFlag = false;

  constructor(private translationService: TranslationService, private feedbackService: FeedbackService, public cookies: CookiesService, private popupService: PopupService, private router: Router) { }

  ngOnInit() {
    this.translations = this.translationService.getTranslations();
    this.submit()
  }

  start() {
    this.page = 0;
    this.submit();
  }
  submit() {
    this.loaderFlag = true;
    let authorizationHeader = this.cookies.getByName("token");
    if (authorizationHeader == null) {
      this.cookies.deleteAll();
      this.router.navigate(["/login"]);
    } else {
      this.feedbackService.getHttp(authorizationHeader, this.page + "", this.email)?.subscribe(
        (response: any) => {
          this.logs = response.list
          this.prevPage = !(response.prev == this.page);
          this.nextPage = !(response.next == this.page);
this.loaderFlag = false;
        },
        (error: any) => {
          this.loaderFlag = false;
          if (error.status === 401) {
            this.cookies.deleteAll();
            this.popupService.showPopup(this.translations.login.expirate, "/login")
          } else if (error.status === 403) {
            this.cookies.deleteAll();
            this.popupService.showPopup(this.translations.login.message403, "/login")
          }
          else {
            this.popupService.showPopup(JSON.stringify(error), null)
          }
        }
      );
    }
  }

  goToPrevPage() {
    this.page--;
    this.submit()
  }

  goToNextPage() {
    this.page++;
    this.submit()
  }

  onTypeChange(): void {
    this.page = 0;
    this.logs = [];
  }

  deleteItem(item: any) {
    this.loaderFlag = true;
    let authorizationHeader = this.cookies.getByName("token");
    if (authorizationHeader == null) {
      this.cookies.deleteAll();
      this.router.navigate(["/login"]);
    } else {
      this.feedbackService.deleteHttp(authorizationHeader, item)?.subscribe(
        (response: any) => {
          this.popupService.showPopup(this.translations.feedback.delete, "/feedback-page")
          this.start()
          this.loaderFlag = false;
        },
        (error: any) => {
          this.loaderFlag = false;
          if (error.status === 401) {
            this.cookies.deleteAll();
            this.popupService.showPopup(this.translations.login.expirate, "/login")
          } 
          else if (error.status === 403) {
            this.cookies.deleteAll();
            this.popupService.showPopup(this.translations.login.message403, "/login")
          }
          else {
            this.popupService.showPopup(JSON.stringify(error), null)
          }
        }
      );
    }
  }
}
