import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore
const Entities = () => import('@/entities/entities.vue');

// prettier-ignore
const Category = () => import('@/entities/category/category.vue');
// prettier-ignore
const CategoryUpdate = () => import('@/entities/category/category-update.vue');
// prettier-ignore
const CategoryDetails = () => import('@/entities/category/category-details.vue');
// prettier-ignore
const Publisher = () => import('@/entities/publisher/publisher.vue');
// prettier-ignore
const PublisherUpdate = () => import('@/entities/publisher/publisher-update.vue');
// prettier-ignore
const PublisherDetails = () => import('@/entities/publisher/publisher-details.vue');
// prettier-ignore
const Book = () => import('@/entities/book/book.vue');
// prettier-ignore
const BookUpdate = () => import('@/entities/book/book-update.vue');
// prettier-ignore
const BookDetails = () => import('@/entities/book/book-details.vue');
// prettier-ignore
const PurchaseOrder = () => import('@/entities/purchase-order/purchase-order.vue');
// prettier-ignore
const PurchaseOrderUpdate = () => import('@/entities/purchase-order/purchase-order-update.vue');
// prettier-ignore
const PurchaseOrderDetails = () => import('@/entities/purchase-order/purchase-order-details.vue');
// prettier-ignore
const PurchaseOrderLine = () => import('@/entities/purchase-order-line/purchase-order-line.vue');
// prettier-ignore
const PurchaseOrderLineUpdate = () => import('@/entities/purchase-order-line/purchase-order-line-update.vue');
// prettier-ignore
const PurchaseOrderLineDetails = () => import('@/entities/purchase-order-line/purchase-order-line-details.vue');
// prettier-ignore
const SalesOrder = () => import('@/entities/sales-order/sales-order.vue');
// prettier-ignore
const SalesOrderUpdate = () => import('@/entities/sales-order/sales-order-update.vue');
// prettier-ignore
const SalesOrderDetails = () => import('@/entities/sales-order/sales-order-details.vue');
// prettier-ignore
const SalesOrderLine = () => import('@/entities/sales-order-line/sales-order-line.vue');
// prettier-ignore
const SalesOrderLineUpdate = () => import('@/entities/sales-order-line/sales-order-line-update.vue');
// prettier-ignore
const SalesOrderLineDetails = () => import('@/entities/sales-order-line/sales-order-line-details.vue');
// prettier-ignore
const InventoryBalance = () => import('@/entities/inventory-balance/inventory-balance.vue');
// prettier-ignore
const InventoryBalanceUpdate = () => import('@/entities/inventory-balance/inventory-balance-update.vue');
// prettier-ignore
const InventoryBalanceDetails = () => import('@/entities/inventory-balance/inventory-balance-details.vue');
// prettier-ignore
const InventoryTransaction = () => import('@/entities/inventory-transaction/inventory-transaction.vue');
// prettier-ignore
const InventoryTransactionUpdate = () => import('@/entities/inventory-transaction/inventory-transaction-update.vue');
// prettier-ignore
const InventoryTransactionDetails = () => import('@/entities/inventory-transaction/inventory-transaction-details.vue');
// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'category',
      name: 'Category',
      component: Category,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'category/new',
      name: 'CategoryCreate',
      component: CategoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'category/:categoryId/edit',
      name: 'CategoryEdit',
      component: CategoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'category/:categoryId/view',
      name: 'CategoryView',
      component: CategoryDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'publisher',
      name: 'Publisher',
      component: Publisher,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'publisher/new',
      name: 'PublisherCreate',
      component: PublisherUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'publisher/:publisherId/edit',
      name: 'PublisherEdit',
      component: PublisherUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'publisher/:publisherId/view',
      name: 'PublisherView',
      component: PublisherDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'book',
      name: 'Book',
      component: Book,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'book/new',
      name: 'BookCreate',
      component: BookUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'book/:bookId/edit',
      name: 'BookEdit',
      component: BookUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'book/:bookId/view',
      name: 'BookView',
      component: BookDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'purchase-order',
      name: 'PurchaseOrder',
      component: PurchaseOrder,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'purchase-order/new',
      name: 'PurchaseOrderCreate',
      component: PurchaseOrderUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'purchase-order/:purchaseOrderId/edit',
      name: 'PurchaseOrderEdit',
      component: PurchaseOrderUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'purchase-order/:purchaseOrderId/view',
      name: 'PurchaseOrderView',
      component: PurchaseOrderDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'purchase-order-line',
      name: 'PurchaseOrderLine',
      component: PurchaseOrderLine,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'purchase-order-line/new',
      name: 'PurchaseOrderLineCreate',
      component: PurchaseOrderLineUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'purchase-order-line/:purchaseOrderLineId/edit',
      name: 'PurchaseOrderLineEdit',
      component: PurchaseOrderLineUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'purchase-order-line/:purchaseOrderLineId/view',
      name: 'PurchaseOrderLineView',
      component: PurchaseOrderLineDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'sales-order',
      name: 'SalesOrder',
      component: SalesOrder,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'sales-order/new',
      name: 'SalesOrderCreate',
      component: SalesOrderUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'sales-order/:salesOrderId/edit',
      name: 'SalesOrderEdit',
      component: SalesOrderUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'sales-order/:salesOrderId/view',
      name: 'SalesOrderView',
      component: SalesOrderDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'sales-order-line',
      name: 'SalesOrderLine',
      component: SalesOrderLine,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'sales-order-line/new',
      name: 'SalesOrderLineCreate',
      component: SalesOrderLineUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'sales-order-line/:salesOrderLineId/edit',
      name: 'SalesOrderLineEdit',
      component: SalesOrderLineUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'sales-order-line/:salesOrderLineId/view',
      name: 'SalesOrderLineView',
      component: SalesOrderLineDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'inventory-balance',
      name: 'InventoryBalance',
      component: InventoryBalance,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'inventory-balance/new',
      name: 'InventoryBalanceCreate',
      component: InventoryBalanceUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'inventory-balance/:inventoryBalanceId/edit',
      name: 'InventoryBalanceEdit',
      component: InventoryBalanceUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'inventory-balance/:inventoryBalanceId/view',
      name: 'InventoryBalanceView',
      component: InventoryBalanceDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'inventory-transaction',
      name: 'InventoryTransaction',
      component: InventoryTransaction,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'inventory-transaction/new',
      name: 'InventoryTransactionCreate',
      component: InventoryTransactionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'inventory-transaction/:inventoryTransactionId/edit',
      name: 'InventoryTransactionEdit',
      component: InventoryTransactionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'inventory-transaction/:inventoryTransactionId/view',
      name: 'InventoryTransactionView',
      component: InventoryTransactionDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
