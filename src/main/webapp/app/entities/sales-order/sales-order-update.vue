<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="cloneNovelSyncApp.salesOrder.home.createOrEditLabel"
          data-cy="SalesOrderCreateUpdateHeading"
          v-text="$t('cloneNovelSyncApp.salesOrder.home.createOrEditLabel')"
        >
          Create or edit a SalesOrder
        </h2>
        <div>
          <div class="form-group" v-if="salesOrder.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="salesOrder.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.salesOrder.code')" for="sales-order-code">Code</label>
            <input
              type="text"
              class="form-control"
              name="code"
              id="sales-order-code"
              data-cy="code"
              :class="{ valid: !$v.salesOrder.code.$invalid, invalid: $v.salesOrder.code.$invalid }"
              v-model="$v.salesOrder.code.$model"
              required
            />
            <div v-if="$v.salesOrder.code.$anyDirty && $v.salesOrder.code.$invalid">
              <small class="form-text text-danger" v-if="!$v.salesOrder.code.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.salesOrder.code.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 50 })"
              >
                This field cannot be longer than 50 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.salesOrder.status')" for="sales-order-status">Status</label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !$v.salesOrder.status.$invalid, invalid: $v.salesOrder.status.$invalid }"
              v-model="$v.salesOrder.status.$model"
              id="sales-order-status"
              data-cy="status"
            >
              <option
                v-for="salesStatus in salesStatusValues"
                :key="salesStatus"
                v-bind:value="salesStatus"
                v-bind:label="$t('cloneNovelSyncApp.SalesStatus.' + salesStatus)"
              >
                {{ salesStatus }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.salesOrder.totalAmount')" for="sales-order-totalAmount"
              >Total Amount</label
            >
            <input
              type="number"
              class="form-control"
              name="totalAmount"
              id="sales-order-totalAmount"
              data-cy="totalAmount"
              :class="{ valid: !$v.salesOrder.totalAmount.$invalid, invalid: $v.salesOrder.totalAmount.$invalid }"
              v-model.number="$v.salesOrder.totalAmount.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.salesOrder.createdAt')" for="sales-order-createdAt"
              >Created At</label
            >
            <div class="d-flex">
              <input
                id="sales-order-createdAt"
                data-cy="createdAt"
                type="datetime-local"
                class="form-control"
                name="createdAt"
                :class="{ valid: !$v.salesOrder.createdAt.$invalid, invalid: $v.salesOrder.createdAt.$invalid }"
                :value="convertDateTimeFromServer($v.salesOrder.createdAt.$model)"
                @change="updateInstantField('createdAt', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.salesOrder.user')" for="sales-order-user">User</label>
            <select class="form-control" id="sales-order-user" data-cy="user" name="user" v-model="salesOrder.user">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="salesOrder.user && userOption.id === salesOrder.user.id ? salesOrder.user : userOption"
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
            :disabled="$v.salesOrder.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./sales-order-update.component.ts"></script>
