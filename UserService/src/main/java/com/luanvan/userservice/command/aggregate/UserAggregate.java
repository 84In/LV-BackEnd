package com.luanvan.userservice.command.aggregate;

import com.luanvan.userservice.command.command.CreateUserCommand;
import com.luanvan.userservice.command.event.UserCreatedEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    private String avatar;
    private String roleName;

    @CommandHandler
    public UserAggregate(CreateUserCommand command){
        UserCreatedEvent event = new UserCreatedEvent();
        BeanUtils.copyProperties(command, event);
        log.info("UserCreatedEvent: {}", event);
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
        this.avatar = event.getAvatar();
        this.roleName = event.getRoleName();

        log.info("UserCreatedEventHandler: {}", event);

    }

}
