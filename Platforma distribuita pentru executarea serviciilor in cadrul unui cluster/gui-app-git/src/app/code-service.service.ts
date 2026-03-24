import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CookiesService } from './cookies.service';
import { UrlService } from './url.service';

@Injectable({
  providedIn: 'root'
})
export class CodeServiceService {

  constructor(private http: HttpClient, public cookies: CookiesService, private urlService: UrlService) { }

  insertCode(authorizationHeader: string, codeRecordRequest: any): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': authorizationHeader,
      'Content-Type': 'application/json'
    });
    let apiUrl = 'http://' + this.urlService.getUrlByName('ip') + ':8081/api/cluster/code';
    return this.http.post<any>(`${apiUrl}`, codeRecordRequest, { headers })
      .pipe(
        catchError(error => {
          throw  error;
        })
      );
  }

  getAllCodes(page: number = 0, title = "", size: number = 6) {
    const url = `http://` + this.urlService.getUrlByName('ip') + `:8081/api/cluster/code?page=${page}&size=${size}&title=${title}`;

    let token = this.cookies.getByName("token")
    if (token != null) {
      const headers = new HttpHeaders({
        'Authorization': token
      });

      const options = { headers: headers };

      return this.http.get(url, options);
    } else {
      return null;
    }
  }

  getById(id: number) {
    const url = `http://` + this.urlService.getUrlByName('ip') + `:8081/api/cluster/code/${id}`;

    let token = this.cookies.getByName("token")
    if (token != null) {
      const headers = new HttpHeaders({
        'Authorization': token
      });

      const options = { headers: headers };

      return this.http.get(url, options);
    } else {
      return null;
    }
  }

  deleteCodeById(id: string) {
    let token = this.cookies.getByName("token")
    if (token != null) {
      const headers = new HttpHeaders({
        'Authorization': token
      });

      const options = { headers: headers };

      return this.http.delete(`http://` + this.urlService.getUrlByName('ip') + `:8081/api/cluster/code/${id}`, options);
    } else {
      return null;
    }
  }
}
