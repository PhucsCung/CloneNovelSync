<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="cloneNovelSyncApp.inventoryTransaction.home.createOrEditLabel"
          data-cy="InventoryTransactionCreateUpdateHeading"
          v-text="$t('cloneNovelSyncApp.inventoryTransaction.home.createOrEditLabel')"
        >
          Create or edit a InventoryTransaction
        </h2>
        <div>
          <div class="form-group" v-if="inventoryTransaction.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="inventoryTransaction.id" readonly />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="$t('cloneNovelSyncApp.inventoryTransaction.transactionType')"
              for="inventory-transaction-transactionType"
              >Transaction Type</label
            >
            <select
              class="form-control"
              name="transactionType"
              :class="{
                valid: !$v.inventoryTransaction.transactionType.$invalid,
                invalid: $v.inventoryTransaction.transactionType.$invalid,
              }"
              v-model="$v.inventoryTransaction.transactionType.$model"
              id="inventory-transaction-transactionType"
              data-cy="transactionType"
              required
            >
              <option
                v-for="transactionType in transactionTypeValues"
                :key="transactionType"
                v-bind:value="transactionType"
                v-bind:label="$t('cloneNovelSyncApp.TransactionType.' + transactionType)"
              >
                {{ transactionType }}
              </option>
            </select>
            <div v-if="$v.inventoryTransaction.transactionType.$anyDirty && $v.inventoryTransaction.transactionType.$invalid">
              <small
                class="form-text text-danger"
                v-if="!$v.inventoryTransaction.transactionType.required"
                v-text="$t('entity.validation.required')"
              >
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="$t('cloneNovelSyncApp.inventoryTransaction.quantity')"
              for="inventory-transaction-quantity"
              >Quantity</label
            >
            <input
              type="number"
              class="form-control"
              name="quantity"
              id="inventory-transaction-quantity"
              data-cy="quantity"
              :class="{ valid: !$v.inventoryTransaction.quantity.$invalid, invalid: $v.inventoryTransaction.quantity.$invalid }"
              v-model.number="$v.inventoryTransaction.quantity.$model"
              required
            />
            <div v-if="$v.inventoryTransaction.quantity.$anyDirty && $v.inventoryTransaction.quantity.$invalid">
              <small
                class="form-text text-danger"
                v-if="!$v.inventoryTransaction.quantity.required"
                v-text="$t('entity.validation.required')"
              >
                This field is required.
              </small>
              <small class="form-text text-danger" v-if="!$v.inventoryTransaction.quantity.numeric" v-text="$t('entity.validation.number')">
                This field should be a number.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="$t('cloneNovelSyncApp.inventoryTransaction.referenceType')"
              for="inventory-transaction-referenceType"
              >Reference Type</label
            >
            <select
              class="form-control"
              name="referenceType"
              :class="{ valid: !$v.inventoryTransaction.referenceType.$invalid, invalid: $v.inventoryTransaction.referenceType.$invalid }"
              v-model="$v.inventoryTransaction.referenceType.$model"
              id="inventory-transaction-referenceType"
              data-cy="referenceType"
            >
              <option
                v-for="referenceType in referenceTypeValues"
                :key="referenceType"
                v-bind:value="referenceType"
                v-bind:label="$t('cloneNovelSyncApp.ReferenceType.' + referenceType)"
              >
                {{ referenceType }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="$t('cloneNovelSyncApp.inventoryTransaction.referenceId')"
              for="inventory-transaction-referenceId"
              >Reference Id</label
            >
            <input
              type="number"
              class="form-control"
              name="referenceId"
              id="inventory-transaction-referenceId"
              data-cy="referenceId"
              :class="{ valid: !$v.inventoryTransaction.referenceId.$invalid, invalid: $v.inventoryTransaction.referenceId.$invalid }"
              v-model.number="$v.inventoryTransaction.referenceId.$model"
            />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="$t('cloneNovelSyncApp.inventoryTransaction.createdAt')"
              for="inventory-transaction-createdAt"
              >Created At</label
            >
            <div class="d-flex">
              <input
                id="inventory-transaction-createdAt"
                data-cy="createdAt"
                type="datetime-local"
                class="form-control"
                name="createdAt"
                :class="{ valid: !$v.inventoryTransaction.createdAt.$invalid, invalid: $v.inventoryTransaction.createdAt.$invalid }"
                :value="convertDateTimeFromServer($v.inventoryTransaction.createdAt.$model)"
                @change="updateInstantField('createdAt', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.inventoryTransaction.book')" for="inventory-transaction-book"
              >Book</label
            >
            <select class="form-control" id="inventory-transaction-book" data-cy="book" name="book" v-model="inventoryTransaction.book">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  inventoryTransaction.book && bookOption.id === inventoryTransaction.book.id ? inventoryTransaction.book : bookOption
                "
                v-for="bookOption in books"
                :key="bookOption.id"
              >
                {{ bookOption.title }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.cancel')">Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="$v.inventoryTransaction.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./inventory-transaction-update.component.ts"></script>
