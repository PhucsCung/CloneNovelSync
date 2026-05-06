import { IBook } from '@/shared/model/book.model';

export interface IInventoryBalance {
  id?: number;
  quantityOnHand?: number;
  updatedAt?: Date | null;
  book?: IBook | null;
}

export class InventoryBalance implements IInventoryBalance {
  constructor(public id?: number, public quantityOnHand?: number, public updatedAt?: Date | null, public book?: IBook | null) {}
}
