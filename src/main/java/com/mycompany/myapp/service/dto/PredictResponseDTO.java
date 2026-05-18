package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Objects;

public class PredictResponseDTO implements Serializable {

    @JsonProperty("book_id")
    private Long bookId;

    @JsonProperty("predicted_sales")
    private Integer predictedSales;

    @JsonProperty("recommend_restock")
    private Integer recommendRestock;

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Integer getPredictedSales() {
        return predictedSales;
    }

    public void setPredictedSales(Integer predictedSales) {
        this.predictedSales = predictedSales;
    }

    public Integer getRecommendRestock() {
        return recommendRestock;
    }

    public void setRecommendRestock(Integer recommendRestock) {
        this.recommendRestock = recommendRestock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PredictResponseDTO)) {
            return false;
        }

        PredictResponseDTO predictResponseDTO = (PredictResponseDTO) o;
        if (this.bookId == null) {
            return false;
        }
        return Objects.equals(this.bookId, predictResponseDTO.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.bookId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PredictResponseDTO{" +
            "bookId=" + getBookId() +
            ", predictedSales=" + getPredictedSales() +
            ", recommendRestock=" + getRecommendRestock() +
            "}";
    }
}
