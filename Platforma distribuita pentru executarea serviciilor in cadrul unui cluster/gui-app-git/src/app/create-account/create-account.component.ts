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
  selector: 'app-create-account',
  standalone: true,
  imports: [CommonModule, FormsModule, PopupComponent,LoaderComponent],
  templateUrl: './create-account.component.html',
  styleUrl: './create-account.component.css'
})
export class CreateAccountComponent {

  constructor(private router: Router, private translationService: TranslationService, public cookies: CookiesService, private userService: UserService, private popupService: PopupService) { }

  translations: any;
  language: string | null = null;
  isValidForm = false;
  messageFromServer = "";
  loaderFlag = false;

  async ngOnInit(): Promise<void> {
    let v = this.cookies.getByName("language");
    this.language = v != null && v != undefined && v.length > 0 ? v : "ro";
    try {
      this.translations = await this.translationService.getTranslations();
    } catch (error) {
      this.popupService.showPopup(JSON.stringify(error), null)
    }
  }

  navigateToLogin() {
    this.cookies.deleteAll();
    this.router.navigate(["/login"])
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

  onClickSubmit(request: any) {
    this.loaderFlag = true;
    request.role = "user";
    let v = this.cookies.getByName("language");
    this.language = v != null && v != undefined && v.length > 0 ? v : "ro";
    request.language = this.language;
    this.userService.createUser(request).subscribe(
      (response) => {
        this.loaderFlag = false;
        console.log("Cerere cu succes!");
        console.log(response);
        this.messageFromServer = "";
        this.cookies.deleteAll();
        // this.popupService.showPopup(this.translations.createAccount.succes,"/login")
        this.router.navigate(["send-code", request.email])
      },
      (error) => {
        this.loaderFlag = false;
        if (error.status === 409 || error.status === 422) {
          console.log("ceva")
          if (this.language === "ro")
            this.messageFromServer = error.error.messageRo;
          else
            this.messageFromServer = error.error.messageEng;
        }
        else {
          this.popupService.showPopup(JSON.stringify(error), null)
        }
      }
    )
  }
}
