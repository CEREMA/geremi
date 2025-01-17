import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, Subject, catchError, map, throwError } from "rxjs";
import { ApplicationConfigService } from "src/app/shared/core/config/application-config.service";
import { InstallationStockage } from "../model/installation-stockage.model";
import { Feature, FeatureCollection } from "geojson";
import { InstallationStockageDTO } from "../model/installation-stockage-dto.model";

@Injectable({
    providedIn: 'root'
})
export class InstallationStockageService {

    private installationStockageResetSource = new Subject();
    private installationStockageRemoveSource = new Subject<InstallationStockage>();
    private installationStockageChangeFeatureSource = new Subject<InstallationStockageDTO>();
    private installationStockageNonActiveRemoveSource = new Subject<InstallationStockage>();
    private installationStockageChangeOpacitySource = new Subject<InstallationStockage>();
    private installationStockageNonActiveChangeOpacitySource = new Subject<InstallationStockage>();
    private installationStockageInitSource = new Subject();

    private url = this.applicationConfigService.getEndpointFor("/installationStockage");
    installationStockageResetSource$ = this.installationStockageResetSource.asObservable();
    installationStockageChangeFeatureSource$ = this.installationStockageChangeFeatureSource.asObservable();
    installationStockageRemoveSource$ = this.installationStockageRemoveSource.asObservable();
    installationStockageNonActiveRemoveSource$ = this.installationStockageNonActiveRemoveSource.asObservable();
    installationStockageChangeOpacitySource$ = this.installationStockageChangeOpacitySource.asObservable();
    installationStockageNonActiveChangeOpacitySource$ = this.installationStockageNonActiveChangeOpacitySource.asObservable();
    installationStockageInitSource$ = this.installationStockageInitSource.asObservable();

    constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {
    }

    getInstallationStockage(idTerritoire:number,idEtude:number): Observable<InstallationStockage[]> {
        let response = this.http.get<FeatureCollection>(this.url+'/existante/'+idTerritoire+'/'+idEtude).pipe(
            map(installationStockages => {
                let cont = [];
                for(let f in installationStockages.features){
                    let installationStockage = new InstallationStockage();
                    installationStockage.id = installationStockages.features[f].id as number;
                    installationStockage.nomEtab = installationStockages.features[f].properties?.nom_etab;
                    installationStockage.codeEtab = installationStockages.features[f].properties?.code_etab;
                    installationStockage.description = installationStockages.features[f].properties?.description;
                    installationStockage.anneeDebut = installationStockages.features[f].properties?.annee_debut;
                    installationStockage.anneeFin = installationStockages.features[f].properties?.annee_fin;
                    installationStockage.betonPref = installationStockages.features[f].properties?.beton_pref;
                    installationStockage.viabAutre = installationStockages.features[f].properties?.viab_autre;
                    installationStockage.tonTot = installationStockages.features[f].properties?.ton_tot; 
                    installationStockage.idSource = installationStockages.features[f].properties?.id_source;
                    installationStockage.libellePere = installationStockages.features[f].properties?.libelle_pere;
                    installationStockage.idFrere = installationStockages.features[f].properties?.id_frere;
                    installationStockage.afficher = false;
                    installationStockage.supprimable = false;
                    installationStockage.feature = installationStockages.features[f];

                    cont.push(installationStockage);
                }
                return cont;
            })
        );
        return response;
    }

    getInstallationStockageEtude(idEtude:number): Observable<InstallationStockage[]> {
        let response = this.http.get<FeatureCollection>(this.url+'/etude/'+idEtude).pipe(
            map(installationStockages => {
                let cont = [];
                for(let f in installationStockages.features){
                    let installationStockage = new InstallationStockage();
                    installationStockage.id = installationStockages.features[f].id as number;
                    installationStockage.nomEtab = installationStockages.features[f].properties?.nom_etab;
                    installationStockage.codeEtab = installationStockages.features[f].properties?.code_etab;
                    installationStockage.description = installationStockages.features[f].properties?.description;
                    installationStockage.anneeDebut = installationStockages.features[f].properties?.annee_debut;
                    installationStockage.anneeFin = installationStockages.features[f].properties?.annee_fin;
                    installationStockage.betonPref = installationStockages.features[f].properties?.beton_pref;
                    installationStockage.viabAutre = installationStockages.features[f].properties?.viab_autre;
                    installationStockage.tonTot = installationStockages.features[f].properties?.ton_tot; 
                    installationStockage.idSource = installationStockages.features[f].properties?.id_source;
                    installationStockage.idFrere = installationStockages.features[f].properties?.id_frere;
                    installationStockage.libellePere = installationStockages.features[f].properties?.libelle_pere;
                    installationStockage.afficher = true;
                    installationStockage.supprimable = installationStockage.idSource === undefined ? true : installationStockage.idSource === null;
                    installationStockage.feature = installationStockages.features[f];

                    cont.push(installationStockage);
                }
                return cont;
            })
        );
        return response;
    }

