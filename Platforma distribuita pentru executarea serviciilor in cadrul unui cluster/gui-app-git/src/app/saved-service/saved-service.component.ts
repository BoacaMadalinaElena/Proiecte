import { Component, Input } from '@angular/core';
import { TranslationService } from '../translation.service';
import { Router } from '@angular/router';
import { CookiesService } from '../cookies.service';
import { PopupComponent } from '../popup/popup.component';
import { PopupService } from '../popup.service';

@Component({
  selector: 'app-saved-service',
  standalone: true,
  imports: [PopupComponent],
  templateUrl: './saved-service.component.html',
  styleUrl: './saved-service.component.css'
})
export class SavedServiceComponent {
  @Input() inputElement: any;
  translations: any;

  constructor(private translationService: TranslationService, private router: Router,public cookies: CookiesService,private popupService: PopupService) { }

   ngOnInit() {
    try {
      this.translations =  this.translationService.getTranslations();
    } catch (error) {
      this.popupService.showPopup(JSON.stringify(error), null)
    }
  }

  openCode(){
    this.cookies.setCookie("idTab", 100 + "");
    this.router.navigate(["/full-saved-service",this.inputElement.codeId]);
  }
}
