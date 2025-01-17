import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScenarioInstallationComponent } from './scenario-installation.component';

describe('ScenarioMateriauComponent', () => {
  let component: ScenarioInstallationComponent;
  let fixture: ComponentFixture<ScenarioInstallationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ScenarioInstallationComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScenarioInstallationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
