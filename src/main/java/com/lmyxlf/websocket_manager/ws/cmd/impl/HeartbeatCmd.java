package com.lmyxlf.websocket_manager.ws.cmd.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lmyxlf.websocket_manager.constants.AppConstant;
import com.lmyxlf.websocket_manager.ws.cmd.WsCmd;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ws 心跳请求体
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HeartbeatCmd implements WsCmd {

    /**
     * 客户端ping，服务端pong
     */
    private String param = AppConstant.HEARTBEAT_PARM_STRING.getMsg();

}