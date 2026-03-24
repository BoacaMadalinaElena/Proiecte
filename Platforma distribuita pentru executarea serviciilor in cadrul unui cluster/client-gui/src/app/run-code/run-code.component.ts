import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { FileUploadComponent } from '../file-upload/file-upload.component';
import { ManageListFilesService } from '../manage-list-files.service';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-run-code',
  standalone: true,
  imports: [CommonModule, NavbarComponent, FileUploadComponent, FormsModule],
  templateUrl: './run-code.component.html',
  styleUrl: './run-code.component.css'
})
export class RunCodeComponent implements OnInit {
  selectedFilesMessage: string = 'Nu există fișiere selectate.';
  listOfFiles = new Map();
  listOfFilesName: string[] = new Array();
  isSelectedType = false;
  isSelectedFiles = false;
  isSelectedStartUp = false;
  typeFiles = "";
  startUpFile = "";

  constructor(private manageListService: ManageListFilesService, private router : Router) { }

  ngOnInit(): void {
    this.manageListService.getObservable().subscribe(value => {
      this.listOfFilesName = this.manageListService.getList();
      if (this.listOfFilesName.length == 0) {
        this.isSelectedFiles = false;
      } else {
        this.isSelectedFiles = true;
      }
      console.log(this.listOfFilesName);
      console.log(this.isSelectedFiles)
    });
  }


  onFileSelected(event: any) {
    const selectedFiles = event.target.files;
    if (selectedFiles.length === 0) {
    } else {
      // citire fisiere
      for (let i = 0; i < selectedFiles.length; i++) {
        const file = selectedFiles[i];
        const reader = new FileReader();

        // citire asincrona
        reader.onload = (e) => {
          if (e.target != null) {
            const result = e.target.result;
            this.manageListService.addItem(file.name, result);
            this.listOfFilesName = this.manageListService.getList();
            if (this.listOfFilesName.length == 0) {
              this.isSelectedFiles = false;
            } else {
              this.isSelectedFiles = true;
            }
          }
        };

        reader.readAsText(file); // apelare citire
      }
    }
  }

  onSelectChangeType(): void {
    if (this.typeFiles != "") {
      this.isSelectedType = true;
    }
  }

  onSelectChangeStartUp(): void {
    if (this.typeFiles != "") {
      this.isSelectedStartUp = true;
    }
  }

  submit(): void {
    console.log("Lista de fisiere trimise spre executie este: " + this.listOfFilesName);
    this.router.navigate(['/result']);
  }
}
