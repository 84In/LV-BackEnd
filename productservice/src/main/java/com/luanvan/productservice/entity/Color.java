package com.luanvan.productservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "colors" )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Color {
    @Id
    private String id;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "code_name", length = 255)
    private String codeName;

    @Column(name = "color_code", length = 255)
    private String colorCode;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
