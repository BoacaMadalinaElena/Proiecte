import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { TranslationService } from '../translation.service';
import { CookiesService } from '../cookies.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PopupComponent } from '../popup/popup.component';
import { UserService } from '../user.service';
import { PopupService } from '../popup.service';
import { LoaderComponent } from '../loader/loader.component';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, NavbarComponent, FormsModule, PopupComponent,LoaderComponent],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css'
})
export class SettingsComponent {
  translations: any;
  language: string = "ro";
  messageFromServer = "";
  id: string | undefined | null = '';
  username: string | undefined | null = '';
  firstName: string | undefined | null = '';
  lastName: string | undefined | null = '';
  description: string | undefined | null = '';
  loaderFlag = false;

  constructor(private translationService: TranslationService, public cookies: CookiesService, private router: Router, private userService: UserService, private popupService: PopupService) { }

  ngOnInit() {
    try {
      this.translations = this.translationService.getTranslations();
      let id = this.cookies.getByName("id");
      this.username = this.cookies.getByName("username");
      this.firstName = this.cookies.getByName("firstName");
      this.lastName = this.cookies.getByName("lastName");
      this.description = this.cookies.getByName("description");
      this.id = this.cookies.getByName("id");
      if (id == null) {
        this.cookies.deleteAll();
        this.router.navigate(["/login"]);
      } else {
        this.id = id;
      }
    } catch (error) {
      this.popupService.showPopup(JSON.stringify(error), null)
    }
    let v = this.cookies.getByName("language");
    this.language = v != null && v != undefined && v.length > 0 ? v : "ro";
    this.translations = this.translationService.getTranslations();
  }

  async onSelectionChange(event: any) {
    console.log(this.translations)
    const selectedOption = event.target.value;
    console.log(selectedOption)
    if (selectedOption.length > 0)
      this.cookies.setCookie("language", selectedOption);
    let v = this.cookies.getByName("language");
    this.language = v != null && v != undefined && v.length > 0 ? v : "ro";
    this.translations = await this.translationService.getTranslations();
    this.router.navigate([''], { skipLocationChange: true }).then(() => {
      console.log('Page reloaded without reloading WebSocket connection');
      this.cookies.setCookie("idTab", 0 + "");
    });
  }

  onClickSubmit() {
    let token = this.cookies.getByName("token");
    if (token == null) {
      this.cookies.deleteAll();
      this.popupService.showPopup(this.translations.changePassword.succes, "/login")
      this.router.navigate(["/login"])
    } else {
   this.loaderFlag = true;
      this.userService.update(token, {
        "id": this.id,
        "username": this.username,
        "firstName": this.firstName,
        "lastName": this.lastName,
        "description": this.description
      }).subscribe(
        (response) => {
          this.loaderFlag = false;
          console.log("Cerere cu succes!");
          console.log(response)
          this.cookies.setCookie("username", this.username != null ? this.username : '');
          this.cookies.setCookie("firstName", this.firstName != null ? this.firstName : '');
          this.cookies.setCookie("lastName", this.lastName != null ? this.lastName : '');
          this.cookies.setCookie("description", this.description != null ? this.description : '');
          this.messageFromServer = ''
          this.popupService.showPopup(this.translations.settings.sucees, "/settings")
        },
        (error) => {
          this.loaderFlag = false;
          if (error.status === 404 || error.status === 422 || error.status === 409) {
            if (this.language === "ro")
              this.messageFromServer = error.error.messageRo;
            else
              this.messageFromServer = error.error.messageEng;
          }
          else if (error.status === 401) {
            if (this.language === "ro")
              this.messageFromServer = error.error.messageRo;
            else
              this.messageFromServer = error.error.messageEng;
            this.popupService.showPopup(this.messageFromServer, null)
          } else {
            this.popupService.showPopup(JSON.stringify(error), null)
          }
        }
      )
    }
  }

  resetPassowrd() {
    this.translations = this.translationService.getTranslations();
    let token = this.cookies.getByName("token");
    if (token == null) {
      this.cookies.deleteAll();
      this.popupService.showPopup(this.translations.changePassword.succes, "/login")
      this.router.navigate(["/login"])
    } else {
      console.log("Valoarea inputului este: ", this.id);
      let v = this.cookies.getByName("language");
      this.language = v != null && v != undefined && v.length > 0 ? v : "ro";
      this.userService.sendCodeId(token, { field: this.id, language: this.language }).subscribe(
        (response) => {
          console.log("Cerere cu succes!");
          console.log(response)
          this.router.navigate(["check-code-reset-password", (response as any).field])
        },
        (error) => {
          if (error.status === 401 || error.status === 404 || error.status === 422) {
            if (this.language === "ro")
              this.messageFromServer = error.error.messageRo;
            else
              this.messageFromServer = error.error.messageEng;
            this.popupService.showPopup(this.messageFromServer, null)
          } else {
            this.popupService.showPopup(JSON.stringify(error), null)
          }
        }
      )
    }
  }
}
