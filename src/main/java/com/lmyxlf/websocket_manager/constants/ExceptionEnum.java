package com.lmyxlf.websocket_manager.constants;

import lombok.Getter;

/**
 * 异常枚举类
 */
@Getter
public enum ExceptionEnum {
    BAD_REQUEST("1001", "服务器异常"),
    DEVICE_NOT_ONLINE("1002", "设备不在线");

    private final String code;
    private final String msg;

    ExceptionEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}