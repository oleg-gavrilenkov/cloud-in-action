import { Product } from './product';
import { ProductFilter } from './product-filter';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Sort } from '../shared/search/sort';
import { environment } from 'src/environments/environment';

const headers = new HttpHeaders().set('Accept', 'application/json');

@Injectable()
export class ProductService {
  productList: Product[] = [];
  api = `${environment.apiUrl}/products`;

  constructor(private http: HttpClient) {
    
  }

  findById(id: string): Observable<Product> {
    const url = `${this.api}/${id}`;
    const params = {};
    return this.http.get<Product>(url, {params, headers});
  }

  load(filter: ProductFilter): void {
    this.find(filter).subscribe(result => {
        this.productList = result;
      },
      err => {
        console.error('error loading', err);
      }
    );
  }

  find(filter: ProductFilter): Observable<Product[]> {
    let params = new HttpParams();
    if (filter.name) {
      params = params.set('name', filter.name);
    }
    
    if (filter.priceLow) {
      params = params.set('priceLow', filter.priceLow);
    }

    if (filter.priceHigh) {
      params = params.set('priceHigh', filter.priceHigh);
    }

    params = params.set('sort', Sort.buildSortString(filter.sort, "name", "asc"));

    console.log(params);
    return this.http.get<Product[]>(this.api, {headers, params});
  }

  save(entity: Product, update: boolean): Observable<Product> {
    let params = new HttpParams();
    let url = '';
    if (update) {
      url = `${this.api}/${entity.code.toString()}`;
      return this.http.put<Product>(url, entity, {headers, params});
    } else {
      url = `${this.api}`;
      return this.http.post<Product>(url, entity, {headers: headers, params: params});
    }
  }

  delete(entity: Product): Observable<Product> {
    let params = new HttpParams();
    let url = '';
    if (entity.code) {
      url = `${this.api}/${entity.code.toString()}`;
      return this.http.delete<Product>(url, {headers, params});
    }
    return null;
  }
}

