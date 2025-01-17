import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { FeatureCollection } from "geojson";
import { ApplicationConfigService } from "src/app/shared/core/config/application-config.service";
import { BassinvieService } from "./bassinvie.service";
import * as L from 'leaflet';

describe('BassinvieService', () => {
  let service: BassinvieService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        BassinvieService,
        { 
          provide: ApplicationConfigService, 
          useValue: { getEndpointFor: (path: string) => `api${path}` } 
        }
      ],
      imports: [HttpClientTestingModule]
    });

    service = TestBed.inject(BassinvieService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should create', () => {
    expect(service).toBeTruthy();
  });

  // Test: vérifie si la méthode findAll() renvoie toutes les FeatureCollection
  it('should get all FeatureCollection using findAll', () => {
    // Création d'un objet mockFeatureCollection pour simuler les données renvoyées par l'API  
    const mockFeatureCollection: FeatureCollection = {
      type: 'FeatureCollection',
      features: []
    };
    // Appel à la méthode findAll() du service et vérification des données retournées  
    service.findAll().subscribe(data => {
      // L'objet retourné par le service doit être égal à l'objet mockFeatureCollection      
      expect(data).toEqual(mockFeatureCollection);
    });
    // Utilisation du contrôleur de test HTTP pour vérifier si une requête a été envoyée avec la bonne URL

    const req = httpTestingController.expectOne('api/bassinvie');
    // Vérification si la méthode de la requête est bien 'GET'
    expect(req.request.method).toBe('GET');
    // Simule la réponse de l'API avec l'objet mockFeatureCollection
    req.flush(mockFeatureCollection);
  });

  // Test: vérifie si la méthode findInBox() renvoie les FeatureCollection dans la zone de délimitation (bounding box)
  it('should get FeatureCollection in bounding box using findInBox', () => {

    // Création d'un objet mockFeatureCollection pour simuler les données renvoyées par l'API   
    const mockFeatureCollection: FeatureCollection = {
      type: 'FeatureCollection',
      features: []
    };

     // Création d'une zone de délimitation (bounding box) et d'une précision pour les tests   
    const bounds = L.latLngBounds(L.latLng(10, 10), L.latLng(20, 20));
    const precision = 5;

    // Appel à la méthode findInBox() du service avec les paramètres définis et vérification des données retournées  
    service.findInBox(bounds, precision).subscribe(data => {
       // L'objet retourné par le service doit être égal à l'objet mockFeatureCollection      
      expect(data).toEqual(mockFeatureCollection);
    });

    // Utilisation du contrôleur de test HTTP pour vérifier si une requête a été envoyée avec la bonne URL et la bonne méthode    
    const req = httpTestingController.expectOne(request => request.url === 'api/bassinvie' && request.method === 'GET');
    // Vérification si les paramètres de la requête sont corrects
    expect(req.request.params.get('bbox')).toEqual('10,10,20,20');
    expect(req.request.params.get('precision')).toEqual(precision.toString());
    // Simule la réponse de l'API avec l'objet mockFeatureCollection
    req.flush(mockFeatureCollection);
  });
});
