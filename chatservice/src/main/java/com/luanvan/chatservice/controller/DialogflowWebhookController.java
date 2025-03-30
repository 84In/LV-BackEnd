package com.luanvan.chatservice.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.luanvan.chatservice.service.IntentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/webhook")
public class DialogflowWebhookController {

    @Autowired
    private IntentService intentService;

    @PostMapping
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<String> handleWebhook(@RequestBody String requestBody) {
        log.info("handleWebhook: {}", requestBody); // Log payload nhận được

        // Parse request từ String thành JsonObject
        JsonObject request = JsonParser.parseString(requestBody).getAsJsonObject();

        if (!request.has("queryResult") || request.get("queryResult").isJsonNull()) {
            log.error("queryResult is missing or null!");
            return ResponseEntity.badRequest().body("");
        }

        JsonObject queryResult = request.getAsJsonObject("queryResult");
        log.info("queryResult: {}", queryResult);

        String intentName = queryResult.getAsJsonObject("intent").get("displayName").getAsString();
        log.info("intentName: {}", intentName);

        JsonObject parameters = queryResult.getAsJsonObject("parameters");
        log.info("handleWebhook parameters: {}", parameters);

        // Xử lý intent thông qua IntentService
        String response = intentService.handleIntent(intentName, parameters);
        log.info("handleWebhook response: {}", response);

        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(response);
    }
}
