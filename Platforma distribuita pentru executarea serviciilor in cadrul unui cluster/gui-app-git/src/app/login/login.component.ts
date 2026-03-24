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
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule,PopupComponent,LoaderComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  constructor(private router: Router, private translationService: TranslationService, public cookies: CookiesService, private userService: UserService, private popupService: PopupService) { }

  translations: any;
  language: string | null = null;
  messageFromServer: string | null = null;
  loaderFlag = false;

   ngOnInit() {
    let v = this.cookies.getByName("language");
    this.language = v != null && v != undefined && v.length > 0 ? v : "ro";
    try {
      this.translations =  this.translationService.getTranslations();
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
    this.translations =  this.translationService.getTranslations();
  }

  onClickSubmit(request: any) {
    this.loaderFlag = true;
    console.log(request)
    this.cookies.deleteAll()
    this.userService.login(request).subscribe(
      (response: any) => {
        this.loaderFlag = false;
        this.cookies.setCookie("id", response.id);
        this.cookies.setCookie("username", response.username);
        this.cookies.setCookie("firstName", response.firstName);
        this.cookies.setCookie("lastName", response.lastName);
        this.cookies.setCookie("token", response.token);
        this.cookies.setCookie("description", response.description);
        this.cookies.setCookie("roleType", response.roleType);
        this.router.navigate(["/home"]);
      },
      (error) => {
        this.loaderFlag = false;
        if (error.status === 401 || error.status === 422) {
          if (this.language === "ro")
            this.messageFromServer = error.error.messageRo;
          else
            this.messageFromServer = error.error.messageEng;
        }else{
          this.popupService.showPopup(this.translations.info.error + "  " + JSON.stringify(error), null)
        }
      }
    )
  }
}
