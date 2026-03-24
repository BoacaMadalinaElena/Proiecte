import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PopupService {
  private popupSubject = new Subject<any>();
  private currentPopupResolve: (() => void) | null = null;

  showPopup(message: string, nextUrl: string | null): Promise<void> {
    if (this.currentPopupResolve) {
      return new Promise<void>((resolve) => {
        this.currentPopupResolve = () => {
          this.currentPopupResolve = null;
          this.showPopupInternal(message, nextUrl).then(resolve);
        };
      });
    } else {
      return this.showPopupInternal(message, nextUrl);
    }
  }

  private showPopupInternal(message: string, nextUrl: string | null): Promise<void> {
    this.popupSubject.next({ type: 'show', message, nextUrl });
    return new Promise<void>((resolve) => {
      this.currentPopupResolve = resolve;
    });
  }

  hidePopup() {
    this.popupSubject.next({ type: 'hide' });
    if (this.currentPopupResolve) {
      this.currentPopupResolve();
      this.currentPopupResolve = null;
    }
  }

  getPopupObservable() {
    return this.popupSubject.asObservable();
  }

}
