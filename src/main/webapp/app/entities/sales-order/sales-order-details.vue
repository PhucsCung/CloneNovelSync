<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="salesOrder">
        <h2 class="jh-entity-heading" data-cy="salesOrderDetailsHeading">
          <span v-text="$t('cloneNovelSyncApp.salesOrder.detail.title')">SalesOrder</span> {{ salesOrder.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('cloneNovelSyncApp.salesOrder.code')">Code</span>
          </dt>
          <dd>
            <span>{{ salesOrder.code }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.salesOrder.status')">Status</span>
          </dt>
          <dd>
            <span v-text="$t('cloneNovelSyncApp.SalesStatus.' + salesOrder.status)">{{ salesOrder.status }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.salesOrder.totalAmount')">Total Amount</span>
          </dt>
          <dd>
            <span>{{ salesOrder.totalAmount }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.salesOrder.createdAt')">Created At</span>
          </dt>
          <dd>
            <span v-if="salesOrder.createdAt">{{ $d(Date.parse(salesOrder.createdAt), 'long') }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.salesOrder.user')">User</span>
          </dt>
          <dd>
            {{ salesOrder.user ? salesOrder.user.login : '' }}
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link
          v-if="salesOrder.id"
          :to="{ name: 'SalesOrderEdit', params: { salesOrderId: salesOrder.id } }"
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

<script lang="ts" src="./sales-order-details.component.ts"></script>
