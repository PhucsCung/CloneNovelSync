/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';
import { ToastPlugin } from 'bootstrap-vue';

import dayjs from 'dayjs';
import { DATE_TIME_LONG_FORMAT } from '@/shared/date/filters';

import * as config from '@/shared/config/config';
import InventoryBalanceUpdateComponent from '@/entities/inventory-balance/inventory-balance-update.vue';
import InventoryBalanceClass from '@/entities/inventory-balance/inventory-balance-update.component';
import InventoryBalanceService from '@/entities/inventory-balance/inventory-balance.service';

import BookService from '@/entities/book/book.service';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
const router = new Router();
localVue.use(Router);
localVue.use(ToastPlugin);
localVue.component('font-awesome-icon', {});
localVue.component('b-input-group', {});
localVue.component('b-input-group-prepend', {});
localVue.component('b-form-datepicker', {});
localVue.component('b-form-input', {});

describe('Component Tests', () => {
  describe('InventoryBalance Management Update Component', () => {
    let wrapper: Wrapper<InventoryBalanceClass>;
    let comp: InventoryBalanceClass;
    let inventoryBalanceServiceStub: SinonStubbedInstance<InventoryBalanceService>;

    beforeEach(() => {
      inventoryBalanceServiceStub = sinon.createStubInstance<InventoryBalanceService>(InventoryBalanceService);

      wrapper = shallowMount<InventoryBalanceClass>(InventoryBalanceUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          inventoryBalanceService: () => inventoryBalanceServiceStub,
          alertService: () => new AlertService(),

          bookService: () =>
            sinon.createStubInstance<BookService>(BookService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      });
      comp = wrapper.vm;
    });

    describe('load', () => {
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.inventoryBalance = entity;
        inventoryBalanceServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(inventoryBalanceServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.inventoryBalance = entity;
        inventoryBalanceServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(inventoryBalanceServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundInventoryBalance = { id: 123 };
        inventoryBalanceServiceStub.find.resolves(foundInventoryBalance);
        inventoryBalanceServiceStub.retrieve.resolves([foundInventoryBalance]);

        // WHEN
        comp.beforeRouteEnter({ params: { inventoryBalanceId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.inventoryBalance).toBe(foundInventoryBalance);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        comp.previousState();
        await comp.$nextTick();

        expect(comp.$router.currentRoute.fullPath).toContain('/');
      });
    });
  });
});
