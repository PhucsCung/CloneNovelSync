import { IPurchaseOrderLine } from '@/shared/model/purchase-order-line.model';
import { IUser } from '@/shared/model/user.model';

import { PurchaseStatus } from '@/shared/model/enumerations/purchase-status.model';
export interface IPurchaseOrder {
  id?: number;
  code?: string;
  status?: PurchaseStatus | null;
  totalAmount?: number | null;
  createdAt?: Date | null;
  lines?: IPurchaseOrderLine[] | null;
  user?: IUser | null;
}

export class PurchaseOrder implements IPurchaseOrder {
  constructor(
    public id?: number,
    public code?: string,
    public status?: PurchaseStatus | null,
    public totalAmount?: number | null,
    public createdAt?: Date | null,
    public lines?: IPurchaseOrderLine[] | null,
    public user?: IUser | null
  ) {}
}
