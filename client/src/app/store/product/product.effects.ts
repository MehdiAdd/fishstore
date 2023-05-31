import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { Router } from '@angular/router';
import * as ProductActions from './product.actions';
import { of } from 'rxjs';
import { ProductService } from '../../services/product.service';
import { catchError, map, switchMap } from 'rxjs/operators';


@Injectable()
export class ProductEffects {


  @Effect()
  fetchCart = this.actions$
    .pipe(ofType(ProductActions.FETCH_PRODUCT),
      switchMap((action: ProductActions.FetchProduct) => {
        return this.productService.getManagedProducts()
          .pipe(map(res => ({
            type: ProductActions.FETCH_PRODUCT_SUCCESS, payload: { product: res, effect: ProductActions.FETCH_PRODUCT }
          })), catchError(error => of(new ProductActions.ProductError({ error, errorEffect: ProductActions.FETCH_PRODUCT }))));
      }));


  // @Effect()
  // addToCart = this.actions$
  //   .pipe(ofType(ProductActions.ADD_TO_CART),
  //     map((action: ProductActions.AddToCart) => {
  //       return action.payload;
  //     }),
  //     switchMap((payload: { id: number, amount: string }) => {
  //       return this.productService.postCart(payload.id, payload.amount)
  //         .pipe(map(res => {
  //           this.router.navigate(['/cart']);
  //           return { type: ProductActions.SET_CART, payload: { cart: res, effect: ProductActions.ADD_TO_CART } };
  //         }),
  //           catchError(error => of(new ProductActions.CartError({ error, errorEffect: ProductActions.ADD_TO_CART }))));
  //     })
  //   );

  @Effect()
  incrementCart = this.actions$
    .pipe(ofType(ProductActions.INCREMENT_PRODUCT),
      map((action: ProductActions.IncrementProduct) => {
        return action.payload;
      }),
      switchMap((payload: { id: number, amount: string }) => {
        return this.productService.incrementCartItem(payload.id, payload.amount)
          .pipe(map(res => (
            { type: ProductActions.SET_PRODUCT, payload: { product: res, effect: ProductActions.INCREMENT_PRODUCT } }
          )),
            catchError(error => of(new ProductActions.ProductError({ error, errorEffect: ProductActions.INCREMENT_PRODUCT}))));
      })
    );

  @Effect()
  decrementCart = this.actions$
    .pipe(ofType(ProductActions.DECREMENT_PRODUCT),
      map((action: ProductActions.DecrementProduct) => {
        return action.payload;
      }),
      switchMap((payload: { id: number, amount: string }) => {
        return this.productService.decrementCartItem(payload.id, payload.amount)
          .pipe(map(res => (
            { type: ProductActions.SET_PRODUCT, payload: { product: res, effect: ProductActions.DECREMENT_PRODUCT } }
          )),
            catchError(error => of(new ProductActions.ProductError({ error, errorEffect: ProductActions.DECREMENT_PRODUCT }))));
      })
    );


  @Effect()
  removeFromCart = this.actions$
    .pipe(ofType(ProductActions.REMOVE_FROM_PRODUCT),
      map((action: ProductActions.RemoveFromProduct) => {
        return action.payload;
      }),
      switchMap((id: number) => {
        return this.productService.removeFromCart(id)
          .pipe(map(res => ({ type: ProductActions.SET_PRODUCT, payload: { product: res, effect: ProductActions.REMOVE_FROM_PRODUCT } })),
            catchError(error => of(new ProductActions.ProductError({ error, errorEffect: ProductActions.REMOVE_FROM_PRODUCT }))));
      })
    );

  // @Effect()
  // applyDiscountCode = this.actions$
  //   .pipe(ofType(ProductActions.APPLY_DISCOUNT),
  //     map((action: ProductActions.ApplyDiscount) => {
  //       return action.payload;
  //     }),
  //     switchMap((code: string) => {
  //       return this.productService.applyDiscount(code)
  //         .pipe(switchMap(res => ([
  //           {
  //             type: ProductActions.APPLY_DISCOUNT_SUCCESS, payload: { effect: ProductActions.APPLY_DISCOUNT }
  //           },
  //           { type: ProductActions.FETCH_CART }
  //         ])),
  //           catchError(error => of(new ProductActions.CartError({ error, errorEffect: ProductActions.APPLY_DISCOUNT }))));
  //     }));

  // @Effect()
  // emptyCart = this.actions$
  //   .pipe(ofType(ProductActions.EMPTY_CART),
  //     switchMap((action: ProductActions.EmptyCart) => {
  //       return this.productService.emptyCart()
  //         .pipe(map(res => ({ type: ProductActions.EMPTY_CART_SUCCESS, payload: res })),
  //           catchError(error => of(new ProductActions.CartError({ error, errorEffect: ProductActions.EMPTY_CART }))
  //           ));
  //     }));


  constructor(private actions$: Actions, private productService: ProductService, private router: Router) {
  }
}
