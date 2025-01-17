import { HttpClient } from "@angular/common/http";
import { TestBed } from "@angular/core/testing";
import { RegionService } from "./region.service";

let service: RegionService;
beforeEach(() => {
    TestBed.configureTestingModule({ providers: [RegionService]});
    service = TestBed.inject(RegionService);
});

describe('RegionService', () => {
    let service: RegionService;
    let httpClientSpy: jasmine.SpyObj<HttpClient>;
  
    beforeEach(() => {
      TestBed.configureTestingModule({ providers: [RegionService]});
      service = TestBed.inject(RegionService);
    });
  
    it('should create', () => {
      expect(service).toBeTruthy();
    });
  });