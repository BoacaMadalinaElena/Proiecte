import { Injectable } from '@angular/core';
import {CookieService} from 'ngx-cookie-service';

@Injectable({
  providedIn: 'root'
})
export class CookiesService {

  constructor(private cookieService:CookieService) { }

  setCookie(name:string,value:string){
    const now = new Date();
    const expires = new Date(now.getTime() + 2 * 60 * 60 * 1000);
    this.cookieService.set(name, value, expires, '/');
  }
   
  deleteCookie(name:string){
    this.cookieService.delete(name);
  }
   
  deleteAll(){
    let language = this.cookieService.get("language");
    this.cookieService.deleteAll("/");
    const cookies: {} = this.cookieService.getAll(); 
    for (const cookieName in cookies) {
      if (Object.prototype.hasOwnProperty.call(cookies, cookieName)) {
        this.cookieService.delete(cookieName,"/"); 
      }
    }
    this.cookieService.set("language",language);
  }

  getByName(name: string) : string | null{
    return this.cookieService.get(name);
  }
}
