import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SavedServiceComponent } from './saved-service.component';

describe('SavedServiceComponent', () => {
  let component: SavedServiceComponent;
  let fixture: ComponentFixture<SavedServiceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SavedServiceComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SavedServiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
