import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, Subject, catchError, map, tap, throwError } from "rxjs";
import { ApplicationConfigService } from "src/app/shared/core/config/application-config.service";
import { ContrainteEnvironnementale } from "../model/contrainteEnv.model";
import { FeatureCollection } from "geojson";

@Injectable({
    providedIn: 'root'
})
export class ContrainteEnvironnetaleService {

    private contrainteEnvironnetaleResetSource = new Subject();
    private contrainteEnvironnetaleRemoveSource = new Subject<ContrainteEnvironnementale>();
    private contrainteEnvironnetaleChangeOpacitySource = new Subject<ContrainteEnvironnementale>();
    private contrainteEnvironnetaleInitContrainteSource = new Subject();

    private url = this.applicationConfigService.getEndpointFor("/contrainteEnvironnementale");
    contrainteEnvironnetaleResetSource$ = this.contrainteEnvironnetaleResetSource.asObservable();
    contrainteEnvironnetaleRemoveSource$ = this.contrainteEnvironnetaleRemoveSource.asObservable();
    contrainteEnvironnetaleChangeOpacitySource$ = this.contrainteEnvironnetaleChangeOpacitySource.asObservable();
    contrainteEnvironnetaleInitContrainteSource$ = this.contrainteEnvironnetaleInitContrainteSource.asObservable();

    constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {
    }

    getContrainteEnvironnementale(idTerritoire:number): Observable<ContrainteEnvironnementale[]> {
        let response = this.http.get<FeatureCollection>(this.url+'/existante/'+idTerritoire).pipe(
            map(contraintes => {
                let cont = [];
                for(let f in contraintes.features){
                    let contrainte = new ContrainteEnvironnementale();
                    contrainte.id = contraintes.features[f].id as number;
                    contrainte.nom = contraintes.features[f].properties?.nom;
                    contrainte.niveau = contraintes.features[f].properties?.niveau;
                    contrainte.descrip = contraintes.features[f].properties?.descrip;
                    contrainte.idSource = contraintes.features[f].properties?.id_source;
                    contrainte.afficher = false;
                    contrainte.supprimable = false;
                    contrainte.feature = contraintes.features[f];

                    cont.push(contrainte);
                }
                return cont;
            })
        );
        return response;
    }

    getContrainteEtude(idEtude:number): Observable<ContrainteEnvironnementale[]> {
        let response = this.http.get<FeatureCollection>(this.url+'/etude/'+idEtude).pipe(
            map(contraintes => {
                let cont = [];
                for(let f in contraintes.features){
                    let contrainte = new ContrainteEnvironnementale();
                    contrainte.id = contraintes.features[f].id as number;
                    contrainte.nom = contraintes.features[f].properties?.nom;
                    contrainte.niveau = contraintes.features[f].properties?.niveau;
                    contrainte.descrip = contraintes.features[f].properties?.descrip;
                    contrainte.idSource = contraintes.features[f].properties?.id_source;
                    contrainte.afficher = true;
                    contrainte.supprimable = contrainte.idSource === undefined ? true : contrainte.idSource === null;
                    contrainte.feature = contraintes.features[f];

                    cont.push(contrainte);
                }
                return cont;
            })
        );
        return response;
    }

    updateContrainte(idContrainte:number,idEtude:number): Observable<Object> {
        return this.http.post<Object>(this.applicationConfigService.getEndpointFor('/duplicateContrainte/')+idContrainte+'/'+idEtude,'');
    }

    deleteContrainte(idContrainte:number): Observable<Object> {
        return this.http.delete<Object>(this.applicationConfigService.getEndpointFor('/deleteContrainte/')+idContrainte);
    }

    changeOpacity(contrainte:ContrainteEnvironnementale) {
        this.contrainteEnvironnetaleChangeOpacitySource.next(contrainte);
    }

    initContrainte(){
        this.contrainteEnvironnetaleInitContrainteSource.next(true);
    }

    removeFromMap(contrainte:ContrainteEnvironnementale){
        this.contrainteEnvironnetaleRemoveSource.next(contrainte);
    }

    reset() {
        this.contrainteEnvironnetaleResetSource.next(false);
    }

    telechargerContrainteGpkg(): Observable<Blob> {
        return this.http.get(this.url + '/gpkg', { responseType: 'blob' });
     }

    importContrainteEnvironnementaleFiles(files: File[], idEtude: number): Observable<Object> {
        let formdata = new FormData();
        for (const file of files) {
            formdata.append("files", file, file.name);
        }

        return this.http.post<Object>(this.url+'/files/'+idEtude, formdata)
            .pipe(catchError((error: HttpErrorResponse) => { return throwError(error); }));
    }

}
