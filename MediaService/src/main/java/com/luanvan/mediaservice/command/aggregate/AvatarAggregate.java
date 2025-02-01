package com.luanvan.mediaservice.command.aggregate;

import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@NoArgsConstructor
public class AvatarAggregate {
    @AggregateIdentifier
    private String userId;

    private String avatarUrl;




}
