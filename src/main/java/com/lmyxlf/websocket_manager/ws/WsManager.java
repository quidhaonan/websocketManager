package com.lmyxlf.websocket_manager.ws;

import cn.hutool.json.JSONUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.lmyxlf.websocket_manager.constants.ExceptionEnum;
import com.lmyxlf.websocket_manager.exception.CustomExceptions;
import com.lmyxlf.websocket_manager.ws.cmd.WsBaseCmd;
import com.lmyxlf.websocket_manager.ws.cmd.WsCmdType;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

import javax.websocket.Session;
import java.io.IOException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * websocket 处理工具类
 */
@Slf4j
public class WsManager {


    // ws 空闲时间， 毫秒
    public static final int wsExpire = 1000 * 60 * 3;

    // ws session 所使用内存的过期时间， 客户端发心跳，内存续期
    private static final int cacheExpire = (int) (wsExpire * 2.5);

    // ws session 在openSession 后没有进入活跃状态会被关闭
    private static final int dangerDuration = 1000 * 10 * 3;

    private static final int cacheSize = 200;

    // ws 所有session, 如果session 不在活跃列表里，并且超过 dangerDuration 就会被断开
    private static final Map<Session, Long> connectedSessions = new ConcurrentHashMap<>();

    // clientId --> store(session)
    public static final LoadingCache<String, WsStore> webStores = CacheBuilder.newBuilder()
            .maximumSize(cacheSize)
            .expireAfterAccess(Duration.ofMillis(cacheExpire))
            .build(new CacheLoader<String, WsStore>() {
                @Override
                public WsStore load(@NotNull String key) {
                    return null;
                }
            });

