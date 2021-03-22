import { Sort } from "../shared/search/sort";

export class ProductFilter {
    name = null;
    priceLow = null;
    priceHigh = null;
    sort: Sort = new Sort("name", "asc");


}
