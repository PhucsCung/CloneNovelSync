import { Component, Vue, Inject } from 'vue-property-decorator';

import { numeric, required, minValue } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import BookService from '@/entities/book/book.service';
import { IBook } from '@/shared/model/book.model';

import PurchaseOrderService from '@/entities/purchase-order/purchase-order.service';
import { IPurchaseOrder } from '@/shared/model/purchase-order.model';

import { IPurchaseOrderLine, PurchaseOrderLine } from '@/shared/model/purchase-order-line.model';
import PurchaseOrderLineService from './purchase-order-line.service';

const validations: any = {
  purchaseOrderLine: {
    quantity: {
      required,
      numeric,
      min: minValue(1),
    },
    unitCost: {},
  },
};

@Component({
  validations,
})
export default class PurchaseOrderLineUpdate extends Vue {
  @Inject('purchaseOrderLineService') private purchaseOrderLineService: () => PurchaseOrderLineService;
  @Inject('alertService') private alertService: () => AlertService;

  public purchaseOrderLine: IPurchaseOrderLine = new PurchaseOrderLine();

  @Inject('bookService') private bookService: () => BookService;

  public books: IBook[] = [];

  @Inject('purchaseOrderService') private purchaseOrderService: () => PurchaseOrderService;

  public purchaseOrders: IPurchaseOrder[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.purchaseOrderLineId) {
        vm.retrievePurchaseOrderLine(to.params.purchaseOrderLineId);
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
    if (this.purchaseOrderLine.id) {
      this.purchaseOrderLineService()
        .update(this.purchaseOrderLine)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('cloneNovelSyncApp.purchaseOrderLine.updated', { param: param.id });
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
      this.purchaseOrderLineService()
        .create(this.purchaseOrderLine)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('cloneNovelSyncApp.purchaseOrderLine.created', { param: param.id });
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

  public retrievePurchaseOrderLine(purchaseOrderLineId): void {
    this.purchaseOrderLineService()
      .find(purchaseOrderLineId)
      .then(res => {
        this.purchaseOrderLine = res;
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
    this.purchaseOrderService()
      .retrieve()
      .then(res => {
        this.purchaseOrders = res.data;
      });
  }
}
