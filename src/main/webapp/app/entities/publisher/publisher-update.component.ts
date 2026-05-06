import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, maxLength } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import BookService from '@/entities/book/book.service';
import { IBook } from '@/shared/model/book.model';

import { IPublisher, Publisher } from '@/shared/model/publisher.model';
import PublisherService from './publisher.service';

const validations: any = {
  publisher: {
    code: {
      required,
      maxLength: maxLength(20),
    },
    name: {
      required,
      maxLength: maxLength(255),
    },
    address: {
      maxLength: maxLength(500),
    },
    phone: {
      maxLength: maxLength(20),
    },
  },
};

@Component({
  validations,
})
export default class PublisherUpdate extends Vue {
  @Inject('publisherService') private publisherService: () => PublisherService;
  @Inject('alertService') private alertService: () => AlertService;

  public publisher: IPublisher = new Publisher();

  @Inject('bookService') private bookService: () => BookService;

  public books: IBook[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.publisherId) {
        vm.retrievePublisher(to.params.publisherId);
      }
      vm.initRelationships();
    });
  }

  created(): void {
    this.currentLanguage = this.$store.getters.currentLanguage;
    this.$store.watch(
      () => this.$store.getters.currentLanguage,
      () => {
        this.currentLanguage = this.$store.getters.currentLanguage;
      }
    );
  }

  public save(): void {
    this.isSaving = true;
    if (this.publisher.id) {
      this.publisherService()
        .update(this.publisher)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('cloneNovelSyncApp.publisher.updated', { param: param.id });
          return (this.$root as any).$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    } else {
      this.publisherService()
        .create(this.publisher)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('cloneNovelSyncApp.publisher.created', { param: param.id });
          (this.$root as any).$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Success',
            variant: 'success',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    }
  }

  public retrievePublisher(publisherId): void {
    this.publisherService()
      .find(publisherId)
      .then(res => {
        this.publisher = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.bookService()
      .retrieve()
      .then(res => {
        this.books = res.data;
      });
  }
}
