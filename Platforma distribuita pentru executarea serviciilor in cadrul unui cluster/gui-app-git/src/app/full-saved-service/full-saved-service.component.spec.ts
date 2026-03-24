import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FullSavedServiceComponent } from './full-saved-service.component';

describe('FullSavedServiceComponent', () => {
  let component: FullSavedServiceComponent;
  let fixture: ComponentFixture<FullSavedServiceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FullSavedServiceComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(FullSavedServiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
