<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="inventoryTransaction">
        <h2 class="jh-entity-heading" data-cy="inventoryTransactionDetailsHeading">
          <span v-text="$t('cloneNovelSyncApp.inventoryTransaction.detail.title')">InventoryTransaction</span> {{ inventoryTransaction.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('cloneNovelSyncApp.inventoryTransaction.transactionType')">Transaction Type</span>
          </dt>
          <dd>
            <span v-text="$t('cloneNovelSyncApp.TransactionType.' + inventoryTransaction.transactionType)">{{
              inventoryTransaction.transactionType
            }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.inventoryTransaction.quantity')">Quantity</span>
          </dt>
          <dd>
            <span>{{ inventoryTransaction.quantity }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.inventoryTransaction.referenceType')">Reference Type</span>
          </dt>
          <dd>
            <span v-text="$t('cloneNovelSyncApp.ReferenceType.' + inventoryTransaction.referenceType)">{{
              inventoryTransaction.referenceType
            }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.inventoryTransaction.referenceId')">Reference Id</span>
          </dt>
          <dd>
            <span>{{ inventoryTransaction.referenceId }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.inventoryTransaction.createdAt')">Created At</span>
          </dt>
          <dd>
            <span v-if="inventoryTransaction.createdAt">{{ $d(Date.parse(inventoryTransaction.createdAt), 'long') }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.inventoryTransaction.book')">Book</span>
          </dt>
          <dd>
            <div v-if="inventoryTransaction.book">
              <router-link :to="{ name: 'BookView', params: { bookId: inventoryTransaction.book.id } }">{{
                inventoryTransaction.book.title
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link
          v-if="inventoryTransaction.id"
          :to="{ name: 'InventoryTransactionEdit', params: { inventoryTransactionId: inventoryTransaction.id } }"
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

<script lang="ts" src="./inventory-transaction-details.component.ts"></script>
