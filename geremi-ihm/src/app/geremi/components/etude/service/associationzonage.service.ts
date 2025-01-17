import { Injectable } from "@angular/core";
import { Feature } from "geojson";
import { Subject } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class AssociationZonageService {
    private associationZonageSource = new Subject<Feature>();
    private associationZonageResetSource = new Subject();

    associationZonageSource$ = this.associationZonageSource.asObservable();
    associationZonageResetSource$ = this.associationZonageResetSource.asObservable();

    onTerritoireStateChange(event: Feature) {
        this.associationZonageSource.next(event);
    }

    reset() {
        this.associationZonageResetSource.next(false);
    }

    resetDREAL() {
        this.associationZonageResetSource.next(true);
    }
}