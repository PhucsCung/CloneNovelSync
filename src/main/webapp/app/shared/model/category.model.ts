import { IBook } from '@/shared/model/book.model';

export interface ICategory {
  id?: number;
  title?: string;
  desc?: string | null;
  books?: IBook[] | null;
}

export class Category implements ICategory {
  constructor(public id?: number, public title?: string, public desc?: string | null, public books?: IBook[] | null) {}
}
