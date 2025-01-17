import { Injectable } from "@angular/core";
import { Subject } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class OverlayService {
    private overlayOpenSource = new Subject<String>();
    private overlayCloseSource = new Subject();

    overlayOpenSource$ = this.overlayOpenSource.asObservable();
    overlayCloseSource$ = this.overlayCloseSource.asObservable();
    
    constructor() { }

    overlayOpen(value:String){
        return this.overlayOpenSource.next(value);
    }

    overlayClose(){
        return this.overlayCloseSource.next(true);
    }
}