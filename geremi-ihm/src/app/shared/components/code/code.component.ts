import { Component, Input, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HighlightModule } from "ngx-highlightjs";

@Component({
  selector: 'app-code',
  template: `
    <pre><code class="border-round" [highlight]="code"></code></pre>
    `
})
export class AppCodeComponent {
  @Input() code: string;

}

@NgModule({
  imports: [CommonModule, HighlightModule],
  exports: [AppCodeComponent],
  declarations: [AppCodeComponent]
})
export class AppCodeModule {
}
