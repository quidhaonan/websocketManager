package com.lmyxlf.websocket_manager.ws.cmd;

import com.lmyxlf.websocket_manager.ws.cmd.impl.HeartbeatCmd;
import com.lmyxlf.websocket_manager.ws.cmd.impl.InitCmd;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * websocket 请求注册枚举
 */
@Getter
@AllArgsConstructor
public enum WsCmdType {

    /**
     * isRequest：暂时无用字段，可取任意Boolean
     */

    PING("heartbeat", HeartbeatCmd.class, true),
    INIT("init", InitCmd.class, true),

    ;

    private final String type;

    private final Class<? extends WsCmd> resClazz;

    private final boolean isRequest;

    public static WsCmdType of(String textType) {
        if (textType == null) {
            return null;
        }

        for (WsCmdType value : values()) {
            if (value.type.equals(textType)) {
                return value;
            }
        }

        return null;
    }
}