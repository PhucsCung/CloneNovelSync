package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.PublisherStatus;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Publisher} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PublisherDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String code;

    @NotNull
    @Size(max = 255)
    private String name;

    @Size(max = 500)
    private String address;

    @Size(max = 20)
    private String phone;

    private PublisherStatus status = PublisherStatus.ACTIVE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public PublisherStatus getStatus() {
        return this.status;
    }

    public void setStatus(PublisherStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PublisherDTO)) {
            return false;
        }

        PublisherDTO publisherDTO = (PublisherDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, publisherDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PublisherDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", address='" + getAddress() + "'" +
            ", phone='" + getPhone() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
