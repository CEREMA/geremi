import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, catchError, throwError } from "rxjs";
import { ApplicationConfigService } from "src/app/shared/core/config/application-config.service";
import { ImportEtudeDTO } from "../model/import-etude.model";

@Injectable({
    providedIn: 'root'
})
export class ImportEtudeService {

    private url = this.applicationConfigService.getEndpointFor("/importEtude");

    constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {
    }

    importEtudeFiles(files: File[], import_etude: ImportEtudeDTO): Observable<Object> {
        let formdata = new FormData();
        for (const file of files) {
            formdata.append("files", file, file.name);
        }
        formdata.append("importEtudeDTO",JSON.stringify(import_etude))
    
        return this.http.post<Object>(this.url+'/files', formdata)
            .pipe(catchError((error: HttpErrorResponse) => { return throwError(error); }));
    }

    createZoneEtudeFiles(files: File[], nomEtude: string, descEtude: string, anneeRef: string, anneeFin: string, nomTerritoire: string, descTerritoire: string, nomScenario: string, descScenario: string, idProprietaire: number): Observable<Object> {
        let formdata = new FormData();
        for (const file of files) {
            formdata.append("files", file, file.name);
        }
     
        formdata.append("nomEtude", nomEtude);
        formdata.append("descEtude", descEtude);
        formdata.append("anneeRef", anneeRef);
        formdata.append("anneeFin", anneeFin);
        formdata.append("nomTerritoire", nomTerritoire);
        formdata.append("descTerritoire", descTerritoire);
        formdata.append("nomScenario", nomScenario);
        formdata.append("descScenario", descScenario);
        formdata.append("idProprietaire", idProprietaire.toString());
    
        return this.http.post<Object>(this.url+'/bis', formdata)
            .pipe(catchError((error: HttpErrorResponse) => { return throwError(error); }));
    }

    telechargerImportEtudeGpkg(): Observable<Blob> {
        return this.http.get(this.url + '/gpkg', { responseType: 'blob' });
     }
}