import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthGuardService } from '../services/auth-guard.service';
import { ManageProductComponent } from './manage-product.component';
import { AddProductComponent } from './add-product/add-product.component';
const routes: Routes = [ {
  path: '', component: ManageProductComponent,
  canActivate: [AuthGuardService]
},{
  path: 'addItem', component: AddProductComponent,
  canActivate: [AuthGuardService]
}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ManageProductRoutingModule { }
