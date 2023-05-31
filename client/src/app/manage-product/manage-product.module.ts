import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ManageProductRoutingModule } from './manage-product-routing.module';
import { ManageProductComponent } from './manage-product.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ReactiveFormsModule } from '@angular/forms';
import { AddProductComponent } from './add-product/add-product.component';

@NgModule({
  declarations: [ManageProductComponent, AddProductComponent],
  imports: [
    CommonModule,
    ManageProductRoutingModule,
    ReactiveFormsModule,
    NgbModule
    
  ]
})
export class ManageProductModule { }
