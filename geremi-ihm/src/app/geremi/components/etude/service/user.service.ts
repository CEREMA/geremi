import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApplicationConfigService } from "src/app/shared/core/config/application-config.service";
import {Users} from "../../../../shared/entities/users.model";

@Injectable({
    providedIn: 'root'
})
export class UserService {


    private url = this.applicationConfigService.getEndpointFor("/user");

    constructor(private httpClient: HttpClient, private applicationConfigService: ApplicationConfigService) { }

    getUserByEmail(email: string): Observable<Users> {
        let url = `${this.url}/${email}`;
        return this.httpClient.get<Users>(url);
    }

    getAllUsers(): Observable<Users[]> {
        let url = `${this.url}/alluser`;
        return this.httpClient.get<Users[]>(url);
    }

  getUsersDelegation(): Observable<Users[]> {
    let url = `${this.url}/users-delegation`;
    return this.httpClient.get<Users[]>(url);
  }

}
