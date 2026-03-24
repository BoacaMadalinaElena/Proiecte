import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UrlService {
  private dictionary: { [key: string]: string } = {};

  //host = "192.168.145.45" //telefon
  //host = "192.168.85.45";
  //host = "192.168.1.101" //camin
  //host = "192.168.54.102" // eduorom
  //host = "44.219.73.72"
  host = "100.24.146.38"

  urlPort = `http://${this.host}:8081`;

  constructor() {
    this.dictionary['createAccount'] = `${this.urlPort}/api/cluster/user`;
    this.dictionary['validateAccount'] = `${this.urlPort}/api/cluster/user/validateAccount`;

    this.dictionary['login'] = `${this.urlPort}/api/cluster/user/login`;
    
    this.dictionary["getIpAddress"] = `${this.urlPort}/api/cluster/ipAddress`;
    this.dictionary["sendCodeResetPassword"] = `${this.urlPort}/api/cluster/user/change-password-send-code`;
    this.dictionary["sendCodeResetPasswordId"] = `${this.urlPort}/api/cluster/user/change-password-send-code-id`;
    this.dictionary["checkCodeResetPassowrd"] = `${this.urlPort}/api/cluster/user/change-password-check-code`;
    this.dictionary["setPassword"] = `${this.urlPort}/api/cluster/user/set-password`;
    this.dictionary["feedbackPost"] = `${this.urlPort}/api/cluster/feedback`;
    this.dictionary["feedbackGet"] = `${this.urlPort}/api/cluster/feedback`;
    this.dictionary["update"] = `${this.urlPort}/api/cluster/user`;
    this.dictionary["logout"] = `${this.urlPort}/api/cluster/user/logoutJWSExtern`;
    this.dictionary["ip"] = '100.24.146.38'
  }

  getUrlByName(name: string): string {
    return this.dictionary[name];
  }
}
