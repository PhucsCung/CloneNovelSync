import { ISalesOrderLine } from '@/shared/model/sales-order-line.model';
import { IUser } from '@/shared/model/user.model';

import { SalesStatus } from '@/shared/model/enumerations/sales-status.model';
export interface ISalesOrder {
  id?: number;
  code?: string;
  status?: SalesStatus | null;
  totalAmount?: number | null;
  createdAt?: Date | null;
  lines?: ISalesOrderLine[] | null;
  user?: IUser | null;
}

export class SalesOrder implements ISalesOrder {
  constructor(
    public id?: number,
    public code?: string,
    public status?: SalesStatus | null,
    public totalAmount?: number | null,
    public createdAt?: Date | null,
    public lines?: ISalesOrderLine[] | null,
    public user?: IUser | null
  ) {}
}
