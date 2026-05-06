<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="book">
        <h2 class="jh-entity-heading" data-cy="bookDetailsHeading">
          <span v-text="$t('cloneNovelSyncApp.book.detail.title')">Book</span> {{ book.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('cloneNovelSyncApp.book.code')">Code</span>
          </dt>
          <dd>
            <span>{{ book.code }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.book.title')">Title</span>
          </dt>
          <dd>
            <span>{{ book.title }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.book.author')">Author</span>
          </dt>
          <dd>
            <span>{{ book.author }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.book.retailPrice')">Retail Price</span>
          </dt>
          <dd>
            <span>{{ book.retailPrice }}</span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.book.category')">Category</span>
          </dt>
          <dd>
            <span v-for="(category, i) in book.categories" :key="category.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'CategoryView', params: { categoryId: category.id } }">{{ category.title }}</router-link>
            </span>
          </dd>
          <dt>
            <span v-text="$t('cloneNovelSyncApp.book.publisher')">Publisher</span>
          </dt>
          <dd>
            <div v-if="book.publisher">
              <router-link :to="{ name: 'PublisherView', params: { publisherId: book.publisher.id } }">{{
                book.publisher.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link v-if="book.id" :to="{ name: 'BookEdit', params: { bookId: book.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./book-details.component.ts"></script>
