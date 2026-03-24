import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AvaiableServiceComponent } from './avaiable-service.component';

describe('AvaiableServiceComponent', () => {
  let component: AvaiableServiceComponent;
  let fixture: ComponentFixture<AvaiableServiceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AvaiableServiceComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AvaiableServiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
