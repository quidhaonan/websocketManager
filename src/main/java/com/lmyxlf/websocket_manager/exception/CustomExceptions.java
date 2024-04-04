package com.lmyxlf.websocket_manager.exception;

import com.lmyxlf.websocket_manager.constants.ExceptionEnum;
import com.lmyxlf.websocket_manager.response.ResponseEnum;
import com.lmyxlf.websocket_manager.response.ServerResponseEntity;
import lombok.Getter;

/**
 * 自定义异常
 */
@Getter
public class CustomExceptions extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -4137688758944857209L;

    /**
     * http状态码
     */
    private String code;

    private Object object;

    private ServerResponseEntity<?> serverResponseEntity;

    public CustomExceptions(ResponseEnum responseEnum) {
        super(responseEnum.getMsg());
        this.code = responseEnum.value();
    }

    /**
     * @param responseEnum
     */
    public CustomExceptions(ResponseEnum responseEnum, String msg) {
        super(msg);
        this.code = responseEnum.value();
    }

    public CustomExceptions(ServerResponseEntity<?> serverResponseEntity) {
        this.serverResponseEntity = serverResponseEntity;
    }


    public CustomExceptions(String msg) {
        super(msg);
        this.code = ResponseEnum.SHOW_FAIL.value();
    }

    public CustomExceptions(String msg, Object object) {
        super(msg);
        this.code = ResponseEnum.SHOW_FAIL.value();
        this.object = object;
    }

    public CustomExceptions(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMsg());
        this.code = exceptionEnum.getCode();
    }
}