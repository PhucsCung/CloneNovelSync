<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="publisher">
        <h2 class="jh-entity-heading" data-cy="publisherDetailsHeading">
          <span v-text="$t('cloneNovelSyncApp.publisher.detail.title')">Publisher</span> {{ publisher.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('cloneNovelSyncApp.publisher.code')">Code</span>
          </dt>
          <dd>
            <span>{{ publisher.code }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.publisher.name')">Name</span>
          </dt>
          <dd>
            <span>{{ publisher.name }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.publisher.address')">Address</span>
          </dt>
          <dd>
            <span>{{ publisher.address }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.publisher.phone')">Phone</span>
          </dt>
          <dd>
            <span>{{ publisher.phone }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link
          v-if="publisher.id"
          :to="{ name: 'PublisherEdit', params: { publisherId: publisher.id } }"
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

<script lang="ts" src="./publisher-details.component.ts"></script>
