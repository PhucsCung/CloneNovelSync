import { Component, Vue, Inject } from 'vue-property-decorator';

import { numeric, required, minValue } from 'vuelidate/lib/validators';
import dayjs from 'dayjs';
import { DATE_TIME_LONG_FORMAT } from '@/shared/date/filters';

import AlertService from '@/shared/alert/alert.service';

import BookService from '@/entities/book/book.service';
import { IBook } from '@/shared/model/book.model';

import { IInventoryBalance, InventoryBalance } from '@/shared/model/inventory-balance.model';
import InventoryBalanceService from './inventory-balance.service';

const validations: any = {
  inventoryBalance: {
    quantityOnHand: {
      required,
      numeric,
      min: minValue(0),
    },
    updatedAt: {},
  },
};

@Component({
  validations,
})
export default class InventoryBalanceUpdate extends Vue {
  @Inject('inventoryBalanceService') private inventoryBalanceService: () => InventoryBalanceService;
  @Inject('alertService') private alertService: () => AlertService;

  public inventoryBalance: IInventoryBalance = new InventoryBalance();

  @Inject('bookService') private bookService: () => BookService;

  public books: IBook[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.inventoryBalanceId) {
        vm.retrieveInventoryBalance(to.params.inventoryBalanceId);
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
    if (this.inventoryBalance.id) {
      this.inventoryBalanceService()
        .update(this.inventoryBalance)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('cloneNovelSyncApp.inventoryBalance.updated', { param: param.id });
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
      this.inventoryBalanceService()
        .create(this.inventoryBalance)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('cloneNovelSyncApp.inventoryBalance.created', { param: param.id });
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
      this.inventoryBalance[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.inventoryBalance[field] = null;
    }
  }

  public updateZonedDateTimeField(field, event) {
    if (event.target.value) {
      this.inventoryBalance[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.inventoryBalance[field] = null;
    }
  }

  public retrieveInventoryBalance(inventoryBalanceId): void {
    this.inventoryBalanceService()
      .find(inventoryBalanceId)
      .then(res => {
        res.updatedAt = new Date(res.updatedAt);
        this.inventoryBalance = res;
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
