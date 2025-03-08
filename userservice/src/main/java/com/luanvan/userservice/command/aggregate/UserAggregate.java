package com.luanvan.userservice.command.aggregate;

import com.luanvan.userservice.command.command.*;
import com.luanvan.userservice.command.event.*;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;


@Slf4j
@Aggregate
@NoArgsConstructor
public class UserAggregate {
    @AggregateIdentifier
    private String id;
    private String username;
    private String password;
    private Boolean active;
    private String email;
    private String phone;
    private String lastName;
    private String firstName;
    private String roleName;

    @CommandHandler
    public UserAggregate(CreateUserCommand command) {
        UserCreatedEvent event = new UserCreatedEvent();
        BeanUtils.copyProperties(command, event);
        log.info("UserAggregate - UserCreatedEvent: {}", event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(UpdateUserCommand command) {
        UserUpdatedEvent event = new UserUpdatedEvent();
        BeanUtils.copyProperties(command, event);
        log.info("UserUpdatedEvent: {}", event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(DeleteUserCommand command) {
        UserDeletedEvent event = new UserDeletedEvent();
        event.setId(command.getId());
        log.info("UserDeletedEvent: {}", event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(ChangeStatusUserCommand command) {

        UserChangeStatusEvent event = new UserChangeStatusEvent();
        event.setId(command.getId());
        event.setActive(command.getActive());
        log.info("UserRemovedEvent: {}", event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(ChangePasswordUserCommand command) {
        UserChangePasswordEvent event = new UserChangePasswordEvent();
        event.setPassword(command.getPassword());
        log.info("UserChangedEvent: {}", event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(UserCreatedEvent event) {
        this.id = event.getId();
        this.username = event.getUsername();
        this.password = event.getPassword();
        this.active = event.getActive();
        this.email = event.getEmail();
        this.phone = event.getPhone();
        this.lastName = event.getLastName();
        this.firstName = event.getFirstName();
        this.roleName = event.getRoleName();

        log.info("UserCreatedEventSourcingHandler id: {}", event.getId());

    }

    @EventSourcingHandler
    public void on(UserUpdatedEvent event) {
        this.id = event.getId();
        this.username = event.getUsername();
        this.email = event.getEmail();
        this.phone = event.getPhone();
        this.lastName = event.getLastName();
        this.firstName = event.getFirstName();
        this.roleName = event.getRoleName();
        log.info("UserUpdatedEventHandler: {}", event);
    }

    @EventSourcingHandler
    public void on(UserDeletedEvent event) {
        this.id = event.getId();
    }

    @EventSourcingHandler
    public void on(UserChangeStatusEvent event) {
        this.id = event.getId();
        this.active = event.getActive();
    }
    @EventSourcingHandler
    public void on(UserChangePasswordEvent event) {
        this.id = event.getId();
        this.password = event.getPassword();
    }

}
