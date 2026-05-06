<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="inventoryBalance">
        <h2 class="jh-entity-heading" data-cy="inventoryBalanceDetailsHeading">
          <span v-text="$t('cloneNovelSyncApp.inventoryBalance.detail.title')">InventoryBalance</span> {{ inventoryBalance.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('cloneNovelSyncApp.inventoryBalance.quantityOnHand')">Quantity On Hand</span>
          </dt>
          <dd>
            <span>{{ inventoryBalance.quantityOnHand }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.inventoryBalance.updatedAt')">Updated At</span>
          </dt>
          <dd>
            <span v-if="inventoryBalance.updatedAt">{{ $d(Date.parse(inventoryBalance.updatedAt), 'long') }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.inventoryBalance.book')">Book</span>
          </dt>
          <dd>
            <div v-if="inventoryBalance.book">
              <router-link :to="{ name: 'BookView', params: { bookId: inventoryBalance.book.id } }">{{
                inventoryBalance.book.title
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link
          v-if="inventoryBalance.id"
          :to="{ name: 'InventoryBalanceEdit', params: { inventoryBalanceId: inventoryBalance.id } }"
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

<script lang="ts" src="./inventory-balance-details.component.ts"></script>
