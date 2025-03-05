package com.luanvan.orderservice.configuration;

import com.luanvan.orderservice.entity.OrderStatus;
import com.luanvan.orderservice.repository.OrderStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApplicationInitConfig {

    private final OrderStatusRepository orderStatusRepository;

    @Bean
    ApplicationRunner initOrderStatus() {
        return args -> {
            createOrderStatus("confirmed", "Xác Nhận");
            createOrderStatus("prepared", "Chuẩn Bị Hàng");
            createOrderStatus("delivery", "Đang Giao");
            createOrderStatus("cancelled", "Hủy");
            createOrderStatus("completed", "Hoàn Tất");
        };
    }

    private void createOrderStatus(String codeName, String name) {
        OrderStatus orderStatus = OrderStatus.builder()
                .codeName(codeName)
                .name(name)
                .build();
        if (!orderStatusRepository.existsById(codeName)) {
            orderStatusRepository.save(orderStatus);
            log.info("Order status " + codeName + " đã được tạo");
        }
    }

}
