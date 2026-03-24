import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class PopupService {
  private popupSubject = new Subject<any>();

  showPopup(message: string,nextUrl:string | null) {
    this.popupSubject.next({ type: 'show', message: message,nextUrl:nextUrl });
  }

  hidePopup() {
    this.popupSubject.next({ type: 'hide' });
  }

  getPopupObservable() {
    return this.popupSubject.asObservable();
  }
}
