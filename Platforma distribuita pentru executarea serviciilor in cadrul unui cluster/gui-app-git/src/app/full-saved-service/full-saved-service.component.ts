
import { Component, OnInit, numberAttribute } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { FileUploadComponent } from '../file-upload/file-upload.component';
import { ManageListFilesService } from '../manage-list-files.service';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TranslationService } from '../translation.service';
import { CookiesService } from '../cookies.service';
import { WebSocketService } from '../web-socket.service';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { CodeServiceService } from '../code-service.service';
import { PopupComponent } from '../popup/popup.component';
import { PopupService } from '../popup.service';
import { LoaderComponent } from '../loader/loader.component';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-full-saved-service',
  standalone: true,
  imports: [CommonModule, NavbarComponent, FileUploadComponent, FormsModule, PopupComponent,LoaderComponent],
  templateUrl: './full-saved-service.component.html',
  styleUrl: './full-saved-service.component.css'
})
export class FullSavedServiceComponent {
  listOfFiles = new Map();
  listOfFilesNameAux: any[] = new Array();
  listOfFilesAux = new Map();
  tabNumber = 0;
  terminalText = "";
  disabledSend = true;
  inputValue: string = '';
  isRedText = false;
  filesResponse: any[] = new Array();
  info: any;
  filesCode: any[] = new Array();
  id = ""
  messageFromServer = ''
  loaderFlag = false;

  translations: any;
  webSocketSubscription: Subscription | null | undefined = null;

  constructor(private manageListService: ManageListFilesService,private router: Router, private translationService: TranslationService, public cookies: CookiesService, public webSocketService: WebSocketService, private route: ActivatedRoute, private codeService: CodeServiceService, private popupService: PopupService) { }

  ngOnInit() {

    try {
      this.translations = this.translationService.getTranslations();
      this.loaderFlag = true;
      this.getCodeById();
    } catch (error) {
      this.popupService.showPopup(JSON.stringify(error), null)
    }
  }

  currnetMessage = ""
  isCompletMessage = false
  currentFrame = 0;

  handleMessage(message: MessageEvent): void {
    let dataJson = JSON.parse(message.data);
    console.log("handleMessage in component!" )
    console.log("---------------------------")
    let contentFrame = dataJson.messageFrame.content;
    let countFrame = dataJson.messageFrame.countFrame;
    let currentFrame = dataJson.messageFrame.frame;

    if (currentFrame < countFrame - 1) {
      this.currnetMessage += contentFrame;
    } else {
      this.currnetMessage += contentFrame;
      try {
        let msg = JSON.parse(this.currnetMessage);
      
        this.currnetMessage = "";
        if (msg.statusCode == 500) {
          this.popupService.showPopup(msg.content,null)
        /*  this.terminalText += "<br>"
          this.terminalText += "<br>"
          this.terminalText += "<br>"
          this.terminalText += "<p class=\"mb-0 redTest text-danger d-inline\" style=\"color: red;\">" + msg.content.replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;') + "</p>";
    */
        }
        else if (msg.content != "READ" && msg.content.length > 0) {
          if (msg.content == "START_EXCEPTION") {
            this.isRedText = true;
          } else {
            if (this.terminalText.length > 0)
              this.terminalText += "<br>"
            if (msg.content.startsWith("STOP_EXCEPTION")) {
              this.isRedText = false;
            } else {
              if (msg.isFileResponse == true) {
                this.filesResponse.push({ fileName: msg.fileName, content: msg.contentFile })
              }
              else if (!this.isRedText) {
                this.terminalText += "<p class=\"mb-0 d-inline\">" + msg.content.replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;') + "</p>";
              } else {
                this.terminalText += "<p class=\"mb-0 redTest text-danger d-inline\" style=\"color: red;\">" + msg.content.replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;') + "</p>";
              }
            }
          }
        } else {
          this.disabledSend = false;

        }
      } catch (e: any) {
        this.popupService.showPopup(JSON.stringify(e), null)
      }
    }
  }

  handleError(error: Error): void {
    console.error(JSON.stringify(error))
    this.popupService.showPopup(JSON.stringify(error), null)
  }

  changeTab(value: number) {
    this.tabNumber = value;
  }

  readValue(): void {
    this.disabledSend = true;
    //{"from":"server","content":"Introduceti un numar intreg:","isRequest":false,"isReadRequest":false}
    let val = this.inputValue;
    this.inputValue = "";
    this.webSocketService.sendMessage(JSON.stringify({ from: "", content: val, "isRequest": true, "isReadRequest": true }))
    if (this.terminalText.endsWith('\n')) {
      this.terminalText = this.terminalText.slice(0, -1);
    }
    // this.terminalText += "<p class=\"my-0 py-0\">" + this.inputValue + "</p>";
    this.terminalText += "<p class=\"mb-0 text-success d-inline\">" + val.replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;') + "</p>";
  }

  onFileSelectedAux(event: any) {
    const selectedFiles = event.target.files;
    if (selectedFiles.length === 0) {
    } else {
      // citire fisiere
      for (let i = 0; i < selectedFiles.length; i++) {
        const file = selectedFiles[i];
        const reader = new FileReader();

        if (file.size > 10 * 1024 * 1024) {
          alert(`Fișierul ${file.name} este prea mare. Limita este de 10 MB.`);
          continue;
        }

        reader.onload = (e) => {
          if (e.target != null) {
            const result: ArrayBuffer = e.target.result as ArrayBuffer;
            let uint8Array = new Uint8Array(result);
            let octetiVector = [];
            for (let i = 0; i < uint8Array.length; i++) {
              octetiVector.push(uint8Array[i]);
            }
            this.manageListService.addItemAux(file.name, "", false, false, octetiVector);
            this.listOfFilesNameAux = this.manageListService.getListAux();

          }
        };
        reader.readAsArrayBuffer(file);
      }
    }
  }


