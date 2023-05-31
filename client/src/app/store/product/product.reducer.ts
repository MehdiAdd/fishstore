import * as ProductActions from './product.actions';
import { HttpError } from '../app.reducers';
import {  Products } from '../model';


export interface ProductState {
  products: Products;
  errors: Array<HttpError>;
  loading: boolean;
  fetchLoading: boolean;
  extra:boolean
}

const initialState: ProductState = {
  products: null,
  errors: [],
  loading: false,
  fetchLoading: false,
  extra:true
};

export function productReducer(state = initialState, action: ProductActions.ProductActions) {
  switch (action.type) {
    case (ProductActions.SET_PRODUCT):
      return {
        products: action.payload.product,
        errors: [...state.errors.filter(error => error.errorEffect !== action.payload.effect)],
        loading: false,
        fetchLoading: state.fetchLoading,
        extra:true
      };

    case (ProductActions.FETCH_PRODUCT):
      return {
        ...state,
        fetchLoading: true
      };

    case (ProductActions.FETCH_PRODUCT_SUCCESS):
      return {
        products: action.payload.product,
        errors: [...state.errors.filter(error => error.errorEffect !== action.payload.effect)],
        loading: false,
        fetchLoading: false,
        extra:true
      };





    
    case (ProductActions.REMOVE_FROM_PRODUCT):

    case (ProductActions.INCREMENT_PRODUCT):
    case (ProductActions.DECREMENT_PRODUCT):
      return {
        ...state,
        loading: true
      };

    case (ProductActions.PRODUCT_ERROR):
      const errors = [...state.errors];
      const index = errors.findIndex(error => error.errorEffect === action.payload.errorEffect);
      if (index !== -1) {
        errors[index] = action.payload;
      } else {
        errors.push(action.payload);
      }
      return {
        ...state,
        loading: false,
        errors
      };



    default:
      return state;
  }
}
