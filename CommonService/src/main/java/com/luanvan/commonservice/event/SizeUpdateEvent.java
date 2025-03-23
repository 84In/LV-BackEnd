package com.luanvan.commonservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SizeUpdateEvent {
    private String id;
    private String name;
    private String codeName;
    private Boolean isActive;
}
