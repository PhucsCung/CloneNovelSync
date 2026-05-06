import { Component, Vue, Inject } from 'vue-property-decorator';

import { IBook } from '@/shared/model/book.model';
import BookService from './book.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class BookDetails extends Vue {
  @Inject('bookService') private bookService: () => BookService;
  @Inject('alertService') private alertService: () => AlertService;

  public book: IBook = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.bookId) {
        vm.retrieveBook(to.params.bookId);
      }
    });
  }

  public retrieveBook(bookId) {
    this.bookService()
      .find(bookId)
      .then(res => {
        this.book = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
