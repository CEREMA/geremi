import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RelEtudeUserProcuration } from '../model/reletudeuserprocuration.model';
import { ApplicationConfigService } from 'src/app/shared/core/config/application-config.service';


@Injectable({
  providedIn: 'root'
})
export class RelEtudeUserProcurationService {


  private url = this.applicationConfigService.getEndpointFor("/procuration");

  constructor(private httpClient: HttpClient, private applicationConfigService: ApplicationConfigService) { }


  getRelEtudeUserProcurationById(id: number): Observable<RelEtudeUserProcuration> {
    return this.httpClient.get<RelEtudeUserProcuration>(`${this.url}/${id}`);
  }

  addRelEtudeUserProcuration(relEtudeUserProcuration: RelEtudeUserProcuration): Observable<RelEtudeUserProcuration> {
    return this.httpClient.post<RelEtudeUserProcuration>(`${this.url}`, relEtudeUserProcuration);
  }

  updateRelEtudeUserProcuration(idEtude: number, relEtudeUserProcuration: RelEtudeUserProcuration[]): Observable<RelEtudeUserProcuration[]> {
    return this.httpClient.put<RelEtudeUserProcuration[]>(`${this.url}/update/${idEtude}`, relEtudeUserProcuration);
  }


}
