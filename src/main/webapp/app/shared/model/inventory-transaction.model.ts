import { IBook } from '@/shared/model/book.model';

import { TransactionType } from '@/shared/model/enumerations/transaction-type.model';
import { ReferenceType } from '@/shared/model/enumerations/reference-type.model';
export interface IInventoryTransaction {
  id?: number;
  transactionType?: TransactionType;
  quantity?: number;
  referenceType?: ReferenceType | null;
  referenceId?: number | null;
  createdAt?: Date | null;
  book?: IBook | null;
}

export class InventoryTransaction implements IInventoryTransaction {
  constructor(
    public id?: number,
    public transactionType?: TransactionType,
    public quantity?: number,
    public referenceType?: ReferenceType | null,
    public referenceId?: number | null,
    public createdAt?: Date | null,
    public book?: IBook | null
  ) {}
}
