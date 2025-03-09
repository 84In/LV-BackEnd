package com.luanvan.notificationservice.service;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.command.SendConfirmedOrderMailCommand;
import com.luanvan.commonservice.model.response.OrderResponseModel;
import com.luanvan.commonservice.model.response.UserResponseModel;
import com.luanvan.commonservice.queries.GetOrderQuery;
import com.luanvan.commonservice.queries.GetUserQuery;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Slf4j
@Service
public class MailService {
    @Autowired
    private QueryGateway queryGateway;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;

    @KafkaListener(topics = "send-confirmed-order-mail-topic", groupId = "mail-group")
    public void handle(SendConfirmedOrderMailCommand command) {
        log.info("Send mail for username: {}", command.getUsername());
        GetUserQuery userQuery = new GetUserQuery(command.getUsername());
        UserResponseModel user = queryGateway.query(userQuery, ResponseTypes.instanceOf(UserResponseModel.class))
                .exceptionally(ex -> {
                    throw new AppException(ErrorCode.USER_NOT_EXISTED);
                })
                .join();
        GetOrderQuery orderQuery = new GetOrderQuery(command.getOrderId());
        OrderResponseModel order = queryGateway.query(orderQuery, ResponseTypes.instanceOf(OrderResponseModel.class))
                .exceptionally(ex -> {
                    throw new AppException(ErrorCode.ORDER_NOT_EXISTED);
                })
                .join();
        // Prepare the context for Thymeleaf template
        Context context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("delivery", order.getDelivery());
        String formattedDate = order.getCreatedAt() != null
                ? order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                : LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        context.setVariable("createdAt", formattedDate);
        context.setVariable("orderId", order.getId());
        context.setVariable("orderDetails", order.getOrderDetails());
        context.setVariable("totalPrice", order.getTotalPrice());

        // Process Thymeleaf template into a String
        String htmlContent = templateEngine.process("order-confirmation", context);

        // Build the email
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject("Xác nhận đơn hàng #" + order.getId());
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send email: {}", e.getMessage());
            throw new AppException(ErrorCode.EMAIL_SENDING_FAILED);
        }
    }

}

