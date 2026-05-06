<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="cloneNovelSyncApp.inventoryBalance.home.createOrEditLabel"
          data-cy="InventoryBalanceCreateUpdateHeading"
          v-text="$t('cloneNovelSyncApp.inventoryBalance.home.createOrEditLabel')"
        >
          Create or edit a InventoryBalance
        </h2>
        <div>
          <div class="form-group" v-if="inventoryBalance.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="inventoryBalance.id" readonly />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="$t('cloneNovelSyncApp.inventoryBalance.quantityOnHand')"
              for="inventory-balance-quantityOnHand"
              >Quantity On Hand</label
            >
            <input
              type="number"
              class="form-control"
              name="quantityOnHand"
              id="inventory-balance-quantityOnHand"
              data-cy="quantityOnHand"
              :class="{ valid: !$v.inventoryBalance.quantityOnHand.$invalid, invalid: $v.inventoryBalance.quantityOnHand.$invalid }"
              v-model.number="$v.inventoryBalance.quantityOnHand.$model"
              required
            />
            <div v-if="$v.inventoryBalance.quantityOnHand.$anyDirty && $v.inventoryBalance.quantityOnHand.$invalid">
              <small
                class="form-text text-danger"
                v-if="!$v.inventoryBalance.quantityOnHand.required"
                v-text="$t('entity.validation.required')"
              >
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.inventoryBalance.quantityOnHand.min"
                v-text="$t('entity.validation.min', { min: 0 })"
              >
                This field should be at least 0.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.inventoryBalance.quantityOnHand.numeric"
                v-text="$t('entity.validation.number')"
              >
                This field should be a number.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.inventoryBalance.updatedAt')" for="inventory-balance-updatedAt"
              >Updated At</label
            >
            <div class="d-flex">
              <input
                id="inventory-balance-updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                class="form-control"
                name="updatedAt"
                :class="{ valid: !$v.inventoryBalance.updatedAt.$invalid, invalid: $v.inventoryBalance.updatedAt.$invalid }"
                :value="convertDateTimeFromServer($v.inventoryBalance.updatedAt.$model)"
                @change="updateInstantField('updatedAt', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.inventoryBalance.book')" for="inventory-balance-book"
              >Book</label
            >
            <select class="form-control" id="inventory-balance-book" data-cy="book" name="book" v-model="inventoryBalance.book">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="inventoryBalance.book && bookOption.id === inventoryBalance.book.id ? inventoryBalance.book : bookOption"
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
            :disabled="$v.inventoryBalance.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./inventory-balance-update.component.ts"></script>
