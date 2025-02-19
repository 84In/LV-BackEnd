package com.luanvan.userservice.command.aggregate;

import com.luanvan.userservice.command.command.CreateUserCommand;
import com.luanvan.userservice.command.command.DeleteUserCommand;
import com.luanvan.userservice.command.command.RemoveUserCommand;
import com.luanvan.userservice.command.command.UpdateUserCommand;
import com.luanvan.userservice.command.event.UserCreatedEvent;
import com.luanvan.userservice.command.event.UserDeletedEvent;
import com.luanvan.userservice.command.event.UserRemoveEvent;
import com.luanvan.userservice.command.event.UserUpdatedEvent;
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

        log.info("UserCreatedEventSourcingHandler: {}", event);

    }

    @EventSourcingHandler
    public void on(UserUpdatedEvent event) {
        this.id = event.getId();
        this.password = event.getPassword();
        this.active = event.getActive();
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
    public void on(UserRemoveEvent event) {
        this.id = event.getId();
        this.active = event.getActive();
    }

}
