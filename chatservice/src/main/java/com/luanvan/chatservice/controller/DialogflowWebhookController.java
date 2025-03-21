package com.luanvan.chatservice.controller;

import com.google.gson.JsonObject;
import com.luanvan.chatservice.service.IntentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/webhook")
public class DialogflowWebhookController {

    @Autowired
    private IntentService intentService;

    @PostMapping
    public ResponseEntity<JsonObject> handleWebhook(@RequestBody JsonObject request) {
        JsonObject queryResult = request.getAsJsonObject("queryResult");
        String intentName = queryResult.getAsJsonObject("intent").get("displayName").getAsString();
        JsonObject parameters = queryResult.getAsJsonObject("parameters");
        log.info("handleWebhook parameters: {}", parameters);
        // Xử lý intent thông qua IntentService
        JsonObject response = intentService.handleIntent(intentName, parameters);
        log.info("handleWebhook response: {}", response);
        return ResponseEntity.ok(response);
    }
}
