<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="cloneNovelSyncApp.book.home.createOrEditLabel"
          data-cy="BookCreateUpdateHeading"
          v-text="$t('cloneNovelSyncApp.book.home.createOrEditLabel')"
        >
          Create or edit a Book
        </h2>
        <div>
          <div class="form-group" v-if="book.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="book.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.book.code')" for="book-code">Code</label>
            <input
              type="text"
              class="form-control"
              name="code"
              id="book-code"
              data-cy="code"
              :class="{ valid: !$v.book.code.$invalid, invalid: $v.book.code.$invalid }"
              v-model="$v.book.code.$model"
              required
            />
            <div v-if="$v.book.code.$anyDirty && $v.book.code.$invalid">
              <small class="form-text text-danger" v-if="!$v.book.code.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small class="form-text text-danger" v-if="!$v.book.code.maxLength" v-text="$t('entity.validation.maxlength', { max: 20 })">
                This field cannot be longer than 20 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.book.title')" for="book-title">Title</label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="book-title"
              data-cy="title"
              :class="{ valid: !$v.book.title.$invalid, invalid: $v.book.title.$invalid }"
              v-model="$v.book.title.$model"
              required
            />
            <div v-if="$v.book.title.$anyDirty && $v.book.title.$invalid">
              <small class="form-text text-danger" v-if="!$v.book.title.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small class="form-text text-danger" v-if="!$v.book.title.maxLength" v-text="$t('entity.validation.maxlength', { max: 255 })">
                This field cannot be longer than 255 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.book.author')" for="book-author">Author</label>
            <input
              type="text"
              class="form-control"
              name="author"
              id="book-author"
              data-cy="author"
              :class="{ valid: !$v.book.author.$invalid, invalid: $v.book.author.$invalid }"
              v-model="$v.book.author.$model"
            />
            <div v-if="$v.book.author.$anyDirty && $v.book.author.$invalid">
              <small
                class="form-text text-danger"
                v-if="!$v.book.author.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 255 })"
              >
                This field cannot be longer than 255 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.book.retailPrice')" for="book-retailPrice">Retail Price</label>
            <input
              type="number"
              class="form-control"
              name="retailPrice"
              id="book-retailPrice"
              data-cy="retailPrice"
              :class="{ valid: !$v.book.retailPrice.$invalid, invalid: $v.book.retailPrice.$invalid }"
              v-model.number="$v.book.retailPrice.$model"
            />
          </div>
          <div class="form-group">
            <label v-text="$t('cloneNovelSyncApp.book.category')" for="book-category">Category</label>
            <select
              class="form-control"
              id="book-categories"
              data-cy="category"
              multiple
              name="category"
              v-if="book.categories !== undefined"
              v-model="book.categories"
            >
              <option
                v-bind:value="getSelected(book.categories, categoryOption)"
                v-for="categoryOption in categories"
                :key="categoryOption.id"
              >
                {{ categoryOption.title }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('cloneNovelSyncApp.book.publisher')" for="book-publisher">Publisher</label>
            <select class="form-control" id="book-publisher" data-cy="publisher" name="publisher" v-model="book.publisher">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="book.publisher && publisherOption.id === book.publisher.id ? book.publisher : publisherOption"
                v-for="publisherOption in publishers"
                :key="publisherOption.id"
              >
                {{ publisherOption.name }}
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
            :disabled="$v.book.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./book-update.component.ts"></script>
