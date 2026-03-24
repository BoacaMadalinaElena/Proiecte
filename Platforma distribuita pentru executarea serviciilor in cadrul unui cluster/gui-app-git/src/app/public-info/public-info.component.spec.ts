import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PublicInfoComponent } from './public-info.component';

describe('PublicInfoComponent', () => {
  let component: PublicInfoComponent;
  let fixture: ComponentFixture<PublicInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PublicInfoComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PublicInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
