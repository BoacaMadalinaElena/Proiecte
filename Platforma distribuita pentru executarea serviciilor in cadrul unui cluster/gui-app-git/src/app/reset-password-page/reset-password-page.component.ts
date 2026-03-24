import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { TranslationService } from '../translation.service';
import { CookiesService } from '../cookies.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../user.service';
import { PopupService } from '../popup.service';
import { PopupComponent } from '../popup/popup.component';
import { LoaderComponent } from '../loader/loader.component';

@Component({
  selector: 'app-reset-password-page',
  standalone: true,
  imports: [FormsModule, CommonModule, PopupComponent,LoaderComponent],
  templateUrl: './reset-password-page.component.html',
  styleUrl: './reset-password-page.component.css'
})
export class ResetPasswordPageComponent {
  constructor(private router: Router, private translationService: TranslationService, public cookies: CookiesService, private userService: UserService, private popupService: PopupService) { }
  passwordMismatch = false;
  passwordMismatchMessage = "Passwords do not match";
  translations: any;
  language: string | null = null;
  messageFromServer: string | null = null;
  password1: string = '';
  password2: string = '';
  loaderFlag = false;

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
    this.router.navigate(["/create-account"])
  }

  onSelectionChange(event: any) {
    console.log(this.translations)
    const selectedOption = event.target.value;
    console.log(selectedOption)
    if (selectedOption.length > 0)
      this.cookies.setCookie("language", selectedOption);
    let v = this.cookies.getByName("language");
    this.language = v != null && v != undefined && v.length > 0 ? v : "ro";
    this.translations = this.translationService.getTranslations();
  }



  onClickSubmit(formData: any): void {
       this.loaderFlag = true;
    let authorizationHeader = this.cookies.getByName("tokenResetPassword");
    if (authorizationHeader == null) {
      this.cookies.deleteAll();
      this.router.navigate(["/login"]);
    } else {
      this.userService.setPassword(authorizationHeader, { password: formData.password1 }).subscribe(response => {
        this.loaderFlag = false;
        this.popupService.showPopup(this.translations.changePassword.succes, "/login")
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
      );
    }
  }

  checkPasswordMatch(): void {
    this.passwordMismatch = this.password1 !== this.password2;
    this.messageFromServer = ''
  }
}
