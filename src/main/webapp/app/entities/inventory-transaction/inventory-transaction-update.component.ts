import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, numeric } from 'vuelidate/lib/validators';
import dayjs from 'dayjs';
import { DATE_TIME_LONG_FORMAT } from '@/shared/date/filters';

import AlertService from '@/shared/alert/alert.service';

import BookService from '@/entities/book/book.service';
import { IBook } from '@/shared/model/book.model';

import { IInventoryTransaction, InventoryTransaction } from '@/shared/model/inventory-transaction.model';
import InventoryTransactionService from './inventory-transaction.service';
import { TransactionType } from '@/shared/model/enumerations/transaction-type.model';
import { ReferenceType } from '@/shared/model/enumerations/reference-type.model';

const validations: any = {
  inventoryTransaction: {
    transactionType: {
      required,
    },
    quantity: {
      required,
      numeric,
    },
    referenceType: {},
    referenceId: {},
    createdAt: {},
  },
};

@Component({
  validations,
})
export default class InventoryTransactionUpdate extends Vue {
  @Inject('inventoryTransactionService') private inventoryTransactionService: () => InventoryTransactionService;
  @Inject('alertService') private alertService: () => AlertService;

  public inventoryTransaction: IInventoryTransaction = new InventoryTransaction();

  @Inject('bookService') private bookService: () => BookService;

  public books: IBook[] = [];
  public transactionTypeValues: string[] = Object.keys(TransactionType);
  public referenceTypeValues: string[] = Object.keys(ReferenceType);
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.inventoryTransactionId) {
        vm.retrieveInventoryTransaction(to.params.inventoryTransactionId);
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
    if (this.inventoryTransaction.id) {
      this.inventoryTransactionService()
        .update(this.inventoryTransaction)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('cloneNovelSyncApp.inventoryTransaction.updated', { param: param.id });
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
      this.inventoryTransactionService()
        .create(this.inventoryTransaction)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('cloneNovelSyncApp.inventoryTransaction.created', { param: param.id });
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

  public convertDateTimeFromServer(date: Date): string {
    if (date && dayjs(date).isValid()) {
      return dayjs(date).format(DATE_TIME_LONG_FORMAT);
    }
    return null;
  }

  public updateInstantField(field, event) {
    if (event.target.value) {
      this.inventoryTransaction[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.inventoryTransaction[field] = null;
    }
  }

  public updateZonedDateTimeField(field, event) {
    if (event.target.value) {
      this.inventoryTransaction[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.inventoryTransaction[field] = null;
    }
  }

  public retrieveInventoryTransaction(inventoryTransactionId): void {
    this.inventoryTransactionService()
      .find(inventoryTransactionId)
      .then(res => {
        res.createdAt = new Date(res.createdAt);
        this.inventoryTransaction = res;
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
