package com.luanvan.userservice.command.event;

import com.luanvan.userservice.command.data.Province;
import com.luanvan.userservice.command.data.repository.ProvinceRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProvinceEventsHandler {

    @Autowired
    private ProvinceRepository provinceRepository;

    @EventHandler
    public void on(ProvinceCreatedEvent event) {
        Province province = new Province();
        BeanUtils.copyProperties(event, province);
        provinceRepository.save(province);
    }
}
