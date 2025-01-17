import { Injectable } from "@angular/core";
import { Message } from "primeng/api";
import { Subject } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class ToastService {
    private toastSource = new Subject<Message>();

    toastSource$ = this.toastSource.asObservable();

    sendToastToFront(event: Message) {
        this.toastSource.next(event);
    }
}