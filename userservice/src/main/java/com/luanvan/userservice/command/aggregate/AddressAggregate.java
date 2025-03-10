package com.luanvan.userservice.command.aggregate;

import com.luanvan.userservice.command.command.ChangeDefaultAddressCommand;
import com.luanvan.userservice.command.command.CreateAddressCommand;
import com.luanvan.userservice.command.command.RemoveAddressCommand;
import com.luanvan.userservice.command.command.UpdateAddressCommand;
import com.luanvan.userservice.command.event.AddressChangeDefaultEvent;
import com.luanvan.userservice.command.event.AddressCreatedEvent;
import com.luanvan.userservice.command.event.AddressRemoveEvent;
import com.luanvan.userservice.command.event.AddressUpdatedEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;


@Aggregate
@NoArgsConstructor
@Slf4j
public class AddressAggregate {
    @AggregateIdentifier
    private String id;
    private Boolean isActive;
    private String phone;
    private String houseNumberAndStreet;
    private Integer provinceId;
    private Integer districtId;
    private Integer wardId;
    private String userId;
    private Boolean isDefault;


    @CommandHandler
    public AddressAggregate(CreateAddressCommand command) {
        AddressCreatedEvent event = new AddressCreatedEvent();
        BeanUtils.copyProperties(command, event);
        log.info("AddressCreatedEvent: {}", event.getId());
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(UpdateAddressCommand command) {
        AddressUpdatedEvent event = new AddressUpdatedEvent();
        BeanUtils.copyProperties(command, event);
        log.info("AddressUpdatedEvent: {}",event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(ChangeDefaultAddressCommand command) {
        AddressChangeDefaultEvent event = new AddressChangeDefaultEvent();
        BeanUtils.copyProperties(command, event);
        log.info("AddressChangeDefaultEvent: {}",event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(RemoveAddressCommand command) {
        AddressRemoveEvent event = new AddressRemoveEvent();
        BeanUtils.copyProperties(command, event);
        log.info("AddressRemoveEvent: {}",event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(AddressCreatedEvent event) {
        this.id = event.getId();
        this.isActive = event.getIsActive();
        this.phone = event.getPhone();
        this.houseNumberAndStreet = event.getHouseNumberAndStreet();
        this.provinceId = event.getProvinceId();
        this.districtId = event.getDistrictId();
        this.wardId = event.getWardId();
        this.userId = event.getUserId();
        this.isDefault = event.getIsDefault();
    }

    @EventSourcingHandler
    public void on(AddressUpdatedEvent event) {
        this.id = event.getId();
        this.isActive = event.getIsActive();
        this.phone = event.getPhone();
        this.houseNumberAndStreet = event.getHouseNumberAndStreet();
        this.provinceId = event.getProvinceId();
        this.districtId = event.getDistrictId();
        this.wardId = event.getWardId();
        this.userId = event.getUserId();
        this.isDefault = event.getIsDefault();
    }

    @EventSourcingHandler
    public void on(AddressChangeDefaultEvent event){
        this.id = event.getId();
        this.userId = event.getUserId();
    }

    @EventSourcingHandler
    public void on(AddressRemoveEvent event){
        this.id = event.getId();
        this.userId = event.getUserId();
    }
}
