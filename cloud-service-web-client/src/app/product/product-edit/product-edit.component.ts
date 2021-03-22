import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { ProductService } from '../product.service';
import { Product } from '../product';
import { map, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-product-edit',
  templateUrl: './product-edit.component.html'
})
export class ProductEditComponent implements OnInit {

  id: string;
  product: Product;
  feedback: any = {};

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService) {
  }

  ngOnInit() {
    this
      .route
      .params
      .pipe(
        map(p => p.id),
        switchMap(id => {
          this.id = id;
          if (id === 'new') { return of(new Product()); }
          return this.productService.findById(id);
        })
      )
      .subscribe(product => {
          this.product = product;
          this.feedback = {};
        },
        err => {
          this.feedback = {type: 'warning', message: 'Error loading'};
        }
      );
  }

  save() {
    console.log(this.id);
    const update = this.id !== 'new';
    this.productService.save(this.product, update).subscribe(
      product => {
        this.product = product;
        this.feedback = {type: 'success', message: 'Save was successful!'};
        setTimeout(() => {
          this.router.navigate(['/products']);
        }, 1000);
      },
      err => {
        this.feedback = {type: 'warning', message: 'Error saving'};
      }
    );
  }

  cancel() {
    this.router.navigate(['/products']);
  }

  refresh() {
    let currentUrl = this.router.url;
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() => {
        this.router.navigate([currentUrl]);
    }); 
  }

  unlinkCategoryFromProduct(indexToRemove: number) {
    this.product.categories.forEach((value, index)=> {
      if (index == indexToRemove) {
        this.product.categories.splice(indexToRemove, 1);
      }
    });
  }

  linkCategoryToProduct() {
    if (!this.product.categories) {
      this.product.categories = [];
    }

    let categoryCode = (<HTMLInputElement>document.getElementById('categoryCodeToAdd')).value;
    if (categoryCode === '') {
      return;
    }
    
    (<HTMLInputElement>document.getElementById('categoryCodeToAdd')).value = '';
    
    const categories = this.product.categories;
    if (categories.indexOf(categoryCode) === -1) {
      categories.push(categoryCode);
    }
  }
}