    duplicateInstallationStockage(idStockage:number,idEtude:number): Observable<Object> {
        return this.http.post<Object>(this.applicationConfigService.getEndpointFor('/duplicateInstallationStockage/')+idStockage+'/'+idEtude,'');
    }

    deleteInstallationStockage(idStockage:number): Observable<Object> {
        return this.http.delete<Object>(this.applicationConfigService.getEndpointFor('/deleteInstallationStockage/')+idStockage);
    }

    updateDataInstallationStockage(installationStockageId: number, installationStockageDTO: InstallationStockageDTO): Observable<Object> {
        const endpoint = this.applicationConfigService.getEndpointFor('/updateDataInstallationStockage/') + installationStockageId;
        return this.http.post<Object>(endpoint, installationStockageDTO);
    }
    
    changeOpacity(installationStockage:InstallationStockage) {
        this.installationStockageChangeOpacitySource.next(installationStockage);
    }

    changeOpacityNonActive(installationStockage:InstallationStockage) {
        this.installationStockageNonActiveChangeOpacitySource.next(installationStockage);
    }

    initInstallationStockage(){
        this.installationStockageInitSource.next(true);
    }

    removeFromMap(installationStockage:InstallationStockage){
        this.installationStockageRemoveSource.next(installationStockage);
    }

    removeFromMapNonActive(installationStockage:InstallationStockage){
        this.installationStockageNonActiveRemoveSource.next(installationStockage);
    }

    changeFeatureInstallationStockage(installationStockage:InstallationStockageDTO){
        this.installationStockageChangeFeatureSource.next(installationStockage);
    }

    reset() {
        this.installationStockageResetSource.next(false);
    }

    telechargerInstallationStockageGpkg(): Observable<Blob> {
        return this.http.get(this.url + '/gpkg', { responseType: 'blob' });
     }
    
    importInstallationStockageFiles(files: File[], idEtude: number): Observable<Object> {
        let formdata = new FormData();
        for (const file of files) {
            formdata.append("files", file, file.name);
        }
    
        return this.http.post<Object>(this.url+'/files/'+idEtude, formdata)
            .pipe(catchError((error: HttpErrorResponse) => { return throwError(error); }));
    }

    getInstallationStockageById(id: number): Observable<InstallationStockage> {
        return this.http.get<Feature>(`${this.url}/existante/${id}`).pipe(
            map(feature => {
                    let installationStockage = new InstallationStockage();
                    installationStockage.id = feature.id as number;
                    installationStockage.nomEtab = feature.properties?.nom_etab;
                    installationStockage.codeEtab = feature.properties?.code_etab;
                    installationStockage.description = feature.properties?.description;
                    installationStockage.anneeDebut = feature.properties?.annee_debut;
                    installationStockage.anneeFin = feature.properties?.annee_fin;
                    installationStockage.betonPref = feature.properties?.beton_pref;
                    installationStockage.viabAutre = feature.properties?.viab_autre;
                    installationStockage.tonTot = feature.properties?.ton_tot; 
                    installationStockage.idSource = feature.properties?.id_source;
                    installationStockage.idFrere = feature.properties?.id_frere;
                    installationStockage.libellePere = feature.properties?.libelle_pere;
                    installationStockage.afficher = false;
                    installationStockage.supprimable = installationStockage.idSource === undefined ? true : installationStockage.idSource === null;
                    installationStockage.feature = feature;

                    return installationStockage
                }
            )
        );
    }
    
}