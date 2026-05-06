import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, maxLength } from 'vuelidate/lib/validators';
import dayjs from 'dayjs';
import { DATE_TIME_LONG_FORMAT } from '@/shared/date/filters';

import AlertService from '@/shared/alert/alert.service';

import PurchaseOrderLineService from '@/entities/purchase-order-line/purchase-order-line.service';
import { IPurchaseOrderLine } from '@/shared/model/purchase-order-line.model';

import UserService from '@/entities/user/user.service';

import { IPurchaseOrder, PurchaseOrder } from '@/shared/model/purchase-order.model';
import PurchaseOrderService from './purchase-order.service';
import { PurchaseStatus } from '@/shared/model/enumerations/purchase-status.model';

const validations: any = {
  purchaseOrder: {
    code: {
      required,
      maxLength: maxLength(50),
    },
    status: {},
    totalAmount: {},
    createdAt: {},
  },
};

@Component({
  validations,
})
export default class PurchaseOrderUpdate extends Vue {
  @Inject('purchaseOrderService') private purchaseOrderService: () => PurchaseOrderService;
  @Inject('alertService') private alertService: () => AlertService;

  public purchaseOrder: IPurchaseOrder = new PurchaseOrder();

  @Inject('purchaseOrderLineService') private purchaseOrderLineService: () => PurchaseOrderLineService;

  public purchaseOrderLines: IPurchaseOrderLine[] = [];

  @Inject('userService') private userService: () => UserService;

  public users: Array<any> = [];
  public purchaseStatusValues: string[] = Object.keys(PurchaseStatus);
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.purchaseOrderId) {
        vm.retrievePurchaseOrder(to.params.purchaseOrderId);
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
    if (this.purchaseOrder.id) {
      this.purchaseOrderService()
        .update(this.purchaseOrder)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('cloneNovelSyncApp.purchaseOrder.updated', { param: param.id });
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
      this.purchaseOrderService()
        .create(this.purchaseOrder)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('cloneNovelSyncApp.purchaseOrder.created', { param: param.id });
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
      this.purchaseOrder[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.purchaseOrder[field] = null;
    }
  }

  public updateZonedDateTimeField(field, event) {
    if (event.target.value) {
      this.purchaseOrder[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.purchaseOrder[field] = null;
    }
  }

  public retrievePurchaseOrder(purchaseOrderId): void {
    this.purchaseOrderService()
      .find(purchaseOrderId)
      .then(res => {
        res.createdAt = new Date(res.createdAt);
        this.purchaseOrder = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.purchaseOrderLineService()
      .retrieve()
      .then(res => {
        this.purchaseOrderLines = res.data;
      });

    this.userService()
      .retrieve()
      .then(res => {
        this.users = res.data;
      });
  }
}
