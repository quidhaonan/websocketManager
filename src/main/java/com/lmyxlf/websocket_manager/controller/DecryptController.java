package com.lmyxlf.websocket_manager.controller;

import com.lmyxlf.websocket_manager.model.Req.ReqDecrypt;
import com.lmyxlf.websocket_manager.response.ServerResponseEntity;
import com.lmyxlf.websocket_manager.service.DecryptService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 通过浏览器加密明文
 *
 * @author ramm
 * @since 2024/5/2 23:12
 */
@RestController
@RequestMapping("/ws")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DecryptController {

    private final DecryptService decryptService;

    @PostMapping("/decrypt")
    public ServerResponseEntity<String> decrypt(@RequestBody @Validated ReqDecrypt reqDecrypt) {

        return ServerResponseEntity.success(decryptService.decrypt(reqDecrypt));
    }
}
