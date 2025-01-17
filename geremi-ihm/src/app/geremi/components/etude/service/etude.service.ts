import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Etude } from '../model/etude.model';
import { ApplicationConfigService } from "src/app/shared/core/config/application-config.service";
import {EtudesUtilisateur} from "../model/etudesUtilisateur.model";
import {Etape} from "../../../../shared/enums/etape.enums";
import {EtatEtape} from "../../../../shared/enums/etatEtape.enums";
import { Region } from '../model/region.model';

@Injectable({
    providedIn: 'root'
})
export class EtudeService {
    private url = this.applicationConfigService.getEndpointFor("/etudes");

    constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) { }

    getAllEtudes(): Observable<EtudesUtilisateur> {
        return this.http.get<EtudesUtilisateur>(this.url);
    }

    getAllEtudesSuivies(): Observable<EtudesUtilisateur> {
        return this.http.get<EtudesUtilisateur>(this.url+'/suivi');
    }

    getAllEtudesPublic(): Observable<Map<Region,Etude[]>> {
        return this.http.get<Map<Region,Etude[]>>(this.url+'/public');
    }

    getEtudeById(id: number): Observable<Etude> {
        return this.http.get<Etude>(this.url+ '/' + id );
    }

    addEtude(etude: Etude): Observable<Etude> {
        return this.http.post<Etude>(this.url, etude);
    }

    updateEtude(id: number, etude: Etude): Observable<Etude> {
        return this.http.put<Etude>(this.url + '/' + id, etude);
    }

    publierEtude(etude: Etude): Observable<Etude> {
        return this.http.put<Etude>(this.url + '/publier', etude);
    }

    addTracabiliteEtape(id: number, etape : Etape , etatEtape : EtatEtape): Observable<Etude> {
      return this.http.put<Etude>(this.url + '/' + id + '/tracabilite/' + etape + '/' + etatEtape, {});
    }

    deleteEtude(id: number): Observable<void> {
        return this.http.delete<void>(this.url+'/'+id);
    }

    telechargerODS(id: number): Observable<Blob> {
        let params = new HttpParams().set('id', id.toString());
        return this.http.get(this.url + '/' + id + '/ods', { responseType: 'blob', params });
    }

    uploadFile(id: number, file: File): Observable<Object> {
        const formData = new FormData();
        formData.append('file', file);
        return this.http.post<Object>(`${this.url}/${id}/uploadfile`, formData).pipe( catchError((error: HttpErrorResponse) => {  
            return throwError(error);
        }));
      }

    telechargerZonesGpkg(): Observable<Blob> {
       return this.http.get(this.url + '/gpkg', { responseType: 'blob' });
    }
}
