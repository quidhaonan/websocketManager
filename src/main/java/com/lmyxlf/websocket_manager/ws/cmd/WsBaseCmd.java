package com.lmyxlf.websocket_manager.ws.cmd;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ws 基础类型
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WsBaseCmd {

    private String msgId;

    private String type;

    private Object data;

    private String err;

    private Integer code;

    public WsBaseCmd setType(String type) {
        this.type = StrUtil.trimToNull(type);
        return this;
    }

    public WsBaseCmd setMsgId(String msgId) {
        this.msgId = StrUtil.trimToNull(msgId);
        return this;
    }
}