package com.luanvan.userservice.command.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Sử dụng kiểu số tự động tăng
    private String id;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "house_number_and_street", nullable = false)
    private String houseNumberAndStreet;

    @ManyToOne
    @JoinColumn(name = "province_id", nullable = false, referencedColumnName = "province_id")
    private Province province;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false, referencedColumnName = "district_id")
    private District district;

    @ManyToOne
    @JoinColumn(name = "ward_id", referencedColumnName = "ward_code") // Dùng khóa ngoại là `ward_code` của Ward
    private Ward ward;


    @CreationTimestamp
    @Column(name = "created_at", updatable = false) // Không cho phép cập nhật giá trị này
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at") // Tự động cập nhật khi có thay đổi
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void validateAddress() {
        // Nếu ward không thuộc district hoặc district không thuộc province, ném lỗi.
        if (ward != null && !ward.getDistrict().getDistrictId().equals(district.getDistrictId())) {
            throw new IllegalArgumentException("Ward không thuộc District!");
        }

        if (!district.getDistrictId().equals(province.getProvinceId())) {
            throw new IllegalArgumentException("District không thuộc Province!");
        }
    }
}
