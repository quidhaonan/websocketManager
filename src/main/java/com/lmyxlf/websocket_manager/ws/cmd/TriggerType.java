package com.lmyxlf.websocket_manager.ws.cmd;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * websocket 请求类型
 */
@Getter
@AllArgsConstructor
public enum TriggerType {

    USER_SEND,
    EVENT,
    ;
}