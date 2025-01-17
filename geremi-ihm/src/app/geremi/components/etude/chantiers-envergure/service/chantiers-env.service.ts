import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, Subject, catchError, map, throwError } from "rxjs";
import { ApplicationConfigService } from "src/app/shared/core/config/application-config.service";
import { Chantier } from "../model/chantiers.model";
import { Feature, FeatureCollection } from "geojson";

@Injectable({
    providedIn: 'root'
})
export class ChantiersService {
    private chantierResetSource = new Subject();
    private chantierRemoveSource = new Subject<Chantier>();
    private chantierChangeClassAffichageSource = new Subject<Chantier>();
    private chantierChangeFeatureSource = new Subject<Chantier>();
    private chantierInitSource = new Subject();

    private url = this.applicationConfigService.getEndpointFor("/chantierEnvergure");

    chantierResetSource$ = this.chantierResetSource.asObservable();
    chantierRemoveSource$ = this.chantierRemoveSource.asObservable();
    chantierChangeClassAffichageSource$ = this.chantierChangeClassAffichageSource.asObservable();
    chantierChangeFeatureSource$ = this.chantierChangeFeatureSource.asObservable();
    chantierInitSource$ = this.chantierInitSource.asObservable();

    constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {
    }

    getChantiers(idTerritoire:number,idEtude:number): Observable<Chantier[]> {
        let response = this.http.get<FeatureCollection>(this.url+'/existant/'+idTerritoire+'/'+idEtude).pipe(
            map(chantiers => {
                let chan = [];
                for(let f in chantiers.features){
                    let chantier = new Chantier();
                    chantier.id_chantier = chantiers.features[f].id as number;
                    chantier.nom = chantiers.features[f].properties?.nom;
                    chantier.description = chantiers.features[f].properties?.description;
                    chantier.annee_debut = chantiers.features[f].properties?.annee_debut;
                    chantier.annee_fin = chantiers.features[f].properties?.annee_fin;
                    chantier.viab_autre = chantiers.features[f].properties?.viab_autre;
                    chantier.beton_pref = chantiers.features[f].properties?.beton_pref;
                    chantier.ton_tot = chantiers.features[f].properties?.ton_tot;
                    chantier.id_source = chantiers.features[f].properties?.id_source;
                    chantier.id_frere = chantiers.features[f].properties?.id_frere;
                    chantier.libelle_pere = chantiers.features[f].properties?.libelle_pere;

                    chantier.afficher = false;
                    chantier.supprimable = false;
                    chantier.feature = chantiers.features[f];

                    chan.push(chantier);
                }
                return chan;
            })
        );
        return response;
    }

    getChantiersEtude(idEtude:number): Observable<Chantier[]> {
        let response = this.http.get<FeatureCollection>(this.url+'/etude/'+idEtude).pipe(
            map(chantiers => {
                let chan = [];
                for(let f in chantiers.features){
                    let chantier = new Chantier();
                    chantier.id_chantier = chantiers.features[f].id as number;
                    chantier.nom = chantiers.features[f].properties?.nom;
                    chantier.description = chantiers.features[f].properties?.description;
                    chantier.annee_debut = chantiers.features[f].properties?.annee_debut;
                    chantier.annee_fin = chantiers.features[f].properties?.annee_fin;
                    chantier.viab_autre = chantiers.features[f].properties?.viab_autre;
                    chantier.beton_pref = chantiers.features[f].properties?.beton_pref;
                    chantier.ton_tot = chantiers.features[f].properties?.ton_tot;
                    chantier.id_source = chantiers.features[f].properties?.id_source;
                    chantier.id_frere = chantiers.features[f].properties?.id_frere;
                    chantier.libelle_pere = chantiers.features[f].properties?.libelle_pere;

                    chantier.afficher = true;
                    chantier.supprimable = chantier.id_source === undefined ? true : chantier.id_source === null;
                    chantier.feature = chantiers.features[f];

                    chan.push(chantier);
                }
                return chan;
            })
        );
        return response;
    }

    duplicateChantier(idChantier:number,idEtude:number): Observable<Object> {
        return this.http.post<Object>(this.applicationConfigService.getEndpointFor('/duplicateChantier/')+idChantier+'/'+idEtude,'');
    }

    deleteChantier(idChantier:number): Observable<Object> {
        return this.http.delete<Object>(this.applicationConfigService.getEndpointFor('/deleteChantier/')+idChantier);
    }

    modificationChantier(chantier:Chantier): Observable<Object> {
        return this.http.post<Object>(this.applicationConfigService.getEndpointFor('/modificationChantier'), chantier);
    }

    changeClassAffichage(chantier:Chantier) {
        this.chantierChangeClassAffichageSource.next(chantier);
    }

    initChantier(){
        this.chantierInitSource.next(true);
    }

    removeFromMap(chantier:Chantier){
        this.chantierRemoveSource.next(chantier);
    }

    changeFeatureChantier(chantier:Chantier){
        this.chantierChangeFeatureSource.next(chantier);
    }

    reset() {
        this.chantierResetSource.next(false);
    }

    telechargerChantierZip(): Observable<Blob> {
        return this.http.get(this.url + '/zip', { responseType: 'blob' });
     }
    
    importChantierFiles(files: File[], idEtude: number): Observable<Object> {
        let formdata = new FormData();
        for (const file of files) {
            formdata.append("files", file, file.name);
        }
    
        return this.http.post<Object>(this.url+'/files/'+idEtude, formdata)
            .pipe(catchError((error: HttpErrorResponse) => { return throwError(error); }));
    }

    getChantierEnvergureById(id: number): Observable<Chantier> {
        return this.http.get<Feature>(`${this.url}/chercher/${id}`).pipe(
            map(feature => {
                    let chan = new Chantier();
                    chan.id_chantier = feature.id as number;
                    chan.nom = feature.properties?.nom;
                    chan.description = feature.properties?.description;
                    chan.annee_debut = feature.properties?.annee_debut;
                    chan.annee_fin = feature.properties?.annee_fin;
                    chan.viab_autre = feature.properties?.viab_autre;
                    chan.beton_pref = feature.properties?.beton_pref;
                    chan.ton_tot = feature.properties?.ton_tot;
                    chan.id_source = feature.properties?.id_source;
                    chan.id_frere = feature.properties?.id_frere;
                    chan.libelle_pere = feature.properties?.libelle_pere;

                    chan.afficher = false;
                    chan.supprimable = chan.id_source === undefined ? true : chan.id_source === null;
                    chan.feature = feature;

                    return chan;
                }
            )
        );
    }
}