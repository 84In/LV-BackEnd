package com.luanvan.userservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luanvan.userservice.entity.District;
import com.luanvan.userservice.entity.Province;
import com.luanvan.userservice.entity.Role;
import com.luanvan.userservice.entity.Ward;
import com.luanvan.userservice.repository.DistrictRepository;
import com.luanvan.userservice.repository.ProvinceRepository;
import com.luanvan.userservice.repository.RoleRepository;
import com.luanvan.userservice.repository.WardRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DataLoaderService {

    @Autowired
    private ProvinceRepository provinceRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private WardRepository wardRepository;

    @Autowired
    private RoleRepository roleRepository;

    public void loadDataLocationFromJson(InputStream inputStream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode provincesJson = objectMapper.readTree(inputStream);

        for (JsonNode provinceNode : provincesJson) {
            String ProvinceCodeName = provinceNode.get("codename").asText();
            if (!provinceRepository.existsByCodeName(ProvinceCodeName)) {
                Province province = new Province();
                province.setName(provinceNode.get("name").asText());
                province.setCodeName(provinceNode.get("codename").asText());
                province.setDivisionType(provinceNode.get("division_type").asText());

                JsonNode districtsJson = provinceNode.get("districts");

                for (JsonNode districtNode : districtsJson) {
                    District district = new District();
                    district.setName(districtNode.get("name").asText());
                    district.setCodeName(districtNode.get("codename").asText());
                    district.setDivisionType(districtNode.get("division_type").asText());
                    district.setProvince(province);

                    JsonNode wardsJson = districtNode.get("wards");
                    for (JsonNode wardNode : wardsJson) {
                        Ward ward = new Ward();
                        ward.setName(wardNode.get("name").asText());
                        ward.setCodeName(wardNode.get("codename").asText());
                        ward.setDivisionType(wardNode.get("division_type").asText());
                        ward.setDistrict(district); // Set the district for the ward

                        // Thêm ward vào district
                        district.getWards().add(ward);
                    }

                    province.getDistricts().add(district);
                }
                provinceRepository.save(province);
            }
        }
    }

    public void initDataRole() {

        createRole("admin", "Quản lý toàn bộ hệ thống");
        createRole("staff", "Nhân viên thực hiện các chức năng về đơn hàng, dịch vụ, tư vấn");
        createRole("customer", "Khách hàng sử dụng dịch vụ");

    }

    private void createRole(String roleName, String description) {
        Role role = new Role(roleName, description);
        if (!roleRepository.existsById(roleName)) {
            roleRepository.save(role);
            log.info("✅ Vai trò " + roleName + " đã được tạo!");
        }
    }
}
