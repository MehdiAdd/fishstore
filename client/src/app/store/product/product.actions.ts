import { Action } from '@ngrx/store';
import { HttpError } from '../app.reducers';
import {  Products } from '../model';
import { SET_CART } from '../cart/cart.actions';

// export const ADD_TO_CART = 'ADD_TO_CART';
export const INCREMENT_PRODUCT = 'INCREMENT_PRODUCT';
export const DECREMENT_PRODUCT = 'DECREMENT_PRODUCT';
export const REMOVE_FROM_PRODUCT = 'REMOVE_FROM_PRODUCT';

export const FETCH_PRODUCT = 'FETCH_PRODUCT';
export const FETCH_PRODUCT_SUCCESS = 'FETCH_PRODUCT_SUCCESS';

export const PRODUCT_ERROR = 'PRODUCT_ERROR';
export const SET_PRODUCT = 'SET_PRODUCT';

// export class AddToCart implements Action {
//   readonly type = ADD_TO_CART;

//   constructor(public payload: { id: number, amount: string }) {
//   }
// }

export class IncrementProduct implements Action {
  readonly type = INCREMENT_PRODUCT;

  constructor(public payload: { id: number, amount: string }) {
  }
}

export class DecrementProduct implements Action {
  readonly type = DECREMENT_PRODUCT;

  constructor(public payload: { id: number, amount: string }) {
  }
}



export class RemoveFromProduct implements Action {
  readonly type = REMOVE_FROM_PRODUCT;

  constructor(public payload: number) {
  } // index of the product in the list to be deleted
}




export class FetchProduct implements Action {
  readonly type = FETCH_PRODUCT;
}

export class FetchProductSuccess implements Action {
  readonly type = FETCH_PRODUCT_SUCCESS;

  constructor(public payload: { product: Products, effect: string }) {
  }
}
export class SetProduct implements Action {
  readonly type = SET_PRODUCT;

  constructor(public payload: { product: Products, effect: string }) {
  }
}




export class ProductError implements Action {
  readonly type = PRODUCT_ERROR;

  constructor(public payload: HttpError) {
  }
}


export type ProductActions =  IncrementProduct | DecrementProduct | FetchProduct | FetchProductSuccess 
  | RemoveFromProduct |  ProductError | SetProduct;
