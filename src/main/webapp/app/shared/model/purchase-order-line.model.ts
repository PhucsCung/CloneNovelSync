import { IBook } from '@/shared/model/book.model';
import { IPurchaseOrder } from '@/shared/model/purchase-order.model';

export interface IPurchaseOrderLine {
  id?: number;
  quantity?: number;
  unitCost?: number | null;
  book?: IBook | null;
  purchaseOrder?: IPurchaseOrder | null;
}

export class PurchaseOrderLine implements IPurchaseOrderLine {
  constructor(
    public id?: number,
    public quantity?: number,
    public unitCost?: number | null,
    public book?: IBook | null,
    public purchaseOrder?: IPurchaseOrder | null
  ) {}
}
