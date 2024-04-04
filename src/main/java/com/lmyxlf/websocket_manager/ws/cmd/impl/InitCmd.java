package com.lmyxlf.websocket_manager.ws.cmd.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lmyxlf.websocket_manager.ws.cmd.WsCmd;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 客户端注册请求体
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InitCmd implements WsCmd {

    private String clientId;
}