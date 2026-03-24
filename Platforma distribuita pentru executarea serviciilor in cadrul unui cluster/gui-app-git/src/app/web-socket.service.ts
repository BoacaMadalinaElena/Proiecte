import { Injectable } from '@angular/core';
import { Observable, Subject, throwError } from 'rxjs';
import { UrlService } from './url.service';
import { HttpClient } from '@angular/common/http';
import { CookiesService } from './cookies.service';
import { HttpHeaders } from '@angular/common/http';
import { catchError, tap } from 'rxjs/operators';
import { PopupService } from './popup.service';
import { TranslationService } from './translation.service';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private webSocketSubject: Subject<MessageEvent> | null = null;
  private webSocket: WebSocket | null = null;
  private booleanClosed = true;
  translations: any;
  private isConnect = false;
  private isMessage = false;

  constructor(private urlService: UrlService, private httpClient: HttpClient, private coockieService: CookiesService, private popupService: PopupService, private translationService: TranslationService) {
    this.translations = this.translationService.getTranslations();
  }

  public connect(messageToSend: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      if (this.webSocket && this.webSocket.readyState === WebSocket.OPEN) {
        this.sendMessage(messageToSend);
      }
      else {
        this.getIpAddress()?.subscribe(
          (response: any) => {
            this.webSocketSubject = new Subject<MessageEvent>();
            let url = "ws://" + response.ip + ":" + response.port + "//conexion-manager-node/cluster/" + this.coockieService.getByName("token");
            try {
              this.webSocket = new WebSocket(url);
              this.isMessage = false;
              this.webSocket.onmessage = (message) => {
                this.isMessage = true;
                if (this.webSocketSubject) {

                  let dataJson = JSON.parse(message.data);

                  let contentFrame = dataJson.messageFrame;
                  try {
                    let content = JSON.parse(contentFrame.content).content;
                    if (content == "Connected!") {
                      this.sendMessage(messageToSend);
                    }
                    if (JSON.parse(contentFrame.content).statusCode === 401) {
                      alert("Sesiunea a expirat vă rog să vă reconectați!");
                    }
                    if (JSON.parse(contentFrame.content).statusCode === 503) {
                      let v = this.coockieService.getByName("language");
                      let language = v != null && v != undefined && v.length > 0 ? v : "ro";
                      if (language === "ro")
                        {

                        this.popupService.showPopup(JSON.parse(content).messageRo, null)
                        }
                      else
                       { 
                        this.popupService.showPopup(JSON.parse(content).messageEng, null)
                       }
                    }
                  } catch (e: any) {
                    console.error(e);
                  }
                  // TODO
                  this.webSocketSubject.next(message);
                  this.isMessage = false;
                  resolve();
                  console.log("Received message in onmessage!")
                }
              };

              this.webSocket.onerror = (error) => {
                if (this.webSocketSubject) {
                  this.webSocketSubject.error(error);
                }
                reject(error);
              };

              this.webSocket.onclose = () => {
                if (this.booleanClosed) {
                  let v = this.coockieService.getByName("language");
                  let language = v != null && v != undefined && v.length > 0 ? v : "ro";
                  if (this.isMessage = false) {
                    if (this.isConnect = false) {
                      if (language === "ro")
                        this.popupService.showPopup(this.translations.info.errorTime, null)
                      else
                        this.popupService.showPopup(this.translations.info.errorTime, null)
                    } else {
                      if (language === "ro")
                        this.popupService.showPopup(this.translations.info.errorWebSocket, null)
                      else
                        this.popupService.showPopup(this.translations.info.errorWebSocket, null)
                    }
                  }else{
                    if (language === "ro")
                      this.popupService.showPopup(this.translations.info.errorTime, null)
                    else
                      this.popupService.showPopup(this.translations.info.errorTime, null)
                  }
                  
                }
                else { console.log("Conexiunea a fost inchisa!") }
                /* if (this.webSocketSubject) {
                   this.webSocketSubject.complete();
                 }*/
                this.booleanClosed = true;
                console.log(this.booleanClosed)
                resolve();
              };
            } catch (e: any) {
              let v = this.coockieService.getByName("language");
              let language = v != null && v != undefined && v.length > 0 ? v : "ro";
              if (language === "ro")
                this.popupService.showPopup(this.translations.info.errorWebSocket, null)
              else
                this.popupService.showPopup(this.translations.info.errorWebSocket, null)
            }
          },
          (error) => {
            console.log(JSON.stringify(error))
            reject(error);
          }
        )
      }
    });
  }

  /* public getMessages(): Observable<MessageEvent> | null {
     if (this.webSocketSubject == null) {
       console.log('WebSocketSubject is null.');
       return null;
     }
     return this.webSocketSubject.asObservable()
       .pipe(
         tap(message => {
           console.log('Received message in getMessages.');
         }),
         catchError(err => {
           console.error('Error in getMessages:', err);
           return this.handleError(err);
         })
       );
   }*/

  public getMessages(): Observable<MessageEvent> | null {
    if (this.webSocketSubject == null) {
      console.log('WebSocketSubject is null.');
      return null;
    }

    const messagesObservable = new Observable<MessageEvent>(observer => {
      const subscription = this.webSocketSubject?.asObservable().subscribe({
        next: (message: MessageEvent) => {
          console.log('Received message in custom Observable:');

          observer.next(message);
        },
        error: (err) => {
          console.error('Error in WebSocketSubject:', err);
          observer.error(err);
        },
        complete: () => {
          observer.complete();
        }
      });

      return () => {
        subscription?.unsubscribe();
      };
    });

    return messagesObservable.pipe(
      catchError(err => {
        console.error('Error in custom messagesObservable:', err);
        return this.handleError(err);
      })
    );
  }

  private handleError(error: Error): Observable<never> {
    console.log(JSON.stringify(error))
    return throwError(error);
  }

  public sendMessage(message: string): void {
    if (this.webSocket?.readyState === WebSocket.OPEN) {
      this.webSocket.send(message);
    } else {
      alert("Conexiunea nu a fost stabilita!");
    }
  }

  getIpAddress() {
    const url = this.urlService.getUrlByName("getIpAddress");

    let token = this.coockieService.getByName("token")
    if (token != null) {
      const headers = new HttpHeaders({
        'Authorization': token
      });

      const options = { headers: headers };

      return this.httpClient.get(url, options);
    } else {
      return null;
    }
  }


  public disconnect(): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.webSocket && (this.webSocket.readyState === WebSocket.OPEN || this.webSocket.readyState === WebSocket.CONNECTING)) {
        this.booleanClosed = false;
        console.log(this.booleanClosed);
        
        this.webSocket.onclose = () => {
          this.booleanClosed = true;
          console.log("Conexiunea WebSocket a fost inchisa.");
          
          this.webSocket = null;
          this.webSocketSubject = null;
  
          resolve();
        };
  
        this.webSocket.onerror = (error) => {
          console.log("Eroare la inchiderea conexiunii WebSocket.", error);
          reject(error);
        };
  
        this.webSocket.close();
      } else {
        console.log("Nu exista o conexiune WebSocket deschisa.");
        resolve(); 
      }
    });
  }
}
