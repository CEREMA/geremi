import { Injectable } from "@angular/core";

@Injectable()
export class ZoomMiniService {

    private zoomMini: Map<String,number> = new Map();

    constructor(){
        this.zoomMini.set('commune',11);
        this.zoomMini.set('bassindevie',9);
        this.zoomMini.set('epci',9);
    }

    getZoomMini(type: string): number {
        return this.zoomMini.get(type) as number;
    }
}