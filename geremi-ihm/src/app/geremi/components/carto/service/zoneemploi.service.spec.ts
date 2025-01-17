import { HttpClient } from "@angular/common/http";
import { TestBed } from "@angular/core/testing";
import { ZoneemploiService } from "./zoneemploi.service";

let service: ZoneemploiService;
beforeEach(() => {
    TestBed.configureTestingModule({ providers: [ZoneemploiService]});
    service = TestBed.inject(ZoneemploiService);
});

describe('RegionService', () => {
    let service: ZoneemploiService;
    let httpClientSpy: jasmine.SpyObj<HttpClient>;
  
    beforeEach(() => {
      TestBed.configureTestingModule({ providers: [ZoneemploiService]});
      service = TestBed.inject(ZoneemploiService);
    });
  
    it('should create', () => {
      expect(service).toBeTruthy();
    });
  });