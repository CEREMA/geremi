import { TestBed } from '@angular/core/testing';
import { ConfigService } from './config.service';
import { EtablissementService } from '../../carto/service/etablissement.service';
import { of } from 'rxjs';
import { FeatureCollection } from 'geojson';
import { Etablissement } from '../model/Etablissement.model';


// Début du bloc de tests pour ConfigService
describe('ConfigService', () => {
  let service: ConfigService;
  let etablissementServiceSpy: jasmine.SpyObj<EtablissementService>;

  // Configuration avant chaque test
  beforeEach(() => {

     // Crée un espion pour EtablissementService avec la méthode 'findEtablissementInBox'
    const spy = jasmine.createSpyObj('EtablissementService', ['findEtablissementInBox']);

    // Configure le TestBed avec ConfigService et l'espion EtablissementService
    TestBed.configureTestingModule({
      providers: [
        ConfigService,
        { provide: EtablissementService, useValue: spy }
      ]
    });

    // Injecte ConfigService et l'espion EtablissementService dans les variables
    service = TestBed.inject(ConfigService);
    // Configure l'espion pour retourner le mockFeatureCollection lors de l'appel de la méthode 'findEtablissementInBox'
    etablissementServiceSpy = TestBed.inject(EtablissementService) as jasmine.SpyObj<EtablissementService>;
  });

  // Teste si ConfigService est créé correctement
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // Teste la récupération des années uniques à partir des objets Etablissement en utilisant la méthode 'findEtablissement'
  it('should get unique years from Etablissement objects using findEtablissement', async () => {
     // Crée un mockFeatureCollection avec des Etablissements ayant différentes années
    const mockFeatureCollection: FeatureCollection = {
        type: 'FeatureCollection',
        features: [
          {
            type: 'Feature',
            geometry: {
              type: 'Point',
              coordinates: [0, 0]
            },
            properties: { annee: '2021' } as Etablissement
          },
          {
            type: 'Feature',
            geometry: {
              type: 'Point',
              coordinates: [1, 1]
            },
            properties: { annee: '2020' } as Etablissement
          },
          {
            type: 'Feature',
            geometry: {
              type: 'Point',
              coordinates: [2, 2]
            },
            properties: { annee: '2021' } as Etablissement
          }
        ]
      };

    // Configure l'espion pour retourner le mockFeatureCollection lors de l'appel de la méthode 'findEtablissementInBox'
    etablissementServiceSpy.findEtablissementInBox.and.returnValue(of(mockFeatureCollection));
    // Définit le résultat attendu avec les années uniques
    const expectedResult = [
      { label: '2021', value: '2021' },
      { label: '2020', value: '2020' }
    ];
    // Appelle la méthode 'findEtablissement' et vérifie si le résultat correspond au résultat attendu
    const result = await service.findEtablissement();
    expect(result).toEqual(expectedResult);
    // Vérifie si la méthode 'findEtablissementInBox' de l'espion a été appelée
    expect(etablissementServiceSpy.findEtablissementInBox).toHaveBeenCalled();
  });
});
