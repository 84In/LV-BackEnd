package com.luanvan.userservice.command.data;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "user_addresses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAddress {

    @Data
    public static class UserAddressId implements Serializable {
        private String userId;
        private String addressId;

        // Override equals() and hashCode()
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserAddressId that = (UserAddressId) o;
            return Objects.equals(userId, that.userId) && Objects.equals(addressId, that.addressId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, addressId);
        }
    }

    @Id
    private UserAddressId id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "address_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Address address;

    private boolean isDefault = false;
}
