import { ICategory } from '@/shared/model/category.model';
import { IPublisher } from '@/shared/model/publisher.model';

export interface IBook {
  id?: number;
  code?: string;
  title?: string;
  author?: string | null;
  retailPrice?: number | null;
  categories?: ICategory[] | null;
  publisher?: IPublisher | null;
}

export class Book implements IBook {
  constructor(
    public id?: number,
    public code?: string,
    public title?: string,
    public author?: string | null,
    public retailPrice?: number | null,
    public categories?: ICategory[] | null,
    public publisher?: IPublisher | null
  ) {}
}
