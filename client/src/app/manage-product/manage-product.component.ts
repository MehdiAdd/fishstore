import { Component, OnInit } from '@angular/core';
import { CartState } from './../store/cart/cart.reducer';

import { Observable, Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import * as fromApp from '../store/app.reducers';
import { Store } from '@ngrx/store';
import * as ProductActions from '../store/product/product.actions';
import * as OrderActions from '../store/order/order.actions';
import { take } from 'rxjs/operators';
import { ProductState } from '../store/product/product.reducer';


@Component({
  selector: 'app-manage-product',
  templateUrl: './manage-product.component.html',
  styleUrls: ['./manage-product.component.scss']
})
export class ManageProductComponent implements OnInit {

  productState: Observable<ProductState>;
  cartItemCountSubscription: Subscription;
  showDiscountInput = false;

  applyCodeShow = false;
  cartItemCount = 0;

  constructor(private store: Store<fromApp.AppState>, private router: Router, private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.productState = this.store.select('product');

    this.store.dispatch(new ProductActions.FetchProduct());
    this.cartItemCountSubscription = this.productState.subscribe(data => {
      console.log("products");
      console.log(data);
             
     
    });
  }

  ngOnDestroy() {
    if (this.cartItemCountSubscription) {
      this.cartItemCountSubscription.unsubscribe();
    }
  }



  goToItem(productUrl) {
    this.router.navigate(['/detail/', productUrl], { relativeTo: this.route });
  }

  removeFromCart(id: number) {
    this.store.dispatch(new ProductActions.RemoveFromProduct(id));
  }

  amountIncrement(id: number) {
    this.store.dispatch(new ProductActions.IncrementProduct({ id, amount: '1' }));
  }

  amountDecrement(id: number) {
    this.store.dispatch(new ProductActions.DecrementProduct({ id, amount: '1' }));
  }

  activatePurchase() {
    this.store.select('auth')
      .pipe(take(1))
      .subscribe(data => {
        if (data.isActive) {
          this.store.dispatch(new OrderActions.IsCheckoutActive(true));
          this.router.navigate(['/manage/addItem'], { relativeTo: this.route });
        } else {
          alert('Votre compte est desactive');
        }
      });
  }
}
