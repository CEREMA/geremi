import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { catchError, Observable, throwError } from "rxjs";
import { ApplicationConfigService } from "src/app/shared/core/config/application-config.service";

@Injectable({
    providedIn: 'root'
})
export class ImportZonageService {

    private url = this.applicationConfigService.getEndpointFor("/importzonage");

    constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

    importZonageCheck(idEtude: number): Observable<Object> {
        return this.http.get<Object>(`${this.url}/check/${idEtude}`).pipe( catchError((error: HttpErrorResponse) => {  return throwError(error); }));
    }

    importZonageFiles(files: File[], idEtude: number): Observable<Object> {
        let formdata = new FormData();
        for (const file of files) {
            formdata.append("files", file, file.name);
        }

        return this.http.post<Object>(`${this.url}/files/${idEtude}`, formdata).pipe( catchError((error: HttpErrorResponse) => {  return throwError(error); }));
    }
}