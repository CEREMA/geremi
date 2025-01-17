import { TestBed } from '@angular/core/testing';

import { AccueilResolver } from './accueil.resolver';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('AccueilResolver', () => {
  let resolver: AccueilResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    });
    resolver = TestBed.inject(AccueilResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });
});
