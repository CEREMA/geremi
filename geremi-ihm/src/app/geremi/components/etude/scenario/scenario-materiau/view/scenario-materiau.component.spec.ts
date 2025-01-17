import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScenarioMateriauComponent } from './scenario-materiau.component';

describe('ScenarioMateriauComponent', () => {
  let component: ScenarioMateriauComponent;
  let fixture: ComponentFixture<ScenarioMateriauComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ScenarioMateriauComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScenarioMateriauComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