  downloadFile(file: any) {
    //  this.filesResponse.push({fileName: dataJson.fileName,content:dataJson.contentFile})
    const content = new Uint8Array(file.content);
    const filename = file.fileName;

    const blob = new Blob([content]);
    const url = window.URL.createObjectURL(blob);

    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = filename;
    document.body.appendChild(anchor);
    anchor.click();

    // Cleanup
    document.body.removeChild(anchor);
    window.URL.revokeObjectURL(url);
  }

  downloadFileCode(file: any) {

    //  this.filesResponse.push({fileName: dataJson.fileName,content:dataJson.contentFile})
    if (file.contentString.length != 0) {
      const content = file.contentString;
      const filename = file.fileName;

      const blob = new Blob([content], { type: 'text/plain' });

      const url = window.URL.createObjectURL(blob);

      const anchor = document.createElement('a');
      anchor.href = url;
      anchor.download = filename;
      document.body.appendChild(anchor);
      anchor.click();

      // Cleanup
      document.body.removeChild(anchor);
      window.URL.revokeObjectURL(url);
    } else {
      const content = Uint8Array.from(file.contentBytes);
      const filename = file.fileName;

      const blob = new Blob([content]);
      const url = window.URL.createObjectURL(blob);


      const anchor = document.createElement('a');
      anchor.href = url;
      anchor.download = filename;
      document.body.appendChild(anchor);
      anchor.click();

      // Cleanup
      document.body.removeChild(anchor);
      window.URL.revokeObjectURL(url);
    }
  }

  stringToBytes(str: string): Uint8Array {
    const bytes = new Uint8Array(str.length);
    for (let i = 0; i < str.length; i++) {
      bytes[i] = str.charCodeAt(i);
    }
    return bytes;
  }

  getCodeById() {
    this.route.params.subscribe(params => {
      let paramValue = params['id'];
      this.id = paramValue;
      this.codeService.getById(paramValue)?.subscribe(
        (response: any) => {
          this.loaderFlag = false;
          this.info = response;
          this.filesCode = response.codeMessageList;
        },
        (error: any) => {
          this.loaderFlag = false;
          console.log("Error: " + JSON.stringify(error) + " " + error.status)
          if (error.status === 500 || error.status === 503 || error.status === 401) {
            let v = this.cookies.getByName("language");
            let language = v != null && v != undefined && v.length > 0 ? v : "ro";
            if (language === "ro") {
              this.popupService.showPopup(error.error.messageRo, null);
               //  alert(error.error.messageRo)
                 this.info = {}
                 this.filesCode = []
            }
            else {
              this.popupService.showPopup(error.error.messageEng, null);
             //   alert(error.error.messageEng)
                this.info = {}
                this.filesCode = []
            }
          }
        }
      );
    });
  }

  deleteCode() {
    this.codeService.deleteCodeById(this.id)?.subscribe(
      () => {
        this.popupService.showPopup(this.translations.delete.deleteSucces, "/avaiable-service/0")
      },
      error => {
        this.popupService.showPopup(JSON.stringify(error), null)
      }
    );
  }

  async submit() {
    console.clear();
    await this.webSocketService.disconnect();
    this.terminalText = "";
    this.filesResponse = new Array();
    let array2 = this.manageListService.getListAux();

    this.webSocketService.connect(JSON.stringify({ from: "", content: "", isRequest: true, listOfCodes: array2, idCode: this.id }))
    .then(() => {
      this.webSocketSubscription = new Observable<MessageEvent>(observer => {
        const subscription = this.webSocketService.getMessages()?.subscribe({
          next: (message) => {
            observer.next(message); 
          },
          error: (error) => {
            console.error('Error in WebSocketService:', error);
            observer.error(error); 
          },
          complete: () => {
            console.error("complete")
            observer.complete(); 
          }
        });

        return () => {
          subscription?.unsubscribe();
        };
      }).subscribe({
        next: (message) => this.handleMessage(message), 
        error: (error) => this.handleError(error) 
      });
    })
      .catch((error) => {
        let v = this.cookies.getByName("language");
        let language = v != null && v != undefined && v.length > 0 ? v : "ro";
        if (error.status === 422 || error.status === 500) {
          if (language === "ro")
            this.messageFromServer = error.error.messageRo;
          else
            this.messageFromServer = error.error.messageEng;
          this.popupService.showPopup(this.messageFromServer, null)
        } if (error.status === 500 || error.status === 503 || error.status === 401) {
          let v = this.cookies.getByName("language");
          let language = v != null && v != undefined && v.length > 0 ? v : "ro";
          if (language === "ro")
            this.popupService.showPopup(error.error.messageRo, null);
          else
            this.popupService.showPopup(error.error.messageEng, null);
        }
        else {
          if (language === "ro")
            this.popupService.showPopup(this.translations.info.errorWebSocket, null)
          else
            this.popupService.showPopup(this.translations.info.errorWebSocket, null)
        }
      });
    this.tabNumber = 3;
  }
}
