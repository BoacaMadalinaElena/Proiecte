import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RunCodeInfoComponent } from './run-code-info.component';

describe('RunCodeInfoComponent', () => {
  let component: RunCodeInfoComponent;
  let fixture: ComponentFixture<RunCodeInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RunCodeInfoComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(RunCodeInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
