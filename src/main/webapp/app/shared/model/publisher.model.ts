import { IBook } from '@/shared/model/book.model';

export interface IPublisher {
  id?: number;
  code?: string;
  name?: string;
  address?: string | null;
  phone?: string | null;
  books?: IBook[] | null;
}

export class Publisher implements IPublisher {
  constructor(
    public id?: number,
    public code?: string,
    public name?: string,
    public address?: string | null,
    public phone?: string | null,
    public books?: IBook[] | null
  ) {}
}
