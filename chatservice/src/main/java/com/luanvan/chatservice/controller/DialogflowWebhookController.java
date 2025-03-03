package com.luanvan.chatservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/webhook")
public class DialogflowWebhookController {
    @PostMapping
    public Map<String, Object> handleWebhook(@RequestBody JsonNode request) {
        String intent = request.get("queryResult").get("intent").get("displayName").asText();
        String responseText;

        switch (intent) {
            case "TuVan":
                responseText = "Bạn cần mình tư vấn gì!";
                break;
            case "TimSanPham":
                responseText = "Bạn cần mặt hàng gì để mình tư vấn cho?";
                break;
            default:
                responseText = "Xin lỗi, bạn có thể nói rõ hơn không.";
        }

        Map<String, Object> response = new HashMap<>();
        response.put("fulfillmentText", responseText);
        return response;
    }
}
