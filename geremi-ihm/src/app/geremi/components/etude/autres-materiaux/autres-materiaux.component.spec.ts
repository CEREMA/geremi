import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AutresMateriauxComponent } from './autres-materiaux.component';

describe('AutresMateriauxComponent', () => {
  let component: AutresMateriauxComponent;
  let fixture: ComponentFixture<AutresMateriauxComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AutresMateriauxComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AutresMateriauxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
