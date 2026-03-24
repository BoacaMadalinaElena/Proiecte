import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CookiesService } from './cookies.service';

@Injectable({
  providedIn: 'root'
})
export class LogsService {

  constructor(private http: HttpClient, public cookies: CookiesService) { }

  getHttp(authorizationHeader: string, page: string,time:string,address:string,type:string,typeNode : String): Observable<any> {
    let apiUrl = "http://100.24.146.38:8070/api/cluster/logsHttpNode/page"
    const headers = new HttpHeaders({
      'Authorization': authorizationHeader,
      'Content-Type': 'application/json'
    });

    return this.http.get<any>(`${apiUrl}?page=${page}&size=100&time=${time}&address=${address}&type=${type}&typeNode=${typeNode}`, { headers })
      .pipe(
        catchError(error => {
          throw  error;
        })
      );
  }

  getMaster(authorizationHeader: string, page: string,time:string,address:string,type:string): Observable<any> {
    let apiUrl = "http://100.24.146.38:8070/api/cluster/logsMasterNode/page"
    const headers = new HttpHeaders({
      'Authorization': authorizationHeader,
      'Content-Type': 'application/json'
    });

    return this.http.get<any>(`${apiUrl}?page=${page}&size=100&time=${time}&address=${address}&type=${type}`, { headers })
      .pipe(
        catchError(error => {
          throw  error;
        })
      );
  }

  getExecution(authorizationHeader: string, page: string,time:string,address:string,type:string,userId:string): Observable<any> {
    let apiUrl = "http://100.24.146.38:8070/api/cluster/logsExecutionNode/page"
    const headers = new HttpHeaders({
      'Authorization': authorizationHeader,
      'Content-Type': 'application/json'
    });

    return this.http.get<any>(`${apiUrl}?page=${page}&size=100&time=${time}&address=${address}&type=${type}&userId=${userId}`, { headers })
      .pipe(
        catchError(error => {
          throw error;
        })
      );
  }
}
