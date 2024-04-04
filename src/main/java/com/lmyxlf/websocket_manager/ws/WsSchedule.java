package com.lmyxlf.websocket_manager.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * websocket 监控定时任务
 */
@Slf4j
@Component
public class WsSchedule {


    /**
     * 活跃设备监测
     */
    @Scheduled(fixedRate = 1000 * 20)
    public void checkWsActive() {
        WsManager.buildActiveCache();
        int size = WsManager.getAllActiveWebs().size();
        if (size > 0) {
            log.info("活跃web:{}", String.join(",", WsManager.getAllActiveWebs()));
        }

    }

    /**
     * 活跃 session 连接
     */
    @Scheduled(fixedRate = 1000 * 60)
    public void checkConnectedSession() {
        Set<String> sessionIds = WsManager.checkConnectedSession();
        if (!sessionIds.isEmpty()) {
            log.info("ws 连接:{}", String.join(",", sessionIds));
        }

        long size = WsManager.getAllWebStores().size();
        if (size > 0) {
            log.info("clientId和session关联缓存大小是:{}", size);
        }

    }
}