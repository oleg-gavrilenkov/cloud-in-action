import { Component, OnInit } from '@angular/core';
import { CategoryFilter } from '../category-filter';
import { CategoryService } from '../category.service';
import { Category } from '../category';

@Component({
  selector: 'app-category',
  templateUrl: 'category-list.component.html'
})
export class CategoryListComponent implements OnInit {

  filter = new CategoryFilter();
  selectedCategory: Category;
  feedback: any = {};
  sortBy: string[] = ["name", "products"];
  orders: string[] = ["asc", "desc"];

  get categoryList(): Category[] {
    return this.categoryService.categoryList;
  }

  constructor(private categoryService: CategoryService) {
  }

  ngOnInit() {
    this.search();
  }

  search(): void {
    this.categoryService.load(this.filter);
  }

  select(selected: Category): void {
    this.selectedCategory = selected;
  }

  delete(category: Category): void {
    if (confirm('Are you sure?')) {
      this.categoryService.delete(category).subscribe(() => {
          this.feedback = {type: 'success', message: 'Delete was successful!'};
          setTimeout(() => {
            this.search();
          }, 1000);
        },
        err => {
          this.feedback = {type: 'warning', message: 'Error deleting.'};
        }
      );
    }
  }
}
