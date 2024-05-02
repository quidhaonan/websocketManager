package com.lmyxlf.websocket_manager.ws.handler.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.lmyxlf.websocket_manager.constants.ExceptionEnum;
import com.lmyxlf.websocket_manager.exception.CustomExceptions;
import com.lmyxlf.websocket_manager.service.impl.DecryptServiceImpl;
import com.lmyxlf.websocket_manager.ws.WsManager;
import com.lmyxlf.websocket_manager.ws.WsServerEndpoint;
import com.lmyxlf.websocket_manager.ws.WsStore;
import com.lmyxlf.websocket_manager.ws.cmd.WsBaseCmd;
import com.lmyxlf.websocket_manager.ws.cmd.WsCmdType;
import com.lmyxlf.websocket_manager.ws.cmd.impl.DecryptCmd;
import com.lmyxlf.websocket_manager.ws.handler.WsHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.Session;
import java.util.concurrent.CountDownLatch;

/**
 * 通过浏览器加密明文，https 格式
 */
@Slf4j
@Component
public class DecryptHttpsHandler implements WsHandler<DecryptCmd> {

    private static final WsCmdType CMD_TYPE = WsCmdType.DECRYPT_HTTPS;


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
            String msgId = wsWebBaseCmd.getMsgId();
            if (StrUtil.isBlank(msgId)) {
                log.error("无 msgId，cmd：{}，wsWebBaseCmd：{}", cmd, wsWebBaseCmd);
                throw new CustomExceptions(ExceptionEnum.NO_MSG_ID);
            }
            DecryptServiceImpl.msgIdCiphertextMap.put(msgId, cmd.getMsg());

            CountDownLatch countDownLatch = DecryptServiceImpl.msgIdCountDownLatchMap.getIfPresent(msgId);
            if (ObjectUtil.isNull(countDownLatch)) {
                log.error("无对应 countDownLatch，cmd：{}，wsWebBaseCmd：{}", cmd, wsWebBaseCmd);
                throw new CustomExceptions(ExceptionEnum.BAD_REQUEST);
            }
            countDownLatch.countDown();
        }

        return null;
    }
}
