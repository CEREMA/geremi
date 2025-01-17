import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Users } from '../entities/users.model';
import { Roles } from '../enums/roles.enums';
import { ApplicationConfigService } from '../core/config/application-config.service';
import { Claims } from "../entities/claims.model";

@Injectable({
  providedIn: 'root',
})
export class UsersService {
  get currentUsers(): Users {
    return this._currentUsers;
  }

  set currentUsers(value: Users) {
    this._currentUsers = value;
  }

  private _currentUsers!: Users;

  constructor(private httpClient: HttpClient, private applicationConfigService: ApplicationConfigService) {
  }

  getCurrentUsers(): Observable<HttpResponse<Users>> {
    const url = '/user';

    return this.httpClient.get<Users>(this.applicationConfigService.getEndpointFor(url), {observe: 'response'});
  }

  //TODO
  userHasRole(...roles: Roles[]): boolean {
    return true;
  }

  getAllAdminGeremi(): Observable<Users[]> {
    return this.httpClient.get<Users[]>(this.applicationConfigService.getEndpointFor('/user/admin-geremi'));
  }

  getAllAdminGeremiWithoutAuthentication(): Observable<Users[]> {
    return this.httpClient.get<Users[]>(this.applicationConfigService.getEndpointForApiOpen('/user/admin-geremi'));
  }

  addInfoFromClaims(identityClaims: Claims) {
    this._currentUsers.nom = identityClaims.family_name;
    this._currentUsers.prenom = identityClaims.given_name;
  }
}
