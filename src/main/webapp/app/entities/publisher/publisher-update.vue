<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="cloneNovelSyncApp.publisher.home.createOrEditLabel"
          data-cy="PublisherCreateUpdateHeading"
          v-text="$t('cloneNovelSyncApp.publisher.home.createOrEditLabel')"
        >
          Create or edit a Publisher
        </h2>
        <div>
          <div class="form-group" v-if="publisher.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="publisher.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.publisher.code')" for="publisher-code">Code</label>
            <input
              type="text"
              class="form-control"
              name="code"
              id="publisher-code"
              data-cy="code"
              :class="{ valid: !$v.publisher.code.$invalid, invalid: $v.publisher.code.$invalid }"
              v-model="$v.publisher.code.$model"
              required
            />
            <div v-if="$v.publisher.code.$anyDirty && $v.publisher.code.$invalid">
              <small class="form-text text-danger" v-if="!$v.publisher.code.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.publisher.code.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 20 })"
              >
                This field cannot be longer than 20 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.publisher.name')" for="publisher-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="publisher-name"
              data-cy="name"
              :class="{ valid: !$v.publisher.name.$invalid, invalid: $v.publisher.name.$invalid }"
              v-model="$v.publisher.name.$model"
              required
            />
            <div v-if="$v.publisher.name.$anyDirty && $v.publisher.name.$invalid">
              <small class="form-text text-danger" v-if="!$v.publisher.name.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.publisher.name.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 255 })"
              >
                This field cannot be longer than 255 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.publisher.address')" for="publisher-address">Address</label>
            <input
              type="text"
              class="form-control"
              name="address"
              id="publisher-address"
              data-cy="address"
              :class="{ valid: !$v.publisher.address.$invalid, invalid: $v.publisher.address.$invalid }"
              v-model="$v.publisher.address.$model"
            />
            <div v-if="$v.publisher.address.$anyDirty && $v.publisher.address.$invalid">
              <small
                class="form-text text-danger"
                v-if="!$v.publisher.address.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 500 })"
              >
                This field cannot be longer than 500 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.publisher.phone')" for="publisher-phone">Phone</label>
            <input
              type="text"
              class="form-control"
              name="phone"
              id="publisher-phone"
              data-cy="phone"
              :class="{ valid: !$v.publisher.phone.$invalid, invalid: $v.publisher.phone.$invalid }"
              v-model="$v.publisher.phone.$model"
            />
            <div v-if="$v.publisher.phone.$anyDirty && $v.publisher.phone.$invalid">
              <small
                class="form-text text-danger"
                v-if="!$v.publisher.phone.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 20 })"
              >
                This field cannot be longer than 20 characters.
              </small>
            </div>
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
            :disabled="$v.publisher.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./publisher-update.component.ts"></script>
