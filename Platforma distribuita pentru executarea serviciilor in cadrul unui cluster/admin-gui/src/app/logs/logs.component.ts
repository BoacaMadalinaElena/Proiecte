import { Component } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { TranslationService } from '../translation.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { LogsService } from '../logs.service';
import { CookiesService } from '../cookies.service';
import { PopupService } from '../popup.service';
import { PopupComponent } from '../popup/popup.component';
import { LoaderComponent } from '../loader/loader.component';

@Component({
  selector: 'app-logs',
  standalone: true,
  imports: [NavbarComponent, FormsModule, CommonModule, PopupComponent,LoaderComponent],
  templateUrl: './logs.component.html',
  styleUrl: './logs.component.css'
})
export class LogsComponent {
  translations: any;
  typeMessages: string = "2";
  time = '';
  address = '';
  type = '';
  userId = '';
  logs: any[] = [];
  page = 0;
  prevPage = false;
  nextPage = false;
  formattedLogs: string = '';
  loaderFlag = false;
  microservice = ''

  constructor(private translationService: TranslationService, private logsService: LogsService, public cookies: CookiesService, private popupService: PopupService) { }

  ngOnInit() {
    this.translations = this.translationService.getTranslations();
  }

  start() {
    this.page = 0;
    this.submit();
  }

  submit() {
    this.loaderFlag = true;
    if (this.typeMessages === "0") {
      //Http node
      let authorizationHeader = this.cookies.getByName("token");
      if (authorizationHeader == undefined || authorizationHeader == null) {
        this.popupService.showPopup(this.translations.login.expirate, "/login")
      } else {
        this.logsService.getHttp(authorizationHeader, this.page + "", this.time, this.address, this.type,this.microservice)?.subscribe(
          (response: any) => {
            this.logs = response.content
            this.prevPage = (this.page > response.prevPage);
            this.nextPage = (this.page < response.nextPage);
            this.formatLogs();
            this.loaderFlag = false;
          },
          (error: any) => {
            this.loaderFlag = false;
            console.error('Eroare:', error);
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
    } else if (this.typeMessages === "1") {
      //Master node
      let authorizationHeader = this.cookies.getByName("token");
      if (authorizationHeader == undefined || authorizationHeader == null) {
        this.popupService.showPopup(this.translations.login.expirate, "/login")
      } else {
        this.logsService.getMaster(authorizationHeader, this.page + "", this.time, this.address, this.type)?.subscribe(
          (response: any) => {
            this.logs = response.content
            this.prevPage = (this.page > response.prevPage);
            this.nextPage = (this.page < response.nextPage);
            this.formatLogs();
            this.loaderFlag = false;
          },
          (error: any) => {
            this.loaderFlag = false;
            console.error('Eroare:', error);
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
    } else if (this.typeMessages === "2") {
      //Execution node
      let authorizationHeader = this.cookies.getByName("token");
      if (authorizationHeader == undefined || authorizationHeader == null) {
        this.popupService.showPopup(this.translations.login.expirate, "/login")
      } else {
        this.logsService.getExecution(authorizationHeader, this.page + "", this.time, this.address, this.type, this.userId)?.subscribe(
          (response: any) => {
            this.logs = response.content
            this.prevPage = (this.page > response.prevPage);
            this.nextPage = (this.page < response.nextPage);
            this.formatLogs();
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

  goToPrevPage() {
    this.page--;
    this.submit();
    window.scrollTo(0, 0);
  }

  goToNextPage() {
    this.page++;
    this.submit();
    window.scrollTo(0, 0);
  }

  onTypeChange(): void {
    this.type = '';
    this.time = '';
    this.page = 0;
    this.address = '';
    this.logs = [];
    this.userId = '';
  }

  padString(str: string, length: number): string {
    return str.padEnd(length, ' ');
  }

  padStringStart(str: string, length: number): string {
    return str.padStart(length, ' ');
  }

  formatLogs() {
    console.log("format")
    let formattedString = '';

    for (let item of this.logs) {
      formattedString += `${this.padString(item.localDateTime, 20)} ${this.padString(item.address, 20)} ${this.padString(item.type, 10)}`;
      if (this.typeMessages === '2') {
        formattedString += ` ${this.padString(item.userId, 36)}`;
      }
      if(this.typeMessages === "0"){
        formattedString += ` ${this.padString(item.typeNode, 10)}`;
      }
      formattedString += ` ${item.message}\n`;
    }

    this.formattedLogs = formattedString;
  }
}
