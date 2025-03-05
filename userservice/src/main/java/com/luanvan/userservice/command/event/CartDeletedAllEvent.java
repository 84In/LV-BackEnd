package com.luanvan.userservice.command.event;

import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartDeletedAllEvent {
    private String id;
}
