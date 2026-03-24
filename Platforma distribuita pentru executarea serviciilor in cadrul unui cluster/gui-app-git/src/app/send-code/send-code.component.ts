import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { TranslationService } from '../translation.service';
import { CookiesService } from '../cookies.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../user.service';
import { PopupService } from '../popup.service';
import { ActivatedRoute } from '@angular/router';
import { PopupComponent } from '../popup/popup.component';
import { LoaderComponent } from '../loader/loader.component';

@Component({
  selector: 'app-send-code',
  standalone: true,
  imports: [FormsModule, CommonModule, PopupComponent,LoaderComponent],
  templateUrl: './send-code.component.html',
  styleUrl: './send-code.component.css'
})
export class SendCodeComponent {
  translations: any;
  language: string = "ro";
  email: string = ''
  valoareInput: number = 0;
  messageFromServer = "";
  loaderFlag = false;

  constructor(private router: Router, private translationService: TranslationService, public cookies: CookiesService, private userService: UserService, private popupService: PopupService, private route: ActivatedRoute) { }

  ngOnInit() {
    let v = this.cookies.getByName("language");
    this.language = v != null && v != undefined && v.length > 0 ? v : "ro";
    try {
      this.translations = this.translationService.getTranslations();
    } catch (error) {
      this.popupService.showPopup(JSON.stringify(error), null)
    }
    this.route.params.subscribe(params => {
      let paramValue = params['email'];
      console.log(paramValue)
      this.email = paramValue;
    });
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

  submit() {
    this.loaderFlag = true;
    console.log("Valoarea inputului este: ", this.valoareInput);
    this.userService.validate({ email: this.email, code: this.valoareInput }).subscribe(
      (response) => {
        this.loaderFlag = false;
        console.log("Cerere cu succes!");
        console.log(response);
        this.cookies.deleteAll();
        this.popupService.showPopup(this.translations.createAccount.succes, "/login")
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
