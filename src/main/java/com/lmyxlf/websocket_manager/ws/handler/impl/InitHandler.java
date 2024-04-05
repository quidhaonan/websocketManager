package com.lmyxlf.websocket_manager.ws.handler.impl;

import com.lmyxlf.websocket_manager.constants.ExceptionEnum;
import com.lmyxlf.websocket_manager.exception.CustomExceptions;
import com.lmyxlf.websocket_manager.ws.WsManager;
import com.lmyxlf.websocket_manager.ws.WsServerEndpoint;
import com.lmyxlf.websocket_manager.ws.WsStore;
import com.lmyxlf.websocket_manager.ws.cmd.WsBaseCmd;
import com.lmyxlf.websocket_manager.ws.cmd.WsCmdType;
import com.lmyxlf.websocket_manager.ws.cmd.impl.InitCmd;
import com.lmyxlf.websocket_manager.ws.handler.WsHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.Session;

/**
 * 客户端初始化登录
 */
@Slf4j
@Component
public class InitHandler implements WsHandler<InitCmd> {

    private static final WsCmdType CMD_TYPE = WsCmdType.INIT;

    private static final Integer MAX_CONNECT = 99;

    @PostConstruct
    public void init() {
        WsServerEndpoint.registerHandler(CMD_TYPE, this);
    }

    @Override
    public Object handle(InitCmd cmd, Session session, WsBaseCmd wsBaseCmd) {

        // 防止随机 clientId 进行初始化，设置最多连接数
        int size = WsManager.getAllActiveWebs().size();
        if (size > MAX_CONNECT) {
            log.error("活跃web:{}", String.join(",", WsManager.getAllActiveWebs()));
            throw new CustomExceptions(ExceptionEnum.MAX_CONNECT);
        }

        // 1. 查询
        String clientId = cmd.getClientId();
        if (clientId == null) {
            log.error("[初始化 web 连接],clientId为空,sessionId:[{}]", session.getId());
            throw new CustomExceptions(ExceptionEnum.BAD_REQUEST);
        }

        // 2. 踢掉旧的连接
        // 判断 imei 旧的连接是否存在，sessionId 不一致就踢出
        WsStore store = WsManager.getStore(clientId);
        if (store != null && !session.getId().equals(store.getSession().getId())) {
            log.info("[初始化 web 连接],踢出session,clientId:[{}],sessionId:[{}]", clientId, store.getSession().getId());
            WsManager.closeSession(store.getSession());
        }

        // 记录ws 信息
        WsManager.initSession(session, clientId);

//        WsManager.sendOK(session, wsBaseCmd, null);

        return null;
    }

    public static boolean AccessLegal(Session session, WsBaseCmd wsBaseCmd) {
        // 获取设备消息
        WsStore store = WsManager.getStoreBySessionId(session.getId());
        if (store == null) {
            WsManager.sendError(wsBaseCmd, session, new CustomExceptions(ExceptionEnum.DEVICE_NOT_ONLINE));
            WsManager.closeSession(session);
            return false;
        }
        return true;
    }


}