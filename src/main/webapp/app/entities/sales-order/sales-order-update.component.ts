import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, maxLength } from 'vuelidate/lib/validators';
import dayjs from 'dayjs';
import { DATE_TIME_LONG_FORMAT } from '@/shared/date/filters';

import AlertService from '@/shared/alert/alert.service';

import SalesOrderLineService from '@/entities/sales-order-line/sales-order-line.service';
import { ISalesOrderLine } from '@/shared/model/sales-order-line.model';

import UserService from '@/entities/user/user.service';

import { ISalesOrder, SalesOrder } from '@/shared/model/sales-order.model';
import SalesOrderService from './sales-order.service';
import { SalesStatus } from '@/shared/model/enumerations/sales-status.model';

const validations: any = {
  salesOrder: {
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
export default class SalesOrderUpdate extends Vue {
  @Inject('salesOrderService') private salesOrderService: () => SalesOrderService;
  @Inject('alertService') private alertService: () => AlertService;

  public salesOrder: ISalesOrder = new SalesOrder();

  @Inject('salesOrderLineService') private salesOrderLineService: () => SalesOrderLineService;

  public salesOrderLines: ISalesOrderLine[] = [];

  @Inject('userService') private userService: () => UserService;

  public users: Array<any> = [];
  public salesStatusValues: string[] = Object.keys(SalesStatus);
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.salesOrderId) {
        vm.retrieveSalesOrder(to.params.salesOrderId);
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
    if (this.salesOrder.id) {
      this.salesOrderService()
        .update(this.salesOrder)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('cloneNovelSyncApp.salesOrder.updated', { param: param.id });
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
      this.salesOrderService()
        .create(this.salesOrder)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('cloneNovelSyncApp.salesOrder.created', { param: param.id });
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
      this.salesOrder[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.salesOrder[field] = null;
    }
  }

  public updateZonedDateTimeField(field, event) {
    if (event.target.value) {
      this.salesOrder[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.salesOrder[field] = null;
    }
  }

  public retrieveSalesOrder(salesOrderId): void {
    this.salesOrderService()
      .find(salesOrderId)
      .then(res => {
        res.createdAt = new Date(res.createdAt);
        this.salesOrder = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.salesOrderLineService()
      .retrieve()
      .then(res => {
        this.salesOrderLines = res.data;
      });

    this.userService()
      .retrieve()
      .then(res => {
        this.users = res.data;
      });
  }
}
