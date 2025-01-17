import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChantiersEnvergureComponent } from './chantiers-env.component';

describe('ChantiersEnvComponent', () => {
  let component: ChantiersEnvergureComponent;
  let fixture: ComponentFixture<ChantiersEnvergureComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChantiersEnvergureComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChantiersEnvergureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
