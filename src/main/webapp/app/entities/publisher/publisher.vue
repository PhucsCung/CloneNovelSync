<template>
  <div>
    <h2 id="page-heading" data-cy="PublisherHeading">
      <span v-text="$t('cloneNovelSyncApp.publisher.home.title')" id="publisher-heading">Publishers</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="$t('cloneNovelSyncApp.publisher.home.refreshListLabel')">Refresh List</span>
        </button>
        <router-link :to="{ name: 'PublisherCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-publisher"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="$t('cloneNovelSyncApp.publisher.home.createLabel')"> Create a new Publisher </span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && publishers && publishers.length === 0">
      <span v-text="$t('cloneNovelSyncApp.publisher.home.notFound')">No publishers found</span>
    </div>
    <div class="table-responsive" v-if="publishers && publishers.length > 0">
      <table class="table table-striped" aria-describedby="publishers">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="$t('global.field.id')">ID</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('code')">
              <span v-text="$t('cloneNovelSyncApp.publisher.code')">Code</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'code'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('name')">
              <span v-text="$t('cloneNovelSyncApp.publisher.name')">Name</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'name'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('address')">
              <span v-text="$t('cloneNovelSyncApp.publisher.address')">Address</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'address'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('phone')">
              <span v-text="$t('cloneNovelSyncApp.publisher.phone')">Phone</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'phone'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="publisher in publishers" :key="publisher.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'PublisherView', params: { publisherId: publisher.id } }">{{ publisher.id }}</router-link>
            </td>
            <td>{{ publisher.code }}</td>
            <td>{{ publisher.name }}</td>
            <td>{{ publisher.address }}</td>
            <td>{{ publisher.phone }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'PublisherView', params: { publisherId: publisher.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.view')">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'PublisherEdit', params: { publisherId: publisher.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.edit')">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(publisher)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="$t('entity.action.delete')">Delete</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <span slot="modal-title"
        ><span id="cloneNovelSyncApp.publisher.delete.question" data-cy="publisherDeleteDialogHeading" v-text="$t('entity.delete.title')"
          >Confirm delete operation</span
        ></span
      >
      <div class="modal-body">
        <p id="jhi-delete-publisher-heading" v-text="$t('cloneNovelSyncApp.publisher.delete.question', { id: removeId })">
          Are you sure you want to delete this Publisher?
        </p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-text="$t('entity.action.cancel')" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-publisher"
          data-cy="entityConfirmDeleteButton"
          v-text="$t('entity.action.delete')"
          v-on:click="removePublisher()"
        >
          Delete
        </button>
      </div>
    </b-modal>
    <div v-show="publishers && publishers.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage" :change="loadPage(page)"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./publisher.component.ts"></script>
