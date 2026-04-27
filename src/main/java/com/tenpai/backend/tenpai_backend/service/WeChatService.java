package com.tenpai.backend.tenpai_backend.service;

public interface WeChatService {
    String getAccessToken();
    String getUnlimitedQRCode(String scene, String page);
    cn.hutool.json.JSONObject code2Session(String jsCode);
}
