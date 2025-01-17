import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScenarioContrainteComponent } from './scenario-contrainte.component';

describe('ScenarioMateriauComponent', () => {
  let component: ScenarioContrainteComponent;
  let fixture: ComponentFixture<ScenarioContrainteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ScenarioContrainteComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScenarioContrainteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
