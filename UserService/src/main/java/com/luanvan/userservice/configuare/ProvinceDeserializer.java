package com.luanvan.userservice.configuare;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.luanvan.userservice.command.data.Province;
import com.luanvan.userservice.command.data.repository.ProvinceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class ProvinceDeserializer extends JsonDeserializer<Province> {
    @Autowired
    private ProvinceRepository provinceRepository;

    @Override
    public Province deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // Kiểm tra kiểu dữ liệu của node (IntNode hoặc TextNode)
        String provinceIdStr = null;
        if (p.getCurrentValue() instanceof IntNode) {
            provinceIdStr = String.valueOf(p.getValueAsInt());  // Nếu là IntNode
        } else if (p.getCurrentValue() instanceof TextNode) {
            provinceIdStr = p.getValueAsString();  // Nếu là TextNode
        }

        if (provinceIdStr != null) {
            log.info(provinceIdStr);
            Integer provinceId = Integer.parseInt(provinceIdStr);

            // Truy vấn Province từ database bằng provinceId
            return provinceRepository.findByProvinceId(provinceId);
        }

        // Trường hợp không có provinceId hoặc dữ liệu sai
        return null;
    }
}
