package com.luanvan.chatservice.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.luanvan.commonservice.model.response.PageProductResponse;
import com.luanvan.commonservice.model.response.ProductResponseModel;
import com.luanvan.commonservice.queries.GetAllProductWithFilterQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class IntentService {

    @Value("${app.base-url}")
    private String baseUrl;

    @Autowired
    QueryGateway queryGateway;

    public JsonObject handleIntent(String intentName, JsonObject parameters) {
        switch (intentName) {
            case "ITuVan":
                return handleTuVanGiay(parameters);
            default:
                JsonObject response = new JsonObject();
                response.addProperty("fulfillmentText", "Xin lỗi, chúng tôi chưa hiểu câu hỏi của bạn.");
                return response;
        }
    }

    private JsonObject handleTuVanGiay(JsonObject parameters) {
        String category = parameters.has("ECategory") ? parameters.get("ECategory").getAsString() : "";
        String size = parameters.has("ESize") ? parameters.get("ESize").getAsString() : "";
        String color = parameters.has("EColor") ? parameters.get("EColor").getAsString() : "";
        GetAllProductWithFilterQuery query = new GetAllProductWithFilterQuery();
        if (!category.isBlank()) {
            query.setCategory(category);
        }
        if (!size.isBlank()) {
            query.setSize(size);
        }
        if (!color.isBlank()) {
            query.setColor(color);
        }

        PageProductResponse result = queryGateway.query(query, ResponseTypes.instanceOf(PageProductResponse.class)).join();

        List<ProductResponseModel> data = result.getContent().stream().toList();

        JsonObject response = new JsonObject();
        JsonArray fulfillmentMessages = new JsonArray();

        JsonArray cards = new JsonArray();
        data.stream().limit(3).forEach(product -> {
            List<String> imageUrls = Arrays.asList(product.getImages().split(","));
            String firstImageUrl = imageUrls.isEmpty() ? "" : imageUrls.get(0);

            cards.add(createProductCard(
                    product.getName(),
                    firstImageUrl,
                    "Xem chi tiết",
                    catUrl(product.getId())
            ));
        });
        JsonObject payload = new JsonObject();
        payload.add("richContent", cards);

        JsonObject payloadWrapper = new JsonObject();
        payloadWrapper.add("payload", payload);

        fulfillmentMessages.add(payloadWrapper);

        response.add("fulfillmentMessages", fulfillmentMessages);

        return response;

    }

    private String catUrl(String id){
        return baseUrl + id;
    }

    private JsonObject createProductCard(String title, String imageUrl, String buttonText, String buttonUrl) {
        JsonObject item = new JsonObject();
        item.addProperty("title", title);

        JsonObject image = new JsonObject();
        image.addProperty("imageUri", imageUrl);
        item.add("image", image);

        JsonArray buttons = new JsonArray();
        JsonObject action = new JsonObject();
        action.addProperty("text", buttonText);
        action.addProperty("postback", buttonUrl);
        buttons.add(action);

        item.add("buttons", buttons);

        return item;
    }
}
