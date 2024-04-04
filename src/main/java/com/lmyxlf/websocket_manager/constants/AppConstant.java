package com.lmyxlf.websocket_manager.constants;

import lombok.Getter;

/**
 * 通用枚举类
 */
@Getter
public enum AppConstant {
    HEARTBEAT_PARM_STRING(1,"heartbeat_parm_string"),
    HEARTBEAT_WORD(2,"heartbeat_word")
    ;


    private final Integer code;
    private final String msg;

    AppConstant(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}