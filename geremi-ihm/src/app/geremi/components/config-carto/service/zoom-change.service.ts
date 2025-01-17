import { Injectable } from "@angular/core";
import { Subject } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class ZoomChangeService {
    private zoomChangeSource = new Subject<number>();

    zommChangeSource$ = this.zoomChangeSource.asObservable();

    onZoomChange(zoom:number) {
        this.zoomChangeSource.next(zoom);
    }


}
