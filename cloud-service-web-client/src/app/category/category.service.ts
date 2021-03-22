import { Category } from './category';
import { CategoryFilter } from './category-filter';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Sort } from '../shared/search/sort';
import { environment } from 'src/environments/environment';

const headers = new HttpHeaders().set('Accept', 'application/json');

@Injectable()
export class CategoryService {
  categoryList: Category[] = [];
  api = `${environment.apiUrl}/categories`;

  constructor(private http: HttpClient) {
  }

  findById(id: string): Observable<Category> {
    const url = `${this.api}/${id}`;
    const params = {};
    return this.http.get<Category>(url, {params, headers});
  }

  load(filter: CategoryFilter): void {
    this.find(filter).subscribe(result => {
        this.categoryList = result;
      },
      err => {
        console.error('error loading', err);
      }
    );
  }

  find(filter: CategoryFilter): Observable<Category[]> {
    let params = new HttpParams();
    if (filter.name) {
      params = params.set("name", filter.name);
    }

    params = params.set("sort", Sort.buildSortString(filter.sort, "products", "desc"))

    return this.http.get<Category[]>(this.api, {params, headers});
  }

  save(entity: Category, update: boolean): Observable<Category> {
    let params = new HttpParams();
    let url = '';
    if (update) {
      url = `${this.api}/${entity.code.toString()}`;
      return this.http.put<Category>(url, entity, {headers, params});
    } else {
      url = `${this.api}`;
      return this.http.post<Category>(url, entity, {headers, params});
    }
  }

  delete(entity: Category): Observable<Category> {
    let params = new HttpParams();
    let url = '';
    if (entity.code) {
      url = `${this.api}/${entity.code.toString()}`;
      params = new HttpParams().set('ID', entity.code.toString());
      return this.http.delete<Category>(url, {headers, params});
    }
    return null;
  }
}

