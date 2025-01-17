import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IndicComponent } from './indic.component';

describe('IndicComponent', () => {
  let component: IndicComponent;
  let fixture: ComponentFixture<IndicComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [IndicComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(IndicComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
