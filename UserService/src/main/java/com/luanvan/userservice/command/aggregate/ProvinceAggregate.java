package com.luanvan.userservice.command.aggregate;

import com.luanvan.userservice.command.command.CreateProvinceCommand;
import com.luanvan.userservice.command.event.ProvinceCreatedEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@NoArgsConstructor
public class ProvinceAggregate {

    @AggregateIdentifier
    private Integer code;
    private String name;
    private String codeName;
    private String divisionType;
    private Boolean isActive = true;

    @CommandHandler
    public ProvinceAggregate(CreateProvinceCommand command) {
        ProvinceCreatedEvent event = new ProvinceCreatedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(ProvinceCreatedEvent event) {
        this.code = event.getCode();
        this.name = event.getName();
        this.codeName = event.getCodeName();
        this.divisionType = event.getDivisionType();
        this.isActive = event.getIsActive();
    }
}
