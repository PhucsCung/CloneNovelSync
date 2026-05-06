import { Component, Provide, Vue } from 'vue-property-decorator';

import UserService from '@/entities/user/user.service';
import CategoryService from './category/category.service';
import PublisherService from './publisher/publisher.service';
import BookService from './book/book.service';
import PurchaseOrderService from './purchase-order/purchase-order.service';
import PurchaseOrderLineService from './purchase-order-line/purchase-order-line.service';
import SalesOrderService from './sales-order/sales-order.service';
import SalesOrderLineService from './sales-order-line/sales-order-line.service';
import InventoryBalanceService from './inventory-balance/inventory-balance.service';
import InventoryTransactionService from './inventory-transaction/inventory-transaction.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

@Component
export default class Entities extends Vue {
  @Provide('userService') private userService = () => new UserService();
  @Provide('categoryService') private categoryService = () => new CategoryService();
  @Provide('publisherService') private publisherService = () => new PublisherService();
  @Provide('bookService') private bookService = () => new BookService();
  @Provide('purchaseOrderService') private purchaseOrderService = () => new PurchaseOrderService();
  @Provide('purchaseOrderLineService') private purchaseOrderLineService = () => new PurchaseOrderLineService();
  @Provide('salesOrderService') private salesOrderService = () => new SalesOrderService();
  @Provide('salesOrderLineService') private salesOrderLineService = () => new SalesOrderLineService();
  @Provide('inventoryBalanceService') private inventoryBalanceService = () => new InventoryBalanceService();
  @Provide('inventoryTransactionService') private inventoryTransactionService = () => new InventoryTransactionService();
  // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
}
