package com.lmyxlf.websocket_manager.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.lmyxlf.websocket_manager.constants.ExceptionEnum;
import com.lmyxlf.websocket_manager.exception.CustomExceptions;
import com.lmyxlf.websocket_manager.model.Req.ReqDecrypt;
import com.lmyxlf.websocket_manager.service.DecryptService;
import com.lmyxlf.websocket_manager.ws.WsManager;
import com.lmyxlf.websocket_manager.ws.WsStore;
import com.lmyxlf.websocket_manager.ws.cmd.WsBaseCmd;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * @author ramm
 * @since 2024/5/2 23:24
 */
@Slf4j
@Service
public class DecryptServiceImpl implements DecryptService {

    // msgId --> userId
    public static final LoadingCache<String, String> sendByClientIdMap = CacheBuilder.newBuilder()
            .maximumSize(200)
            .expireAfterAccess(Duration.ofMillis((int) (1000 * 60 * 3 * 2.5)))
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(@NotNull String key) {
                    return null;
                }
            });
    // msgId --> 密文
    public static final LoadingCache<String, String> msgIdCiphertextMap = CacheBuilder.newBuilder()
            .maximumSize(200)
            .expireAfterAccess(Duration.ofMillis((int) (1000 * 60 * 3 * 2.5)))
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(@NotNull String key) {
                    return null;
                }
            });
    // msgId --> CountDownLatch
    public static final LoadingCache<String, CountDownLatch> msgIdCountDownLatchMap = CacheBuilder.newBuilder()
            .maximumSize(200)
            .expireAfterAccess(Duration.ofMillis((int) (1000 * 60 * 3 * 2.5)))
            .build(new CacheLoader<String, CountDownLatch>() {
                @Override
                public CountDownLatch load(@NotNull String key) {
                    return null;
                }
            });

    @Override
    public String decrypt(ReqDecrypt reqDecrypt) {

        String clientId = reqDecrypt.getClientId();
        WsStore wsStore = WsManager.webStores.getIfPresent(clientId);
        if (ObjectUtil.isNull(wsStore)) {
            log.error("[浏览器解密]，浏览器已下线");
            throw new CustomExceptions(ExceptionEnum.BROWSER_OFFLINE);
        }

        if (reqDecrypt.getFlag()) {
            // 浏览器将加密后的密文发送过来
            String msgId = reqDecrypt.getMsgId();
            String userId = sendByClientIdMap.getIfPresent(msgId);
            if (StrUtil.isBlank(userId)) {
                log.error("[浏览器解密]，无 userId ，发送失败");
                throw new CustomExceptions(ExceptionEnum.NO_USER_ID);
            }

            // 返回数据前清除 map
            sendByClientIdMap.invalidate(msgId);
            return reqDecrypt.getMsg();
        } else {
            UUID uuid = UUID.randomUUID();
            String msgId = uuid.toString();
            // 存放消息 id，记录应该发给谁
            sendByClientIdMap.put(uuid.toString(), reqDecrypt.getUserId());
            reqDecrypt.setMsgId(msgId);
            // 程序请求注册的浏览器 ----> 携带明文
            WsManager.sendOK(wsStore.getSession(), new WsBaseCmd(), reqDecrypt);

            // 暂停，获得结果后返回
            CountDownLatch countDownLatch = new CountDownLatch(1);
            try {
                msgIdCountDownLatchMap.put(msgId, countDownLatch);
                countDownLatch.await();
                String result = msgIdCiphertextMap.getIfPresent(msgId);
                if (StrUtil.isBlank(result)) {
                    log.error("结果为空，reqDecrypt：{}", reqDecrypt);
                }
                return result;
            } catch (InterruptedException e) {
                log.error("暂停失败，reqDecrypt：{}", reqDecrypt);
                e.printStackTrace();
            }
        }
        return null;
    }
}
