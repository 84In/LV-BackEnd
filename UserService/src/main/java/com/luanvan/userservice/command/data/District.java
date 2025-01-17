package com.luanvan.userservice.command.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.luanvan.userservice.configuare.ProvinceDeserializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "districts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class District {

    @Id
    @JsonProperty("DistrictID")
    @Column(name = "district_id")
    private Integer districtId;

    @JsonProperty("ProvinceID")
    @ManyToOne
    @JoinColumn(name = "province_id")
    @JsonDeserialize(using = ProvinceDeserializer.class)
    private Province province;

    @JsonProperty("DistrictName")
    @Column(name = "district_name")
    private String districtName;

    @JsonProperty("NameExtension")
    @ElementCollection
    @CollectionTable(name = "district_name_extensions", joinColumns = @JoinColumn(name = "district_id"))
    @Column(name = "name_extension")
    private List<String> nameExtension;

    @JsonProperty("IsEnable")
    @Column(name = "is_enable")
    private Boolean isEnable;

    @JsonProperty("CanUpdateCOD")
    @Column(name = "can_update_cod")
    private Boolean canUpdateCod;

    @JsonProperty("CreatedAt")
    @Column(name = "created_at")
    private String createdAt;

    @JsonProperty("UpdatedAt")
    @Column(name = "updated_at")
    private String updatedAt;

}
