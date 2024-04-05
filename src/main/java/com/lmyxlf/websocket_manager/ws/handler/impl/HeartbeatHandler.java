package com.lmyxlf.websocket_manager.ws.handler.impl;


import com.lmyxlf.websocket_manager.constants.AppConstant;
import com.lmyxlf.websocket_manager.ws.WsManager;
import com.lmyxlf.websocket_manager.ws.WsServerEndpoint;
import com.lmyxlf.websocket_manager.ws.cmd.WsBaseCmd;
import com.lmyxlf.websocket_manager.ws.cmd.WsCmdType;
import com.lmyxlf.websocket_manager.ws.cmd.impl.HeartbeatCmd;
import com.lmyxlf.websocket_manager.ws.handler.WsHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.Session;

/**
 * 心跳处理
 */
@Slf4j
@Component
public class HeartbeatHandler implements WsHandler<HeartbeatCmd> {

    private static final WsCmdType CMD_TYPE = WsCmdType.PING;

    @PostConstruct
    public void init() {
        WsServerEndpoint.registerHandler(CMD_TYPE, this);
    }

    @Override
    public Object handle(HeartbeatCmd cmd, Session session, WsBaseCmd wsWebBaseCmd) {


        // 缓存续期
        boolean equals = cmd.getParam().equals(AppConstant.HEARTBEAT_PARM_STRING.getMsg());
        if (equals) {
            boolean exist = WsManager.renewCache(session);

            cmd.setParam(AppConstant.HEARTBEAT_WORD.getMsg());

            if (exist) {
                WsManager.sendOK(session, wsWebBaseCmd, cmd);
            }
        }

        return null;
    }
}