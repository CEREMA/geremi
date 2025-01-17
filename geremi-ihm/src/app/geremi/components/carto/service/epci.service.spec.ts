import { HttpClient } from "@angular/common/http";
import { TestBed } from "@angular/core/testing";
import { EpciService } from "./epci.service";

let service: EpciService;
beforeEach(() => {
    TestBed.configureTestingModule({ providers: [EpciService]});
    service = TestBed.inject(EpciService);
});

describe('RegionService', () => {
    let service: EpciService;
    let httpClientSpy: jasmine.SpyObj<HttpClient>;
  
    beforeEach(() => {
      TestBed.configureTestingModule({ providers: [EpciService]});
      service = TestBed.inject(EpciService);
    });
  
    it('should create', () => {
      expect(service).toBeTruthy();
    });
  });