import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SaveProgramComponent } from './save-program.component';

describe('SaveProgramComponent', () => {
  let component: SaveProgramComponent;
  let fixture: ComponentFixture<SaveProgramComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SaveProgramComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SaveProgramComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
