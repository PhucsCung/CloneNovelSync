<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="cloneNovelSyncApp.purchaseOrder.home.createOrEditLabel"
          data-cy="PurchaseOrderCreateUpdateHeading"
          v-text="$t('cloneNovelSyncApp.purchaseOrder.home.createOrEditLabel')"
        >
          Create or edit a PurchaseOrder
        </h2>
        <div>
          <div class="form-group" v-if="purchaseOrder.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="purchaseOrder.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.purchaseOrder.code')" for="purchase-order-code">Code</label>
            <input
              type="text"
              class="form-control"
              name="code"
              id="purchase-order-code"
              data-cy="code"
              :class="{ valid: !$v.purchaseOrder.code.$invalid, invalid: $v.purchaseOrder.code.$invalid }"
              v-model="$v.purchaseOrder.code.$model"
              required
            />
            <div v-if="$v.purchaseOrder.code.$anyDirty && $v.purchaseOrder.code.$invalid">
              <small class="form-text text-danger" v-if="!$v.purchaseOrder.code.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.purchaseOrder.code.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 50 })"
              >
                This field cannot be longer than 50 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.purchaseOrder.status')" for="purchase-order-status"
              >Status</label
            >
            <select
              class="form-control"
              name="status"
              :class="{ valid: !$v.purchaseOrder.status.$invalid, invalid: $v.purchaseOrder.status.$invalid }"
              v-model="$v.purchaseOrder.status.$model"
              id="purchase-order-status"
              data-cy="status"
            >
              <option
                v-for="purchaseStatus in purchaseStatusValues"
                :key="purchaseStatus"
                v-bind:value="purchaseStatus"
                v-bind:label="$t('cloneNovelSyncApp.PurchaseStatus.' + purchaseStatus)"
              >
                {{ purchaseStatus }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.purchaseOrder.totalAmount')" for="purchase-order-totalAmount"
              >Total Amount</label
            >
            <input
              type="number"
              class="form-control"
              name="totalAmount"
              id="purchase-order-totalAmount"
              data-cy="totalAmount"
              :class="{ valid: !$v.purchaseOrder.totalAmount.$invalid, invalid: $v.purchaseOrder.totalAmount.$invalid }"
              v-model.number="$v.purchaseOrder.totalAmount.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.purchaseOrder.createdAt')" for="purchase-order-createdAt"
              >Created At</label
            >
            <div class="d-flex">
              <input
                id="purchase-order-createdAt"
                data-cy="createdAt"
                type="datetime-local"
                class="form-control"
                name="createdAt"
                :class="{ valid: !$v.purchaseOrder.createdAt.$invalid, invalid: $v.purchaseOrder.createdAt.$invalid }"
                :value="convertDateTimeFromServer($v.purchaseOrder.createdAt.$model)"
                @change="updateInstantField('createdAt', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.purchaseOrder.user')" for="purchase-order-user">User</label>
            <select class="form-control" id="purchase-order-user" data-cy="user" name="user" v-model="purchaseOrder.user">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="purchaseOrder.user && userOption.id === purchaseOrder.user.id ? purchaseOrder.user : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.login }}
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
            :disabled="$v.purchaseOrder.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./purchase-order-update.component.ts"></script>
