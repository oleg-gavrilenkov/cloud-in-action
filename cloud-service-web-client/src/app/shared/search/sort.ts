export class Sort {
    sortBy = null;
    order = null;

    constructor(sortBy: string, order: string) {
        this.sortBy = sortBy;
        this.order = order;
    }

    static buildSortString(sort: Sort, defaulSortBy: string, defaultOrder: string): string {
        const sortBy = sort.sortBy ? sort.sortBy :  defaulSortBy;
        const order = sort.order ? sort.order : defaultOrder;

        return `${sortBy},${order}`;
    }
}