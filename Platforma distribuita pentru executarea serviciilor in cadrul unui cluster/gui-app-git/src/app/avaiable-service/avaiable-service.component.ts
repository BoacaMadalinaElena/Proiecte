import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { SavedServiceComponent } from '../saved-service/saved-service.component';
import { CodeServiceService } from '../code-service.service';
import { Router } from '@angular/router';
import { CookiesService } from '../cookies.service';
import { ActivatedRoute } from '@angular/router';
import { PopupComponent } from '../popup/popup.component';
import { PopupService } from '../popup.service';
import { TranslationService } from '../translation.service';
import { FormsModule } from '@angular/forms';
import { LoaderComponent } from '../loader/loader.component';

@Component({
  selector: 'app-avaiable-service',
  standalone: true,
  imports: [CommonModule, NavbarComponent, SavedServiceComponent,PopupComponent,FormsModule,LoaderComponent],
  templateUrl: './avaiable-service.component.html',
  styleUrl: './avaiable-service.component.css'
})
export class AvaiableServiceComponent {
  listOfServices = []
  next = "0";
  prev = "0";
  current = "0";
  translations: any;
  searchTerm: string = '';
  loaderFlag = false;

  constructor(private codeService: CodeServiceService, private router: Router,public cookies: CookiesService,private route: ActivatedRoute,private popupService: PopupService, private translationService: TranslationService) { }

  ngOnInit() {
    this.translations = this.translationService.getTranslations();
    this.getAllCodes()
  }

  getAllCodes() {
    this.route.params.subscribe(params => {
      let paramValue = params['page'];
      let title = params['title'];
      if(paramValue == null || paramValue == undefined){
        paramValue = "0"
      }
      if(title == null || title == undefined){
        title = ""
      }
      this.current = paramValue;
      this.loaderFlag = true;
      this.codeService.getAllCodes(paramValue,title)?.subscribe(
        (response: any) => {
          this.loaderFlag = false;
          this.listOfServices = response.listShortCode
          this.prev = response.prevPage
          this.next = response.nextPage
        },
        (error: any) => {
          this.loaderFlag = false;
          if (error.status === 401) {
            let v = this.cookies.getByName("language");
            let language = v != null && v != undefined && v.length > 0 ? v : "ro";
            if (language === "ro")
              this.popupService.showPopup(error.error.messageRo, null);
            else
              this.popupService.showPopup(error.error.messageEng, null);
          }else{
            console.log(error)
            this.popupService.showPopup(this.translations.info.error + "  " + JSON.stringify(error), null)
          }
        }
      );
    });
  }

  createCode(){
    this.cookies.setCookie("idTab", 100 + "");
    this.router.navigate(["save-program"])
  }

  goToPrevPage(): void {
    this.router.navigate(["avaiable-service",this.prev,this.searchTerm])
  }

  goToNextPage(): void {
    this.router.navigate(["avaiable-service",this.next,this.searchTerm])
  }

  search(): void {
   console.log(this.searchTerm)
   this.router.navigate(["avaiable-service",0,this.searchTerm])
  }
}
