import { TestBed } from '@angular/core/testing';

import { UsersService } from './users.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Users } from '../entities/users.model';
import { Roles } from '../enums/roles.enums';
import { mockedAdminGeremiList, mockedUsers } from '../constants/mocks/users-mocks.constants';

describe('UserService', () => {
  let service: UsersService;
  let httpMock: HttpTestingController;
  let resultat: Users | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(UsersService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get current user', () => {
    const valeurAttendue: Users = {
      id_etat: 0,
      id_profil: 0,
      id_region: 0,
      insee_region: "",
      isSelected: false,
      libelle_etat: "",
      libelle_profil: "",
      nom: "",
      nom_region: "",
      prenom: "",
      mail: '',
      id: 0
    };
    service.getCurrentUsers().subscribe(res => {
      resultat = res.body;
    });
    const testRequete = httpMock.expectOne({method: 'GET'});
    testRequete.flush(valeurAttendue);
    expect(resultat).toEqual(valeurAttendue);
  });

  it('should return true when calling userHasRole with Administrateur', () => {
    service.currentUsers = mockedUsers;
    const resultat = service.userHasRole(Roles.Administrateur);
    expect(resultat).toBeTrue();
  });

  it('should return false when calling userHasRole with GestionnaireDeDonnees', () => {
    service.currentUsers = mockedUsers;
    const resultat = service.userHasRole(Roles.GestionnaireDeDonnees);
    expect(resultat).toBeFalse();
  });

  it('should return all geremi admin of the active direction', () => {
    let result: Users[] = [];
    const valeurAttendue = mockedAdminGeremiList;
    service.getAllAdminGeremi().subscribe(res => {
      result = res;
    });
    const testRequete = httpMock.expectOne({method: 'GET'});
    testRequete.flush(valeurAttendue);
    expect(result).toEqual(valeurAttendue);
  });
});
