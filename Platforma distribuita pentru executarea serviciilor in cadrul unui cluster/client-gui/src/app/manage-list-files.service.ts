import { Injectable } from '@angular/core';
import { Subject,Observable } from 'rxjs'; // pentru a semnala stergerea unor elemente

@Injectable({
  providedIn: 'root'
})
export class ManageListFilesService {
  private listOfFiles = new Map();
  private listOfFileName: string[] = new Array();
  private dataSubject = new Subject<any>();

  constructor() { }

  public addItem(name: string, content: any) {
    this.listOfFiles.set(name, content);
    if (!this.listOfFileName.includes(name)) {
      this.listOfFileName.push(name);
    }
  }

  public getList() {
    return this.listOfFileName;
  }

  public remove(name: string){
    this.listOfFiles.delete(name);
    this.listOfFileName = this.listOfFileName.filter(item  => item !== name);
    this.dataSubject.next(null);
  }

  getObservable(): Observable<any> {
    return this.dataSubject.asObservable();
  }
}
