<template>
  <div>
    <b-dropdown-item to="/category">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.category')">Category</span>
    </b-dropdown-item>
    <b-dropdown-item to="/publisher">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.publisher')">Publisher</span>
    </b-dropdown-item>
    <b-dropdown-item to="/book">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.book')">Book</span>
    </b-dropdown-item>
    <b-dropdown-item to="/purchase-order">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.purchaseOrder')">Purchase Order</span>
    </b-dropdown-item>
    <b-dropdown-item to="/purchase-order-line">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.purchaseOrderLine')">Purchase Order Line</span>
    </b-dropdown-item>
    <b-dropdown-item to="/sales-order">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.salesOrder')">Sales Order</span>
    </b-dropdown-item>
    <b-dropdown-item to="/sales-order-line">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.salesOrderLine')">Sales Order Line</span>
    </b-dropdown-item>
    <b-dropdown-item to="/inventory-balance">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.inventoryBalance')">Inventory Balance</span>
    </b-dropdown-item>
    <b-dropdown-item to="/inventory-transaction">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.inventoryTransaction')">Inventory Transaction</span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>
