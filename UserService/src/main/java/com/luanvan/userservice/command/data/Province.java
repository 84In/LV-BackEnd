package com.luanvan.userservice.command.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "provinces")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Province {

    @Id
    @JsonProperty("ProvinceID")
    @Column(name = "province_id")
    private Integer provinceId;

    @JsonProperty("ProvinceName")
    @Column(name = "province_name")
    private String provinceName;

    @JsonProperty("CountryID")
    @Column(name = "country_id")
    private Integer countryId;

    @JsonProperty("Code")
    @Column(name = "code")
    private String code;

    @JsonProperty("NameExtension")
    @ElementCollection
    @CollectionTable(name = "province_name_extensions", joinColumns = @JoinColumn(name = "province_id"))
    @Column(name = "name_extension")
    private List<String> nameExtension;

    @JsonProperty("IsEnable")
    @Column(name = "is_enable")
    private Boolean isEnable;

    @JsonProperty("RegionID")
    @Column(name = "region_id")
    private Integer regionId;

    @JsonProperty("RegionCPN")
    @Column(name = "region_cpn")
    private Integer regionCpn;

    @JsonProperty("CanUpdateCOD")
    @Column(name = "can_update_cod")
    private Boolean canUpdateCod;

    @JsonProperty("Status")
    @Column(name = "status")
    private Integer status;

    @JsonProperty("CreatedAt")
    @Column(name = "created_at")
    private String createdAt;

    @JsonProperty("UpdatedAt")
    @Column(name = "updated_at")
    private String updatedAt;
}
