package com.lmyxlf.websocket_manager.ws.handler;

import com.lmyxlf.websocket_manager.ws.cmd.WsBaseCmd;
import com.lmyxlf.websocket_manager.ws.cmd.WsCmd;

import javax.websocket.Session;

/**
 * websocket 处理继承接口
 */
public interface WsHandler<T extends WsCmd>{

    Object handle(T cmd, Session session, WsBaseCmd wsWebBaseCmd);
}