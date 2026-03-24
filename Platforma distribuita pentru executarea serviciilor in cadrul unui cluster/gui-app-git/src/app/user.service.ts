import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UrlService } from './url.service';
import { CookiesService } from './cookies.service';
import { HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient, private url: UrlService, private coockieService: CookiesService) { }

  createUser(user: any) {
    const url = this.url.getUrlByName("createAccount");
    return this.http.post(url, user)
  }

  login(info: any) {
    const url = this.url.getUrlByName("login");
    return this.http.post(url, info)
  }

  sendCode(info: any) {
    const url = this.url.getUrlByName("sendCodeResetPassword");
    return this.http.post(url, info)
  }

  sendCodeId(token: string, info: any) {
    const url = this.url.getUrlByName("sendCodeResetPasswordId");

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    return this.http.post(url, info, { headers: headers });
  }

  //update
  update(token: string, info: any) {
    const url = this.url.getUrlByName("update");

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    return this.http.put(url, info, { headers: headers });
  }

  setPassword(token: string, info: any) {
    const url = this.url.getUrlByName("setPassword");

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    return this.http.post(url, info, { headers: headers });
  }

  sendCodePassword(info: any) {
    const url = this.url.getUrlByName("checkCodeResetPassowrd");

    return this.http.post(url, info)
  }

  logout() {
    let token = this.coockieService.getByName("token");
    const url = this.url.getUrlByName("logout");

    if (token != null && token != undefined) {
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      return this.http.post(url, {
        "token": token,
        "ip": ""
      }, { headers: headers });
    } else {
      return null;
    }
  }

  validate(info: any) {
    const url = this.url.getUrlByName("validateAccount");
    
    return this.http.post(url, info)
  }

  isLogin(): boolean {
    let id = this.coockieService.getByName("id");
    if (id != null && id != undefined && id.length > 0)
      return true;
    else
      return false
  }
}
