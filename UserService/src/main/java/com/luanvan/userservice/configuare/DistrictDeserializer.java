package com.luanvan.userservice.configuare;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.luanvan.userservice.command.data.District;
import com.luanvan.userservice.command.data.repository.DistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DistrictDeserializer extends JsonDeserializer<District> {
    @Autowired
    private DistrictRepository districtRepository;

    @Override
    public District deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // Kiểm tra kiểu dữ liệu của node (IntNode hoặc TextNode)
        String districtIdStr = null;
        if (p.getCurrentValue() instanceof IntNode) {
            districtIdStr = String.valueOf(p.getValueAsInt());  // Nếu là IntNode
        } else if (p.getCurrentValue() instanceof TextNode) {
            districtIdStr = p.getValueAsString();  // Nếu là TextNode
        }

        if (districtIdStr != null) {
            Integer districtId = Integer.parseInt(districtIdStr);

            // Truy vấn district từ database bằng districtId
            return districtRepository.findBydistrictId(districtId);
        }

        // Trường hợp không có districtId hoặc dữ liệu sai
        return null;
    }
}