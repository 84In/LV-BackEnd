package com.luanvan.chatservice.service;

import com.google.gson.Gson;
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

import java.util.List;

@Service
public class IntentService {

    @Value("${app.base-url:http://localhost:4000/}")
    private String baseUrl;

    @Autowired
    QueryGateway queryGateway;

    private final Gson gson = new Gson();

    public String handleIntent(String intentName, JsonObject parameters) {
        switch (intentName) {
            case "ITuVan":
                return gson.toJson(handleTuVanGiay(parameters));
            default:
                JsonObject response = new JsonObject();
                response.addProperty("fulfillmentText", "Xin lỗi, chúng tôi chưa hiểu câu hỏi của bạn.");
                return gson.toJson(response);
        }
    }

    //    private JsonObject handleTuVanGiay(JsonObject parameters) {
//        String category = parameters.has("ECategory") ? parameters.get("ECategory").getAsString() : "";
//        String size = parameters.has("ESize") ? parameters.get("ESize").getAsString() : "";
//        String color = parameters.has("EColor") ? parameters.get("EColor").getAsString() : "";
//        GetAllProductWithFilterQuery query = new GetAllProductWithFilterQuery();
//        query.setPageNumber(0);
//        query.setPageSize(10);
//        if (!category.isBlank()) {
//            query.setCategory(category);
//        }
//        if (!size.isBlank()) {
//            query.setSize(size);
//        }
//        if (!color.isBlank()) {
//            query.setColor(color);
//        }
//
//        PageProductResponse result = queryGateway.query(query, ResponseTypes.instanceOf(PageProductResponse.class)).join();
//
//        List<ProductResponseModel> data = result.getContent().stream().toList();
//
//        JsonObject response = new JsonObject();
//        JsonArray fulfillmentMessages = new JsonArray();
//
//        JsonArray cards = new JsonArray();
//        data.stream().limit(3).forEach(product -> {
//            List<String> imageUrls = Arrays.asList(product.getImages().split(","));
//            String firstImageUrl = imageUrls.isEmpty() ? "" : imageUrls.get(0);
//
//            cards.add(createProductCard(
//                    product.getName(),
//                    firstImageUrl,
//                    "Xem chi tiết",
//                    catUrl(product.getId())
//            ));
//        });
//        JsonObject payload = new JsonObject();
//        payload.add("richContent", cards);
//
//        JsonObject payloadWrapper = new JsonObject();
//        payloadWrapper.add("payload", payload);
//
//        fulfillmentMessages.add(payloadWrapper);
//
//        response.add("fulfillmentMessages", fulfillmentMessages);
//
//        return response;
//
//    }
    private JsonObject handleTuVanGiay(JsonObject parameters) {
        String category = parameters.has("ECategory") ? parameters.get("ECategory").getAsString() : "";
        String size = parameters.has("ESize") ? parameters.get("ESize").getAsString() : "";
        String color = parameters.has("EColor") ? parameters.get("EColor").getAsString() : "";

        GetAllProductWithFilterQuery query = new GetAllProductWithFilterQuery();
        query.setPageNumber(0);
        query.setPageSize(10);
        if (!category.isBlank()) query.setCategory(category);
        if (!size.isBlank()) query.setSize(size);
        if (!color.isBlank()) query.setColor(color);

        PageProductResponse result = queryGateway.query(query, ResponseTypes.instanceOf(PageProductResponse.class)).join();
        List<ProductResponseModel> data = result.getContent();

        JsonObject response = new JsonObject();
        JsonArray fulfillmentMessages = new JsonArray();

        JsonObject payload = new JsonObject();
        JsonArray richContent = new JsonArray();
        JsonArray contentArray = new JsonArray();

        data.stream().limit(3).forEach(product -> {
            JsonObject infoCard = new JsonObject();
            infoCard.addProperty("type", "info");
            infoCard.addProperty("title", product.getName());
            infoCard.addProperty("subtitle", product.getDescription());

            // Đảm bảo hình ảnh có src
            JsonObject image = new JsonObject();
            image.addProperty("type", "image");
            image.addProperty("rawUrl", product.getImages().split(",")[0]);
            image.addProperty("accessibilityText", "Hình ảnh sản phẩm");

            // Định nghĩa nút bấm
            JsonObject button = new JsonObject();
            button.addProperty("type", "button");
            button.addProperty("text", "Xem chi tiết");
            button.addProperty("icon", "launch"); // Thêm icon nếu cần
            button.addProperty("link", catUrl(product.getId()));

            // Thêm vào mảng content
            contentArray.add(infoCard);
            contentArray.add(image);
            contentArray.add(button);
        });

        richContent.add(contentArray);
        payload.add("richContent", richContent);

        JsonObject payloadWrapper = new JsonObject();
        payloadWrapper.add("payload", payload);
        fulfillmentMessages.add(payloadWrapper);

        response.add("fulfillmentMessages", fulfillmentMessages);
        return response;
    }




    private String catUrl(String id) {
        return baseUrl + "product/" + id;
    }

}
