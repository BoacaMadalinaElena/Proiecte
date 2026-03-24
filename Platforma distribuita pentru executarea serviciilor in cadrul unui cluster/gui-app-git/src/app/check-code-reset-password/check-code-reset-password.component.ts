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
  selector: 'app-check-code-reset-password',
  standalone: true,
  imports: [FormsModule, CommonModule,PopupComponent,LoaderComponent],
  templateUrl: './check-code-reset-password.component.html',
  styleUrl: './check-code-reset-password.component.css'
})
export class CheckCodeResetPasswordComponent {
  translations: any;
  language: string = "ro";
  email : string = ''
  valoareInput: number = 0; 
  messageFromServer = "";
  constructor(private router: Router, private translationService: TranslationService, public cookies: CookiesService, private userService: UserService, private popupService: PopupService, private route: ActivatedRoute) { }
  loaderFlag = false;

   ngOnInit() {
    let v = this.cookies.getByName("language");
    this.language = v != null && v != undefined && v.length > 0 ? v : "ro";
    try {
      this.translations =  this.translationService.getTranslations();
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
    this.translations =  this.translationService.getTranslations();
  }

  submit() {
    this.loaderFlag = true;
    this.userService.sendCodePassword({email:this.email,code:this.valoareInput}).subscribe(
      (response) => {
        this.loaderFlag = false;
        console.log("Cerere cu succes!");
        console.log(response);
        this.cookies.deleteAll();
        this.cookies.setCookie("tokenResetPassword",(response as any).token)
        this.router.navigate(["reset-password-page"]);
      },
      (error) => {
        this.loaderFlag = false;
        if (error.status === 401) {
          console.log("ceva")
          if (this.language === "ro")
            this.messageFromServer = error.error.messageRo;
          else
            this.messageFromServer = error.error.messageEng;
        }else{
          this.popupService.showPopup(JSON.stringify(error), null)
        }
      }
    )
  }
}
