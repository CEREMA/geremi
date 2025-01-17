import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScenarioGeneraliteComponent } from './scenario-generalite.component';

describe('ScenarioMateriauComponent', () => {
  let component: ScenarioGeneraliteComponent;
  let fixture: ComponentFixture<ScenarioGeneraliteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ScenarioGeneraliteComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScenarioGeneraliteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
