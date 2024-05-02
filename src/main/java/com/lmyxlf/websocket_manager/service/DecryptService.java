package com.lmyxlf.websocket_manager.service;

import com.lmyxlf.websocket_manager.model.Req.ReqDecrypt;

/**
 * @author ramm
 * @since 2024/5/2 23:24
 */
public interface DecryptService {

    String decrypt(ReqDecrypt reqDecrypt);
}
