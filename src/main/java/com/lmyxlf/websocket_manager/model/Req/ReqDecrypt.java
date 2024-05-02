package com.lmyxlf.websocket_manager.model.Req;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @author ramm
 * @since 2024/5/2 23:49
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqDecrypt {

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

    private String msgId;

    /**
     * https 请求识别发给谁
     */
    private String userId;
}
