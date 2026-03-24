import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckCodeResetPasswordComponent } from './check-code-reset-password.component';

describe('CheckCodeResetPasswordComponent', () => {
  let component: CheckCodeResetPasswordComponent;
  let fixture: ComponentFixture<CheckCodeResetPasswordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CheckCodeResetPasswordComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CheckCodeResetPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
