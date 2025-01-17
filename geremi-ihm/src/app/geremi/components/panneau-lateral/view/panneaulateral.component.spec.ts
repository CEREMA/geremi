import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PanneauLateralComponent } from './panneaulateral.component';

describe('PanneauLateralComponent', () => {
    let component: PanneauLateralComponent;
    let fixture: ComponentFixture<PanneauLateralComponent>;
  
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        declarations: [PanneauLateralComponent]
      })
        .compileComponents();
  
      fixture = TestBed.createComponent(PanneauLateralComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });
  
    it('should create', () => {
      expect(component).toBeTruthy();
    });
  });