package com.luanvan.commonservice.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendCancelledOrderMailCommand {
    private String userId;
    private String orderId;
    private String reason;
}
