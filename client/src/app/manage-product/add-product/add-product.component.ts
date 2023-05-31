import { Component, OnInit } from '@angular/core';
import { User } from './../../store/model';
import { OrderState } from './../../store/order/order.reducer';

import { FormControl, FormGroup, Validators } from '@angular/forms';
import * as OrderActions from '../../store/order/order.actions';
import * as fromApp from '../../store/app.reducers';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import * as BlankValidators from '../../../utils/validators/blank.validator';
import { AccountService } from '../../services/account.service';
import { take } from 'rxjs/operators';
import { ProductService } from 'src/app/services/product.service';
@Component({
  selector: 'app-add-product',
  templateUrl: './add-product.component.html',
  styleUrls: ['./add-product.component.scss']
})
export class AddProductComponent implements OnInit {

  personalForm: FormGroup;

  innerLoading = false;


  constructor(private store: Store<fromApp.AppState>, private accountService: AccountService, private router: Router, private productService:ProductService) {
  }

  ngOnInit() {
    this.store.dispatch(new OrderActions.SetCheckoutStep(0));
    this.personalForm = new FormGroup({
      name: new FormControl(null, [Validators.pattern('^[a-zA-Z\\s]+$'), Validators.required, BlankValidators.notBlankValidator, Validators.minLength(3), Validators.maxLength(104)]),
      description: new FormControl(null, [ Validators.required, BlankValidators.notBlankValidator, Validators.minLength(3), Validators.maxLength(250)]),
      category: new FormControl(null, [ Validators.required, BlankValidators.notBlankValidator, Validators.minLength(3), Validators.maxLength(104)]),
     
      price: new FormControl(null, [Validators.required, Validators.pattern('[0-9]+'), Validators.minLength(1), Validators.maxLength(12)]),
      cargo_price: new FormControl(null, [Validators.required, Validators.pattern('[0-9]+'), Validators.minLength(1), Validators.maxLength(12)]),
      tax_percent: new FormControl(null, [Validators.required, Validators.pattern('[0-9]+'), Validators.minLength(1), Validators.maxLength(12)]),
      stock: new FormControl(null, [Validators.required, Validators.pattern('[0-9]+'), Validators.minLength(1), Validators.maxLength(12)]),


      image: new FormControl(null, [ Validators.required, BlankValidators.notBlankValidator]),
      thumb: new FormControl(null, [ Validators.required, BlankValidators.notBlankValidator]),

    });

    // this.store.select('order').pipe(take(1)).subscribe((order: OrderState) => {
    //   if (order.personal) {
    //     this.personalForm.patchValue({
    //       shipName: order.personal.shipName,
    //       phone: order.personal.phone
    //     });
    //     this.innerLoading = false;
    //   } else {
    //     this.accountService.getUser().pipe(take(1)).subscribe((data: User) => {
    //       this.personalForm.patchValue({
    //         shipName: (data.firstName ? data.firstName : '') + ' ' + (data.lastName ? data.lastName : ''),
    //         phone: data.phone,
    //       });
    //       this.innerLoading = false;
    //     });
    //   }

    // });


  }


  onSubmitOrderForm() {
    const postData = {
      name: this.personalForm.value.name.trim(),
      category: this.personalForm.value.category.trim(),
      description: this.personalForm.value.description.trim(),
      price: this.personalForm.value.price.trim(),
      cargo_price: this.personalForm.value.cargo_price.trim(),
      tax_percent: this.personalForm.value.tax_percent.trim(),
      stock: this.personalForm.value.stock.trim(),
      image: this.personalForm.value.image.trim(),
      thumb: this.personalForm.value.thumb.trim(),
    };
    this.innerLoading=true;
    this.productService.addProduct(postData).subscribe(x=>{
      console.log(x);
      this.innerLoading=false;
      this.router.navigate(['/manage']);
    })

    
  }

}
