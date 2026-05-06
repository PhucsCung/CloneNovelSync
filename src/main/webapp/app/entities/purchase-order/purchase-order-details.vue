<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="purchaseOrder">
        <h2 class="jh-entity-heading" data-cy="purchaseOrderDetailsHeading">
          <span v-text="$t('cloneNovelSyncApp.purchaseOrder.detail.title')">PurchaseOrder</span> {{ purchaseOrder.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('cloneNovelSyncApp.purchaseOrder.code')">Code</span>
          </dt>
          <dd>
            <span>{{ purchaseOrder.code }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.purchaseOrder.status')">Status</span>
          </dt>
          <dd>
            <span v-text="$t('cloneNovelSyncApp.PurchaseStatus.' + purchaseOrder.status)">{{ purchaseOrder.status }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.purchaseOrder.totalAmount')">Total Amount</span>
          </dt>
          <dd>
            <span>{{ purchaseOrder.totalAmount }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.purchaseOrder.createdAt')">Created At</span>
          </dt>
          <dd>
            <span v-if="purchaseOrder.createdAt">{{ $d(Date.parse(purchaseOrder.createdAt), 'long') }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.purchaseOrder.user')">User</span>
          </dt>
          <dd>
            {{ purchaseOrder.user ? purchaseOrder.user.login : '' }}
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link
          v-if="purchaseOrder.id"
          :to="{ name: 'PurchaseOrderEdit', params: { purchaseOrderId: purchaseOrder.id } }"
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

<script lang="ts" src="./purchase-order-details.component.ts"></script>
