import { IBook } from '@/shared/model/book.model';
import { ISalesOrder } from '@/shared/model/sales-order.model';

export interface ISalesOrderLine {
  id?: number;
  quantity?: number;
  unitPrice?: number | null;
  book?: IBook | null;
  salesOrder?: ISalesOrder | null;
}

export class SalesOrderLine implements ISalesOrderLine {
  constructor(
    public id?: number,
    public quantity?: number,
    public unitPrice?: number | null,
    public book?: IBook | null,
    public salesOrder?: ISalesOrder | null
  ) {}
}
