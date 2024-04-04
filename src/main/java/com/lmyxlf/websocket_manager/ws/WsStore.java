package com.lmyxlf.websocket_manager.ws;

import com.lmyxlf.websocket_manager.ws.cmd.WsCmdType;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.websocket.Session;

/**
 * websocket 客户端
 */
@Data
@Accessors(chain = true)
public class WsStore {

    private String clientId;

    private Session session;

    private WsCmdType lastCmdType = null;
}