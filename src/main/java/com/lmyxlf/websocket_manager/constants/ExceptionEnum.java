package com.lmyxlf.websocket_manager.constants;

import lombok.Getter;

/**
 * 异常枚举类
 */
@Getter
public enum ExceptionEnum {
    BAD_REQUEST("1001", "服务器异常"),
    DEVICE_NOT_ONLINE("1002", "设备不在线"),
    MAX_CONNECT("1003", "连接数过多"),
    BROWSER_OFFLINE("1004", "浏览器已下线"),
    NO_USER_ID("1005", "无用户id"),
    NO_MSG_ID("1006","无 msgId");

    private final String code;
    private final String msg;

    ExceptionEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}