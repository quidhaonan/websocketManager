package com.lmyxlf.websocket_manager.ws.handler.impl;

import cn.hutool.core.util.ObjectUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.lmyxlf.websocket_manager.constants.ExceptionEnum;
import com.lmyxlf.websocket_manager.exception.CustomExceptions;
import com.lmyxlf.websocket_manager.ws.WsManager;
import com.lmyxlf.websocket_manager.ws.WsServerEndpoint;
import com.lmyxlf.websocket_manager.ws.WsStore;
import com.lmyxlf.websocket_manager.ws.cmd.WsBaseCmd;
import com.lmyxlf.websocket_manager.ws.cmd.WsCmdType;
import com.lmyxlf.websocket_manager.ws.cmd.impl.DecryptCmd;
import com.lmyxlf.websocket_manager.ws.handler.WsHandler;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.Session;
import java.time.Duration;
import java.util.UUID;

/**
 * 通过浏览器加密明文
 */
@Slf4j
@Component
public class DecryptHandler implements WsHandler<DecryptCmd> {

    private static final WsCmdType CMD_TYPE = WsCmdType.DECRYPT;

    // clientId --> List<Session>
    public static final LoadingCache<String, WsStore> sendByClientIdMap = CacheBuilder.newBuilder()
            .maximumSize(200)
            .expireAfterAccess(Duration.ofMillis((int) (1000 * 60 * 3 * 2.5)))
            .build(new CacheLoader<String, WsStore>() {
                @Override
                public WsStore load(@NotNull String key) {
                    return null;
                }
            });

    @PostConstruct
    public void init() {
        WsServerEndpoint.registerHandler(CMD_TYPE, this);
    }

    @Override
    public Object handle(DecryptCmd cmd, Session session, WsBaseCmd wsWebBaseCmd) {

        String clientId = cmd.getClientId();
        WsStore wsStore = WsManager.webStores.getIfPresent(clientId);
        if (ObjectUtil.isNull(wsStore)) {
            log.error("[浏览器解密]，浏览器已下线");
            throw new CustomExceptions(ExceptionEnum.BROWSER_OFFLINE);
        }

        // 浏览器将加密后的密文发送过来
        if (cmd.getFlag()) {
            WsStore store = sendByClientIdMap.getIfPresent(wsWebBaseCmd.getMsgId());
            if (ObjectUtil.isNull(store)) {
                log.error("[浏览器解密]，无 msgId ，发送失败");
                throw new CustomExceptions(ExceptionEnum.BROWSER_OFFLINE);
            }

            WsManager.sendOK(store.getSession(), wsWebBaseCmd, cmd.getMsg());
            sendByClientIdMap.invalidate(wsWebBaseCmd.getMsgId());
        } else {
            UUID uuid = UUID.randomUUID();
            sendByClientIdMap.put(uuid.toString(),new WsStore().setSession(session).setClientId(clientId));
            wsWebBaseCmd.setMsgId(uuid.toString());
            // 程序请求注册的浏览器 ----> 携带明文
            WsManager.sendOK(wsStore.getSession(), wsWebBaseCmd, cmd.getMsg());
        }

        return null;
    }
}
