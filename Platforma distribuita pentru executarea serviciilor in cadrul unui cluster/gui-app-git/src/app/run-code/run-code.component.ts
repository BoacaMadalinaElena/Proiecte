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
import { PopupComponent } from '../popup/popup.component';
import { PopupService } from '../popup.service';

@Component({
  selector: 'app-run-code',
  standalone: true,
  imports: [CommonModule, NavbarComponent, FileUploadComponent, FormsModule, PopupComponent],
  templateUrl: './run-code.component.html',
  styleUrl: './run-code.component.css'
})
export class RunCodeComponent {
  selectedFilesMessage: string = 'Nu există fișiere selectate.';
  listOfFiles = new Map();
  listOfFilesName: any[] = new Array();
  listOfFilesNameAux: any[] = new Array();
  listOfFilesAux = new Map();
  isSelectedType = false;
  isSelectedFiles = false;
  isSelectedStartUp = false;
  typeFiles = "";
  startUpFile = "";
  tabNumber = 0;
  terminalText = "";
  disabledSend = true;
  inputValue: string = '';
  isRedText = false;
  filesResponse: any[] = new Array();
  typeRun = ''

  messageFromServer = ''
  translations: any;
  webSocketSubscription: Subscription | null | undefined = null;

  constructor(private manageListService: ManageListFilesService, private router: Router, private translationService: TranslationService, public cookies: CookiesService, public webSocketService: WebSocketService, private popupService: PopupService) { }

  ngOnInit() {
    try {
      this.translations = this.translationService.getTranslations();
    } catch (error) {
      this.popupService.showPopup(JSON.stringify(error), null)
    }
    this.manageListService.getObservable().subscribe(value => {
      this.listOfFilesName = this.manageListService.getList();
      this.listOfFilesNameAux = this.manageListService.getListAux();
      if (this.listOfFilesName.length == 0) {
        this.isSelectedFiles = false;
        this.isSelectedStartUp = false;
      } else {
        this.isSelectedFiles = true;
      }
    });
  }

  currnetMessage = ""
  isCompletMessage = false
  currentFrame = 0;

  handleMessage(message: MessageEvent): void {
    let dataJson = JSON.parse(message.data);
    let contentFrame = dataJson.messageFrame.content;
    let countFrame = dataJson.messageFrame.countFrame;
    let currentFrame = dataJson.messageFrame.frame;

    // 0 1   
    //console.log(currentFrame + " " + (countFrame - 1))
    if (currentFrame < countFrame - 1) {
      this.currnetMessage += contentFrame;
    } else {
      this.currnetMessage += contentFrame;
      try {
        let msg = JSON.parse(this.currnetMessage);
        console.log("Msg = " + this.currnetMessage)
        this.currnetMessage = "";
        if (msg.statusCode === 500 || msg.statusCode === 503) {
          console.error(msg.content)
          this.popupService.showPopup(msg.content,null)
        /*
          this.terminalText += "<br>"
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
                //console.log(this.filesResponse)
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
  }

  onFileSelected(event: any) {
    const selectedFiles = event.target.files;
    if (selectedFiles.length === 0) {
    } else {
      // citire fisiere
      for (let i = 0; i < selectedFiles.length; i++) {
        const file = selectedFiles[i];
        const reader = new FileReader();
        if (file.size > 10 * 1024 * 1024) {
          this.popupService.showPopup(`Fișierul ${file.name} este prea mare. Limita este de 10 MB.`,null);
          continue;
        }
        if (file.name.endsWith(".class") || file.name.endsWith(".jar") ||  file.name.endsWith(".py")) {
          reader.onload = (e) => {
            if (e.target != null) {
              const result: ArrayBuffer = e.target.result as ArrayBuffer;
              let uint8Array = new Uint8Array(result);
              let octetiVector = [];

              for (let i = 0; i < uint8Array.length; i++) {
                octetiVector.push(uint8Array[i]);
              }
              this.manageListService.addItem(file.name, "", false, true, octetiVector, Number(this.typeRun));

              this.listOfFilesName = this.manageListService.getList();
              this.isSelectedFiles = this.listOfFilesName.length > 0;
              this.startUpFile = ''
            }
          };
          reader.readAsArrayBuffer(file);
        } else {
          reader.onload = (e) => {
            if (e.target != null) {
              const result = e.target.result;
              this.manageListService.addItem(file.name, result, false, true, [], Number(this.typeRun));
              this.listOfFilesName = this.manageListService.getList();
              this.isSelectedFiles = this.listOfFilesName.length > 0;
              this.startUpFile = ''
            }
          };
          reader.readAsText(file); // apelare citire
        }
      }
    }
  }

  onSelectChangeType(): void {
    if (this.typeFiles != "") {
      this.isSelectedType = true;
      this.manageListService.clear();
      this.isSelectedFiles = false;
      this.isSelectedStartUp = false;
      this.listOfFiles.clear();
      this.listOfFilesName = [];
    }
  }

  onSelectChangeStartUp(selectedValue: any): void {
    if (this.typeFiles != "") {
      this.isSelectedStartUp = true;
      this.manageListService.setStartUp(selectedValue);
    }
  }

  async submit() {

    this.terminalText = "";
    this.filesResponse = new Array();
    let aaray1 = this.manageListService.getList();
    let array2 = this.manageListService.getListAux();
    let array = aaray1.concat(array2)
    console.log(array)

    await this.webSocketService.disconnect();
    this.webSocketService.connect(JSON.stringify({ from: "", content: "", isRequest: true, listOfCodes: array, type: Number(this.typeRun) }))
      .then(() => {
        this.webSocketSubscription = this.webSocketService.getMessages()?.subscribe({
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

  submitNewMaven(request: any) {
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
          this.popupService.showPopup(`Fișierul ${file.name} este prea mare. Limita este de 10 MB.`,null);
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
    console.log(content)
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

  downloadFileJava() {
    const link = document.createElement('a');
    link.href = 'assets/GetPathFile.java';
    link.download = 'GetPathFile.java';
    link.click();
  }
}
