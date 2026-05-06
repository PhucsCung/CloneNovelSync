<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="purchaseOrderLine">
        <h2 class="jh-entity-heading" data-cy="purchaseOrderLineDetailsHeading">
          <span v-text="$t('cloneNovelSyncApp.purchaseOrderLine.detail.title')">PurchaseOrderLine</span> {{ purchaseOrderLine.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('cloneNovelSyncApp.purchaseOrderLine.quantity')">Quantity</span>
          </dt>
          <dd>
            <span>{{ purchaseOrderLine.quantity }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.purchaseOrderLine.unitCost')">Unit Cost</span>
          </dt>
          <dd>
            <span>{{ purchaseOrderLine.unitCost }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.purchaseOrderLine.book')">Book</span>
          </dt>
          <dd>
            <div v-if="purchaseOrderLine.book">
              <router-link :to="{ name: 'BookView', params: { bookId: purchaseOrderLine.book.id } }">{{
                purchaseOrderLine.book.title
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.purchaseOrderLine.purchaseOrder')">Purchase Order</span>
          </dt>
          <dd>
            <div v-if="purchaseOrderLine.purchaseOrder">
              <router-link :to="{ name: 'PurchaseOrderView', params: { purchaseOrderId: purchaseOrderLine.purchaseOrder.id } }">{{
                purchaseOrderLine.purchaseOrder.code
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link
          v-if="purchaseOrderLine.id"
          :to="{ name: 'PurchaseOrderLineEdit', params: { purchaseOrderLineId: purchaseOrderLine.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./purchase-order-line-details.component.ts"></script>
