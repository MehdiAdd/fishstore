import { AuthState } from './../store/auth/auth.reducer';
import { CartState } from './../store/cart/cart.reducer';
import { ProductState } from './../store/product/product.reducer';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import * as fromApp from '../store/app.reducers';
import * as CartActions from '../store/cart/cart.actions';
import * as ProductActions from '../store/product/product.actions';
import * as OrderActions from '../store/order/order.actions';
import * as AuthActions from '../store/auth/auth.actions';
import { Observable, Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbDropdownConfig } from '@ng-bootstrap/ng-bootstrap';
import { take } from 'rxjs/operators';
import { AccountService } from '../services/account.service';


@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
  providers: [NgbDropdownConfig]
})
export class HeaderComponent implements OnInit, OnDestroy {

  cartState: Observable<CartState>;
  productState: Observable<ProductState>;
  authState: Observable<AuthState>;

  cartItemCountSubscription: Subscription;
  cartItemCount = 0;
  isCollapsed = true;
  checkAdmin=false;
  authStateSubscription: Subscription;

  constructor(
    private store: Store<fromApp.AppState>,
    private router: Router,
    private route: ActivatedRoute,
    public dropdownConfig: NgbDropdownConfig,
    private accountService: AccountService) {
    dropdownConfig.placement = 'bottom-right';
  }

  ngOnInit() {

    this.authState = this.store.select('auth');
    this.cartState = this.store.select('cart');
  //  this.productState = this.store.select('product');

    this.authStateSubscription = this.authState
      .subscribe((data) => {
        if (data.authenticated) {
          this.store.dispatch(new CartActions.FetchCart());
         // this.store.dispatch(new ProductActions.FetchProduct());
          this.cartItemCountSubscription = this.cartState.subscribe(data => {

            console.log("carts header");
            console.log(data);
            if (data.cart && data.cart.cartItems.length) {
              this.cartItemCount = data.cart.cartItems.reduce((total, cartItem) => total + cartItem.amount, 0);
            } else {
              this.cartItemCount = 0;
            }
          });
        }
        else {
          if (this.cartItemCountSubscription) {
            this.cartItemCountSubscription.unsubscribe();
          }
        }
      }
      );

      this.accountService.checkAdmin().subscribe((data)=>{
        if(data && data == true) this.checkAdmin=true;
      })
  }

  ngOnDestroy() {
    if (this.authStateSubscription) {
      this.authStateSubscription.unsubscribe();
    }
    if (this.cartItemCountSubscription) {
      this.cartItemCountSubscription.unsubscribe();
    }
  }


  userSignOut() {
    this.store.dispatch(new AuthActions.SignOut());
    this.router.navigate(['/']);
  }


  searchProduct(search: HTMLInputElement) {
    if (search.value.trim().length) {
      const url = '/search/' + search.value;
      this.router.navigate([url]);
    }
  }

  activatePurchase() {
    this.store.select('auth')
      .pipe(take(1))
      .subscribe(data => {
        if (data.isActive) {
          this.store.dispatch(new OrderActions.IsCheckoutActive(true));
          this.router.navigate(['/checkout/personal'], { relativeTo: this.route });
        } else {
          alert('Votre compte est inactif. Vous devez activer votre compte pour pouvoir acheter.');
        }
      });
  }
}
