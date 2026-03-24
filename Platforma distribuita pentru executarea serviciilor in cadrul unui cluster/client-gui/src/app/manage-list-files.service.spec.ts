import { TestBed } from '@angular/core/testing';

import { ManageListFilesService } from './manage-list-files.service';

describe('ManageListFilesService', () => {
  let service: ManageListFilesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ManageListFilesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
