import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ManageListFilesService } from '../manage-list-files.service';

@Component({
  selector: 'app-file-upload',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './file-upload.component.html',
  styleUrl: './file-upload.component.css'
})
export class FileUploadComponent {
  @Input()
  inputValue: any = {};

  constructor(private service: ManageListFilesService) { }


  removeFiles() {
    this.service.remove(this.inputValue);
    this.service.removeAux(this.inputValue)
  }

}