    // sessionId --> clientId
    private static final LoadingCache<String, String> webSessions = CacheBuilder.newBuilder()
            .maximumSize(cacheSize)
            .expireAfterAccess(Duration.ofMillis(cacheExpire))
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(@NotNull String key) {
                    return null;
                }
            });

    private static Set<String> activeWebs = new HashSet<>();

    /**
     * 心跳内存续期
     *
     * @param session session
     * @return session是否存在
     */
    public static boolean renewCache(Session session) {
        String sessionId = session.getId();
        String imei = webSessions.getIfPresent(sessionId);
        if (imei == null) {
            return false;
        }

        webStores.getIfPresent(imei);
        return true;
    }

    public static Set<String> getAllActiveWebs() {
        return activeWebs;
    }

    public static LoadingCache<String, WsStore> getAllWebStores() {
        return webStores;
    }

    public static void buildActiveCache() {
        activeWebs = new HashSet<>(webSessions.asMap().values());
    }

    /**
     * 检查 session 是否活跃
     */
    public static Set<String> checkConnectedSession() {

        long now = System.currentTimeMillis();

        Set<String> activeSessions = new HashSet<>(webSessions.asMap().keySet());

        for (Map.Entry<Session, Long> item : connectedSessions.entrySet()) {

            Session session = item.getKey();
            Long openTime = item.getValue();

            // 活跃连接放行
            String sessionId = session.getId();
            if (activeSessions.contains(sessionId)) {
                continue;
            }

            if (now - openTime >= dangerDuration) {
                closeSession(session);
                connectedSessions.remove(session);
                log.info("[检查ws连接],关闭连接超过 dangerDuration：{} 的session, sessionId:[{}]，断开连接", dangerDuration, session.getId());
            } else {
                log.debug("[检查ws连接],连接存续小于 dangerDuration ：{}的session, sessionId:[{}],重新维护连接", dangerDuration, session.getId());
                connectedSessions.put(session, System.currentTimeMillis());
            }
        }

        return connectedSessions.keySet().stream().map(Session::getId).collect(Collectors.toSet());
    }

    public static WsStore getStore(String clientId) {
        return webStores.getIfPresent(clientId);
    }

    public static WsStore getStoreBySessionId(String sessionId) {

        String clientId = webSessions.getIfPresent(sessionId);
        if (clientId == null) {
            return null;
        }

        return getStore(clientId);
    }

    public static String getClientIdBySessionId(String sessionId) {

        String clientId = webSessions.getIfPresent(sessionId);
        return clientId;
    }

    public static String getImei(String sessionId) {
        String imei = webSessions.getIfPresent(sessionId);

        return imei;
    }

    public static boolean isOnline(String imei) {
        return activeWebs.contains(imei);
    }

    public static void openSession(Session session) {
        session.setMaxIdleTimeout(WsManager.wsExpire);
        session.setMaxTextMessageBufferSize(5 * 1024 * 1024);
        session.setMaxBinaryMessageBufferSize(1024 * 1024);

        connectedSessions.put(session, System.currentTimeMillis());
        log.info("[开启 ws web 连接],sessionId:[{}]", session.getId());
    }

    public static void initSession(Session session, String clientId) {

        WsStore wsStore = webStores.getIfPresent(clientId);
        if (wsStore != null) {
            return;
        }

        webSessions.put(session.getId(), clientId);
        webStores.put(clientId, new WsStore().setClientId(clientId).setSession(session));
        activeWebs.add(clientId);
    }

    public static void closeSession(Session session) {

        if (session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                log.error("[关闭 web session], e:[{}]", e.getMessage());
            }
        }

        String imei = webSessions.getIfPresent(session.getId());
        if (imei != null) {
            webStores.invalidate(imei);
            webSessions.invalidate(session.getId());
        }

        activeWebs.remove(imei);
        if (connectedSessions.remove(session) != null) {
            log.info("[关闭 web session],sessionId:[{}],imei:[{}]", session.getId(), imei);
        }
    }

    public static void sendError(WsBaseCmd wsWebBaseCmd, Session session, Exception exception) {

//        ResResult resResult = ExceptionUtil.getExceptionResponse(exception);

//        WsBaseCmd res = new WsBaseCmd().setMsgId(wsWebBaseCmd.getMsgId()).setType(wsWebBaseCmd.getType()).setCode(resResult.getCode()).setErr(resResult.getMsg());
        WsBaseCmd res = new WsBaseCmd()
                .setMsgId(wsWebBaseCmd.getMsgId())
                .setType(wsWebBaseCmd.getType())
                .setCode(Integer.parseInt(ExceptionEnum.BAD_REQUEST.getCode()))
                .setErr(ExceptionEnum.BAD_REQUEST.getMsg());

        sendText(session, res);
    }

    public static void sendCmdByClientId(String clientId, WsBaseCmd wsWebBaseCmd) {

        WsStore store = webStores.getIfPresent(clientId);
        if (store == null) {
            log.error("[发送设备指令],设备不在线,clientId:[{}],cmd:[{}]", clientId, wsWebBaseCmd);
            throw new CustomExceptions(ExceptionEnum.DEVICE_NOT_ONLINE);
        }

        sendText(store.getSession(), wsWebBaseCmd);
    }

    public static void sendOK(Session session, WsBaseCmd wsBaseCmd, Object data) {

        WsBaseCmd res = new WsBaseCmd().setMsgId(wsBaseCmd.getMsgId())
                .setType(wsBaseCmd.getType())
                .setCode(HttpStatus.OK.value())
                .setData(data);
        sendText(session, res);
    }

    public static void sendOK(Session session, WsCmdType type, Object data) {

        if (!type.isRequest()) {
            return;
        }

        WsBaseCmd res = new WsBaseCmd().setCode(HttpStatus.OK.value()).setData(data)
                .setType(type.getType());

        sendText(session, res);
    }

    public static void sendText(Session session, Object data) {

        try {
            if (session == null) {
                return;
            }

            if (session.isOpen()) {
                synchronized (session.getId().intern()) {
//                    session.getBasicRemote().sendText(ObjectUtils.writeValueAsString(data));
                    session.getBasicRemote().sendText(JSONUtil.toJsonStr(data));
                }
            } else {
                // 关闭连接
                log.warn("[发送信息],连接未打开,sessionId:[{}]", session.getId());
                closeSession(session);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("[发送信息]异常,sessionId:[{}]", session.getId());
        }
    }


    public static void updateUserCmd(String sessionId, WsCmdType type) {
        String clientId = webSessions.getIfPresent(sessionId);

        if (clientId == null) {
            return;
        }

        WsStore wsStore = webStores.getIfPresent(clientId);
        if (wsStore != null) {
            wsStore.setLastCmdType(type);
        }
    }

    /**
     * 判断页面当前所处位置 的参数
     *
     * @param clientId
     * @param type
     * @return
     * @throws ExecutionException
     */
    public static boolean isNowClientCmd(String clientId, WsCmdType type) throws ExecutionException {

        WsStore wsStore = webStores.getIfPresent(clientId);
        if (wsStore != null) {
            WsCmdType lastCmdType = wsStore.getLastCmdType();
            return type == lastCmdType;
        }

        return false;
    }
}