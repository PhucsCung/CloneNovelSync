package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class PredictRequestDTO implements Serializable {

    @JsonProperty("book_id")
    private Long bookId;

    @JsonProperty("history_sales")
    private List<Integer> historySales;

    @JsonProperty("current_stock")
    private Integer currentStock;

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public List<Integer> getHistorySales() {
        return historySales;
    }

    public void setHistorySales(List<Integer> historySales) {
        this.historySales = historySales;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PredictRequestDTO)) {
            return false;
        }

        PredictRequestDTO predictRequestDTO = (PredictRequestDTO) o;
        if (this.bookId == null) {
            return false;
        }
        return Objects.equals(this.bookId, predictRequestDTO.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.bookId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PredictRequestDTO{" +
            "bookId=" + getBookId() +
            ", historySales=" + getHistorySales() +
            ", currentStock=" + getCurrentStock() +
            "}";
    }
}
