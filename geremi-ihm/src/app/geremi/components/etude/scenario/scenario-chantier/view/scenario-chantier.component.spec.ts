import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScenarioChantierComponent } from './scenario-chantier.component';

describe('ScenarioChantierComponent', () => {
  let component: ScenarioChantierComponent;
  let fixture: ComponentFixture<ScenarioChantierComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ScenarioChantierComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScenarioChantierComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
