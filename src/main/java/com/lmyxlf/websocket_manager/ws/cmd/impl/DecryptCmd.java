package com.lmyxlf.websocket_manager.ws.cmd.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lmyxlf.websocket_manager.ws.cmd.WsCmd;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * 通过浏览器加密明文 请求体
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DecryptCmd implements WsCmd {

    /**
     * clientId
     */
    @NotBlank(message = "clientId不能为空")
    private String clientId;

    /**
     * 明文密文
     */
    @NotBlank(message = "明文不能为空")
    private String msg;

    /**
     * false：程序请求注册的浏览器 ----> 携带明文
     * true：浏览器将加密后的密文发送过来
     */
    private Boolean flag = false;
}
