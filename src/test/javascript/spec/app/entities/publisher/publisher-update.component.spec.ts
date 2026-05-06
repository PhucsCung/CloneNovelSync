/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import PublisherUpdateComponent from '@/entities/publisher/publisher-update.vue';
import PublisherClass from '@/entities/publisher/publisher-update.component';
import PublisherService from '@/entities/publisher/publisher.service';

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
  describe('Publisher Management Update Component', () => {
    let wrapper: Wrapper<PublisherClass>;
    let comp: PublisherClass;
    let publisherServiceStub: SinonStubbedInstance<PublisherService>;

    beforeEach(() => {
      publisherServiceStub = sinon.createStubInstance<PublisherService>(PublisherService);

      wrapper = shallowMount<PublisherClass>(PublisherUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          publisherService: () => publisherServiceStub,
          alertService: () => new AlertService(),

          bookService: () =>
            sinon.createStubInstance<BookService>(BookService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.publisher = entity;
        publisherServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(publisherServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.publisher = entity;
        publisherServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(publisherServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundPublisher = { id: 123 };
        publisherServiceStub.find.resolves(foundPublisher);
        publisherServiceStub.retrieve.resolves([foundPublisher]);

        // WHEN
        comp.beforeRouteEnter({ params: { publisherId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.publisher).toBe(foundPublisher);
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
