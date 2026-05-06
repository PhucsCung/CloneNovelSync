import { Component, Vue, Inject } from 'vue-property-decorator';

import { IPublisher } from '@/shared/model/publisher.model';
import PublisherService from './publisher.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class PublisherDetails extends Vue {
  @Inject('publisherService') private publisherService: () => PublisherService;
  @Inject('alertService') private alertService: () => AlertService;

  public publisher: IPublisher = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.publisherId) {
        vm.retrievePublisher(to.params.publisherId);
      }
    });
  }

  public retrievePublisher(publisherId) {
    this.publisherService()
      .find(publisherId)
      .then(res => {
        this.publisher = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
