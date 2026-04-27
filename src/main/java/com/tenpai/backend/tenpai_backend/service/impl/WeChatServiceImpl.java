package com.tenpai.backend.tenpai_backend.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tenpai.backend.tenpai_backend.service.WeChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WeChatServiceImpl implements WeChatService {

    @Value("${wechat.app-id}")
    private String appId;

    @Value("${wechat.app-secret}")
    private String appSecret;

    // Simple cache for token (not production ready for distributed mostly, but fine for single instance)
    private String accessToken;
    private long tokenExpirationTime = 0;

    @Override
    public synchronized String getAccessToken() {
        if (StrUtil.isNotEmpty(accessToken) && System.currentTimeMillis() < tokenExpirationTime) {
            return accessToken;
        }

        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;
        String response = HttpUtil.get(url);
        JSONObject json = JSONUtil.parseObj(response);

        if (json.containsKey("access_token")) {
            accessToken = json.getStr("access_token");
            int expiresIn = json.getInt("expires_in");
            // Refresh 5 minutes before expiry
            tokenExpirationTime = System.currentTimeMillis() + (expiresIn - 300) * 1000L;
            return accessToken;
        } else {
            log.error("Failed to get access token: {}", response);
            throw new RuntimeException("Failed to get WeChat access token");
        }
    }

    @Override
    public String getUnlimitedQRCode(String scene, String page) {
        String token = getAccessToken();
        String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + token;

        Map<String, Object> params = new HashMap<>();
        params.put("scene", scene);
        params.put("page", page);
        // check_path: false allows generating code for pages not yet published
        params.put("check_path", false);
        // Optional: formatting
        params.put("width", 430);
        params.put("auto_color", false);
        Map<String, Object> line_color = new HashMap<>();
        line_color.put("r", 0);
        line_color.put("g", 0);
        line_color.put("b", 0);
        params.put("line_color", line_color);

        byte[] imageBytes = cn.hutool.http.HttpRequest.post(url)
                .body(JSONUtil.toJsonStr(params))
                .execute()
                .bodyBytes();
        
        // Check if response is error JSON instead of image
        if (imageBytes.length < 1024) {
            String str = new String(imageBytes);
            if (str.startsWith("{") && str.contains("errcode")) {
                log.error("Failed to get QR code: {}", str);
                // Return null or throw error, treating as failure to generate
                return null;
            }
        }

        // Return Base64 string directly for frontend to display
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }

    @Override
    public JSONObject code2Session(String jsCode) {
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String, Object> params = new HashMap<>();
        params.put("appid", appId);
        params.put("secret", appSecret);
        params.put("js_code", jsCode);
        params.put("grant_type", "authorization_code");

        String response = HttpUtil.get(url, params);
        JSONObject json = JSONUtil.parseObj(response);
        
        if (json.containsKey("errcode") && json.getInt("errcode") != 0) {
            log.error("WeChat code2Session failed: {}", response);
            throw new RuntimeException("WeChat login failed: " + json.getStr("errmsg"));
        }
        return json;
    }
}
