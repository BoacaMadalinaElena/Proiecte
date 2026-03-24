import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs'; // pentru a semnala stergerea unor elemente

@Injectable({
  providedIn: 'root'
})
export class ManageListFilesService {
  private listOfFiles = new Map();
  private listOfFilesAux = new Map();
  private listOfFileName: any[] = new Array();
  private listOfFileNameAux: any[] = new Array();
  private dataSubject = new Subject<any>();

  constructor() { }

  public addItem(name: string, content: any, isStartUp: boolean,isCodeClass:boolean, contentByte: number[],type:number) {
    this.listOfFiles.set(name, content);
    if (!this.listOfFileName.includes(name)) {
      this.listOfFileName.push({ "fileName": name, "contentString": content, "isStartUp": isStartUp, "contentBytes": contentByte,"isCodeClass" : isCodeClass,type:type });
    }
  }

  public addItemAux(name: string, content: any, isStartUp: boolean,isCodeClass:boolean, contentByte: number[]) {
    console.log("add: " + name)
    this.listOfFilesAux.set(name, content);
    if (!this.listOfFileNameAux.includes(name)) {
      this.listOfFileNameAux.push({ "fileName": name, "contentString": content, "isStartUp": isStartUp, "contentBytes": contentByte, "isCodeClass":isCodeClass });
    }
  }

  public getList() {
    return this.listOfFileName;
  }

  public getListAux() {
    return this.listOfFileNameAux;
  }

  public remove(name: string) {
    this.listOfFiles.delete(name);
    this.listOfFileName = this.listOfFileName.filter(item => item !== name);
    this.dataSubject.next(null);
  }

  public removeAux(name: string) {
    this.listOfFilesAux.delete(name);
    this.listOfFileNameAux = this.listOfFileNameAux.filter(item => item !== name);
    this.dataSubject.next(null);
  }

  getObservable(): Observable<any> {
    return this.dataSubject.asObservable();
  }

  setStartUp(name: string) {
    for (let i = 0; i < this.listOfFileName.length; i++) {
      if (this.listOfFileName[i].fileName === name) {
        this.listOfFileName[i].isStartUp = true;
        break;
      }
    }
  }

  clear() {
    this.listOfFileName = new Array();
    this.listOfFiles = new Map();
    this.listOfFileNameAux = new Array();
    this.listOfFilesAux = new Map();
  }
}
