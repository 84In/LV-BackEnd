package com.luanvan.userservice.configuare;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luanvan.userservice.command.data.District;
import com.luanvan.userservice.command.data.Province;
import com.luanvan.userservice.command.data.Role;
import com.luanvan.userservice.command.data.Ward;
import com.luanvan.userservice.command.data.repository.DistrictRepository;
import com.luanvan.userservice.command.data.repository.ProvinceRepository;
import com.luanvan.userservice.command.data.repository.RoleRepository;
import com.luanvan.userservice.command.data.repository.WardRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class DataLoader {

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private WardRepository wardRepository;

    @Autowired
    private RoleRepository roleRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    @Transactional
    public void initDataAddress(){
        try {
            if(provinceRepository.count() == 0){
                List<Province> provinces = readJson("provinces.json", new TypeReference<>() {});
                provinceRepository.saveAll(provinces);
                log.info("✅ Dữ liệu Province đã được load và lưu vào cơ sở dữ liệu thành công!");
            }else {
                log.debug("Province data already exists. Skipping initialization.");
            }
            if (districtRepository.count() == 0) {
                List<District> districts = readJson("districts.json", new TypeReference<>() {});
                districtRepository.saveAll(districts);
                log.info("✅ Dữ liệu District đã được load và lưu vào cơ sở dữ liệu thành công!");
            } else {
                log.debug("District data already exists. Skipping initialization.");
            }

            if (wardRepository.count() == 0) {
                List<Ward> wards = readJson("wards.json", new TypeReference<>() {});
                wardRepository.saveAll(wards);
                log.info("✅ Dữ liệu Ward đã được load và lưu vào cơ sở dữ liệu thành công!");
            } else {
                log.debug("Ward data already exists. Skipping initialization.");
            }
        }catch (IOException e){
            log.error("❌ Lỗi khi đọc JSON file: " + e.getMessage());
        }
    }

    @PostConstruct
    @Transactional
    public void initDataRole(){
        if(roleRepository.count() == 0){
            createRole("admin", "Quản lý toàn bộ hệ thống");
            createRole("staff", "Nhân viên thực hiện các chức năng về đơn hàng, dịch vụ, tư vấn");
            createRole("customer", "Khách hàng sử dụng dịch vụ");
        }
    }

    private <T> List<T> readJson(String fileName, TypeReference<List<T>> typeReference) throws IOException {
        File file = new File("src/main/resources/db/" + fileName);
        return objectMapper.readValue(file, typeReference);
    }

    private void createRole(String roleName, String description) {
        Role role = new Role(roleName,description);
        roleRepository.save(role);
        log.info("✅ Vai trò " + roleName + " đã được tạo!");
    }
}
