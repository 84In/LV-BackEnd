package com.luanvan.orderservice.command.saga;

import com.luanvan.commonservice.command.RollBackStockProductCommand;
import com.luanvan.commonservice.command.UpdateStockProductCommand;
import com.luanvan.orderservice.command.command.ChangeStatusOrderCommand;
import com.luanvan.orderservice.command.event.OrderCreateEvent;
import com.luanvan.orderservice.services.OrderKafkaService;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Saga
public class OrderSaga {
    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private OrderKafkaService orderKafkaService;


    //Danh sách các product update stock sold thành công
    private List<UpdateStockProductCommand> successfulUpdates = new ArrayList<>();

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(OrderCreateEvent event) {
        log.info("OrderCreateEvent in saga for orderId: {}", event.getId());

        // Cập nhật lại stock và sold bên ProductVariant
        try {
            for (OrderCreateEvent.OrderDetail od : event.getOrderDetails()) {
                UpdateStockProductCommand updateStockProductCommand = UpdateStockProductCommand.builder()
                        .id(od.getProductId())
                        .quantity(od.getQuantity())
                        .colorId(od.getColorId())
                        .sizeId(od.getSizeId())
                        .build();
                commandGateway.sendAndWait(updateStockProductCommand);
                successfulUpdates.add(updateStockProductCommand);
            }
            Thread.sleep(500); // Đợi DB lưu hoàn tất
            CompletableFuture.runAsync(() -> orderKafkaService.sendOrder(event.getId()));
            SagaLifecycle.end();
        } catch (Exception e) {
            log.error("Error OrderCreateEvent: " + e.getMessage());

            // Rollback lại stock cho sản phẩm
            List<UpdateStockProductCommand> rollbackSuccessfulUpdates = new ArrayList<>(successfulUpdates);
            for (UpdateStockProductCommand cmd : rollbackSuccessfulUpdates) {
                RollBackStockProductCommand rollBackCmd = RollBackStockProductCommand.builder()
                        .id(cmd.getId())
                        .quantity(cmd.getQuantity())
                        .colorId(cmd.getColorId())
                        .sizeId(cmd.getSizeId())
                        .build();
                commandGateway.sendAndWait(rollBackCmd);
            }
            rollBackOrder(event);
            SagaLifecycle.end();
        }
    }

    private void rollBackOrder(OrderCreateEvent event) {
        String cancelledStatus = "cancelled";
        log.info("Rollback order event: {}", event.getId());
        ChangeStatusOrderCommand command = new ChangeStatusOrderCommand(event.getId(), cancelledStatus);
        commandGateway.sendAndWait(command);
    }
}
