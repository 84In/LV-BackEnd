package com.luanvan.orderservice.services;

import com.luanvan.commonservice.entity.PaymentStatus;
import com.luanvan.commonservice.services.DatabaseBackupService;
import com.luanvan.orderservice.command.command.CancelOrderCommand;
import com.luanvan.orderservice.entity.Order;
import com.luanvan.orderservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class OrderService {
    @Value("${spring.datasource.username}")
    private String dbUser;
    @Value("${spring.datasource.password}")
    private String dbPassword;
    private String dbName = "orderservicedb";
    @Autowired
    private DatabaseBackupService databaseBackupService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CommandGateway commandGateway;

    // Chạy mỗi 30 phút để kiểm tra các đơn hàng quá 24 giờ
    @Scheduled(fixedRate = 30 * 60 * 1000)
    @Transactional
    public void checkAndCancelExpiredOrders() {
        log.info("Checking for expired orders...");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredThreshold = now.minusHours(24);
        PaymentStatus pendingPayment = PaymentStatus.PENDING;

        // Lấy danh sách các đơn hàng có payment PENDING và createdAt quá 24h so với hiện tại
        List<Order> expiredOrders = orderRepository.findExpiredOrders(pendingPayment, expiredThreshold);

        for (Order order : expiredOrders) {
            log.info("Cancelling expired order: ID={}, Status={}, PaymentStatus={}, PaymentCreatedAt={}",
                    order.getId(),
                    order.getOrderStatus().getCodeName(),
                    order.getPayment() != null ? order.getPayment().getStatus() : "No Payment",
                    order.getPayment() != null ? order.getPayment().getCreatedAt() : "No Payment");
            CancelOrderCommand cancelOrderCommand = new CancelOrderCommand(order.getId());
            commandGateway.send(cancelOrderCommand);
        }
    }

    @Scheduled(cron = "0 0 0 */7 * ?") // Chạy mỗi 7 ngày lúc 00:00
    public void backupDatabase() {
        log.info("🔹 Backup Order Database...");
        databaseBackupService.backupDatabase(dbName, dbName, dbUser, dbPassword);
    }
}
