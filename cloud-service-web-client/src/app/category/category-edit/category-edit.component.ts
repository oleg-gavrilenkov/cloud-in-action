import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { CategoryService } from '../category.service';
import { Category } from '../category';
import { map, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-category-edit',
  templateUrl: './category-edit.component.html'
})
export class CategoryEditComponent implements OnInit {

  id: string;
  category: Category;
  feedback: any = {};

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private categoryService: CategoryService) {
  }

  ngOnInit() {
    this
      .route
      .params
      .pipe(
        map(p => p.id),
        switchMap(id => {
          this.id = id;
          if (id === 'new') { return of(new Category()); }
          return this.categoryService.findById(id);
        })
      )
      .subscribe(category => {
          this.category = category;
          this.feedback = {};
        },
        err => {
          this.feedback = {type: 'warning', message: 'Error loading'};
        }
      );
  }

  save() {
    const update = this.id !== 'new';
    this.categoryService.save(this.category, update).subscribe(
      category => {
        this.category = category;
        this.feedback = {type: 'success', message: 'Save was successful!'};
        setTimeout(() => {
          this.router.navigate(['/categories']);
        }, 1000);
      },
      err => {
        this.feedback = {type: 'warning', message: 'Error saving'};
      }
    );
  }

  cancel() {
    this.router.navigate(['/categories']);
  }

  unlinkProductFromCategory(indexToRemove: number) {
    this.category.products.forEach((value, index)=> {
      if (index == indexToRemove) {
        this.category.products.splice(indexToRemove, 1);
      }
    });
  }

  linkProductToCategory() {
    if (!this.category.products) {
        this.category.products = [];
    }

    let productCode = (<HTMLInputElement>document.getElementById('productCodeToAdd')).value;
    if (productCode === '') {
      return;
    }
    
    (<HTMLInputElement>document.getElementById('productCodeToAdd')).value = '';
    
    const products = this.category.products;
    if (products.indexOf(productCode) === -1) {
      products.push(productCode);
    }
  }

  refresh() {
    let currentUrl = this.router.url;
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() => {
        this.router.navigate([currentUrl]);
    }); 
  }
}
