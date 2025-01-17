import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CartoComponent } from './carto.component';

describe('CartoComponent', () => {
    let component: CartoComponent;
    let fixture: ComponentFixture<CartoComponent>;
  
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        declarations: [CartoComponent]
      })
        .compileComponents();
  
      fixture = TestBed.createComponent(CartoComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });
  
    it('should create', () => {
      expect(component).toBeTruthy();
    });
  });