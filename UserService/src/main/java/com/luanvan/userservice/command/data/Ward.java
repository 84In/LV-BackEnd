package com.luanvan.userservice.command.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.luanvan.userservice.configuare.DistrictDeserializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "wards")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ward {

    @Id
    @Column(name = "ward_code")
    @JsonProperty("WardCode")
    private String wardCode;

    @ManyToOne
    @JoinColumn(name = "district_id", referencedColumnName = "district_id")
    @JsonProperty("DistrictID")
    @JsonDeserialize(using = DistrictDeserializer.class)
    private District district;

    @Column(name = "ward_name")
    @JsonProperty("WardName")
    private String wardName;

    @ElementCollection
    @CollectionTable(name = "ward_name_extensions", joinColumns = @JoinColumn(name = "ward_code"))
    @Column(name = "name_extension")
    @JsonProperty("NameExtension")
    private List<String> nameExtension;

    @Column(name = "is_enable")
    @JsonProperty("IsEnable")
    private Boolean isEnable;

    @Column(name = "can_update_cod")
    @JsonProperty("CanUpdateCOD")
    private Boolean canUpdateCod;

    @JsonProperty("CreatedAt")
    @Column(name = "created_at")
    private String createdAt;

    @JsonProperty("UpdatedAt")
    @Column(name = "updated_at")
    private String updatedAt;
}
