import { Injectable } from "@angular/core";

@Injectable()
export class ZoomService {

    private zoomMini: Map<String,number> = new Map();

    constructor(){
        this.zoomMini.set('commune',11);
    }

    getZoomMini(type: string): number {
        return this.zoomMini.get(type) as number;
    }
}