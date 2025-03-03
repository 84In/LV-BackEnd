package com.luanvan.chatservice.service;

import com.google.cloud.dialogflow.v2.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DialogflowService {

    @Value("${google.dialogflow.project-id}")
    private String projectId;

    public String detectIntent(String sessionId, String message) {
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            SessionName session = SessionName.of(projectId, sessionId);

            TextInput textInput = TextInput.newBuilder()
                    .setText(message)
                    .setLanguageCode("vi")
                    .build();

            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

            DetectIntentRequest request = DetectIntentRequest.newBuilder()
                    .setSession(session.toString())
                    .setQueryInput(queryInput)
                    .build();

            DetectIntentResponse response = sessionsClient.detectIntent(request);
            return response.getQueryResult().getFulfillmentText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Xin lỗi, tôi không thể xử lý yêu cầu ngay bây giờ.";
        }
    }
}
