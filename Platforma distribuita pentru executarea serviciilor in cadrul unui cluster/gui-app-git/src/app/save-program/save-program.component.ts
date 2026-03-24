import { Component } from '@angular/core';
import { TranslationService } from '../translation.service';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { FileUploadComponent } from '../file-upload/file-upload.component';
import { FormsModule } from '@angular/forms';
import { CodeServiceService } from '../code-service.service';
import { Router } from '@angular/router';
import { PopupComponent } from '../popup/popup.component';
import { PopupService } from '../popup.service';
import { CookiesService } from '../cookies.service';
import { LoaderComponent } from '../loader/loader.component';

@Component({
  selector: 'app-save-program',
  standalone: true,
  imports: [CommonModule, NavbarComponent, CommonModule, NavbarComponent, FileUploadComponent, FormsModule, PopupComponent,LoaderComponent],
  templateUrl: './save-program.component.html',
  styleUrl: './save-program.component.css'
})
export class SaveProgramComponent {

  listOfFilesName: any[] = new Array();
  listOfFiles: any[] = new Array();
  isSelectedType = false;
  isSelectedFiles = false;
  isSelectedStartUp = false;
  typeFiles = "";
  startUpFile = "";
  translations: any;
  textAreaValue1: string = ''
  textAreaValue2: string = ''
  isPublic: boolean = false;
  typeRun = ''
  loaderFlag = false;

  constructor(private translationService: TranslationService, private codeService: CodeServiceService, private cookieServide: CookiesService, private router: Router, private popupService: PopupService,public cookies: CookiesService) { }

   ngOnInit() {
    try {
      this.translations =  this.translationService.getTranslations();
    } catch (error) {
      this.popupService.showPopup(JSON.stringify(error), null)
    }
  }

  onSelectChangeType(): void {
    if (this.typeFiles != "") {
      this.isSelectedType = true;
      this.isSelectedFiles = false;
      this.isSelectedStartUp = false;
      this.listOfFiles = new Array();
      this.listOfFilesName = new Array();
      this.startUpFile = "";
    }
  }

  onFileSelected(event: any) {
    const selectedFiles = event.target.files;
    if (selectedFiles.length === 0) {
    } else {
      // citire fisiere
      for (let i = 0; i < selectedFiles.length; i++) {
        const file = selectedFiles[i];
       if (file.size > 10 * 1024 * 1024) {
          alert(`Fișierul ${file.name} este prea mare. Limita este de 10 MB.`);
          continue;
        }
        const reader = new FileReader();
        if (file.name.endsWith(".class") || file.name.endsWith(".jar") || file.name.endsWith(".py")) {
          reader.onload = (e) => {
            if (e.target != null) {
              const result: ArrayBuffer = e.target.result as ArrayBuffer;
              let uint8Array = new Uint8Array(result);
              let octetiVector = [];

              for (let i = 0; i < uint8Array.length; i++) {
                octetiVector.push(uint8Array[i]);
              }
              this.listOfFiles.push({ fileName: file.name, contentString: "", startUp: false, codeClass: true, contentBytes: octetiVector,type:this.typeRun })
              this.listOfFilesName.push(file.name);
              this.isSelectedFiles = this.listOfFilesName.length > 0;
              this.startUpFile = ''
            }
          };
          reader.readAsArrayBuffer(file);
        } else {
          reader.onload = (e) => {
            if (e.target != null) {
              const result = e.target.result;
              this.listOfFiles.push({ fileName: file.name, contentString: result, startUp: false, codeClass: true, contentBytes: [],type:this.typeRun })
              this.listOfFilesName.push(file.name);
              this.isSelectedFiles = this.listOfFilesName.length > 0;
              this.startUpFile = ''
            }
          };
          reader.readAsText(file); // apelare citire
        }
      }
    }
  }


  onSelectChangeStartUp(selectedValue: any): void {
    for (let file of this.listOfFiles) {
        file.startUp = false;
    }
    if (this.typeFiles != "") {
      for (let file of this.listOfFiles) {
        if (file.fileName === selectedValue) {
          file.startUp = true;
          break;
        }
      }
      this.isSelectedStartUp = true;
      console.log(this.listOfFiles)
    }
  }

  removeValue(fileName: string) {
    const index = this.listOfFiles.findIndex(file => file.fileName === fileName);

    if (index !== -1) {
      this.listOfFiles.splice(index, 1);
    }

    const nameIndex = this.listOfFilesName.indexOf(fileName);

    if (nameIndex !== -1) {
      this.listOfFilesName.splice(nameIndex, 1);
    }
  }

  async submit() {
    this.loaderFlag = true;
    let object = {
      title: this.textAreaValue1,
      description: this.textAreaValue2,
      userId: '',
      codeMessageList: this.listOfFiles,
      type:this.typeFiles,
      isPublic:this.isPublic,
      typeRun:this.typeRun
    }

    console.log("isPublic: " + this.isPublic)

    console.log(object)
    let authorizationHeader = this.cookieServide.getByName("token");
    if (authorizationHeader == null) {
      this.cookies.deleteAll();
      this.router.navigate(["/login"]);
    } else {
      this.codeService.insertCode(authorizationHeader, object)
        .subscribe(
          response => {
            this.loaderFlag = false;
            this.popupService.showPopup(this.translations.save.saveSuccess, "/avaiable-service/0")
          },
          error => {
            this.loaderFlag = false;
            if (error.status === 401) {
              this.cookies.deleteAll();
              this.router.navigate(['/login']);
            }else{
              if (error.status === 422 || error.status === 401) {
                let v = this.cookies.getByName("language");
                let language = v != null && v != undefined && v.length > 0 ? v : "ro";
                if (language === "ro")
                  this.popupService.showPopup(error.error.messageRo, null);
                else
                  this.popupService.showPopup(error.error.messageEng, null);
              } 
              else {
                this.popupService.showPopup(JSON.stringify(error), null)
              }
            }
          }
        );
    }
  }

  toggleCheckbox(event: any) {
    this.isPublic = event.target.checked;
    console.log("isPublic: " + this.isPublic);
  }

  downloadFileJava() {
    const link = document.createElement('a');
    link.href = 'assets/GetPathFile.java';
    link.download = 'GetPathFile.java';
    link.click();
  }
}
