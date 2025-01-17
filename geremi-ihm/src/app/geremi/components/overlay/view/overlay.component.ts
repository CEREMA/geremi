import {ChangeDetectorRef, Component, OnInit} from "@angular/core";
import {OverlayService} from "../service/overlay.service";

@Component({
    selector: 'overlay',
    templateUrl: './overlay.component.html',
    styleUrls: ['./overlay.component.scss'],
    providers: []
})
export class OverlayComponent implements OnInit{

    message:String;
    overlay:Boolean = false;
    constructor(private cdr: ChangeDetectorRef,private overlayService:OverlayService) {}

    ngOnInit(): void {
        this.overlayService.overlayOpenSource$.subscribe(value => {
            this.message = value;
            this.overlay = true;
            this.cdr.detectChanges();
        })

        this.overlayService.overlayCloseSource$.subscribe(value => {
            this.overlay = false;
            this.cdr.detectChanges();
        })
    }
}
