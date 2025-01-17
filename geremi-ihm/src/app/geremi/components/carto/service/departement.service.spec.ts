import { HttpClient } from "@angular/common/http";
import { TestBed } from "@angular/core/testing";
import { DepartementService } from "./departement.service";

let service: DepartementService;
beforeEach(() => {
    TestBed.configureTestingModule({ providers: [DepartementService]});
    service = TestBed.inject(DepartementService);
});

describe('RegionService', () => {
    let service: DepartementService;
    let httpClientSpy: jasmine.SpyObj<HttpClient>;
  
    beforeEach(() => {
      TestBed.configureTestingModule({ providers: [DepartementService]});
      service = TestBed.inject(DepartementService);
    });
  
    it('should create', () => {
      expect(service).toBeTruthy();
    });
  });