import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslationService } from '../translation.service';
import { CookiesService } from '../cookies.service';
import { Router } from '@angular/router';
import { UserService } from '../user.service';
import { PopupComponent } from '../popup/popup.component';
import { PopupService } from '../popup.service';
import { WebSocketService } from '../web-socket.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule,PopupComponent],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {

  translations: any;
  language: string = "ro";

  constructor(private translationService: TranslationService, public cookies: CookiesService, private router: Router,private userService :UserService, private popupService: PopupService,public webSocketService: WebSocketService) { }

   ngOnInit() {
    try {
      this.translations =  this.translationService.getTranslations();
    } catch (error) {
      this.popupService.showPopup(JSON.stringify(error), null)
    }
    let v = this.cookies.getByName("language");
    this.language = v != null && v != undefined && v.length > 0 ? v : "ro";
    this.translations =  this.translationService.getTranslations();
  }

  changeTab(id: number) {
    this.cookies.setCookie("idTab", id + "");
  }

  getIdUnderline() {
    let val = this.cookies.getByName("idTab");
    if (val == null || val == undefined || val?.length == 0) {
      return 0;
    } else {
      return val;
    }
  }

  async logout() {
    await this.webSocketService.disconnect();
    this.userService.logout()?.subscribe(
      (response: any) => {
        this.cookies.deleteAll();
        this.router.navigate(["/login"]);
      },
      (error) => {
        this.cookies.deleteAll();
        this.router.navigate(["/login"]);
      }
    )
    
  }

  navigateTo(s: string) {
    this.router.navigate([s]);
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
    this.router.navigate(['./'], { skipLocationChange: true }).then(() => {
      console.log('Page reloaded without reloading WebSocket connection');
      this.cookies.setCookie("idTab", 0 + "");
    });
  }

}
