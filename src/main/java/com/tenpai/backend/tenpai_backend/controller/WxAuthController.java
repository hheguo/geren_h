package com.tenpai.backend.tenpai_backend.controller;

import com.tenpai.backend.tenpai_backend.common.R;
import com.tenpai.backend.tenpai_backend.entity.SysUser;
import com.tenpai.backend.tenpai_backend.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 微信认证 Controller
 */
@RestController
@RequestMapping("/api/wx")
@RequiredArgsConstructor
public class WxAuthController {

    private final SysUserService sysUserService;
    private final com.tenpai.backend.tenpai_backend.service.WeChatService weChatService;

    /**
     * 微信登录
     * 请求体: { "code": "js_code", "nickname": "昵称", "avatarUrl": "头像URL(可选)" }
     */
    @PostMapping("/login")
    public R<SysUser> login(@RequestBody Map<String, String> params) {
        String code = params.get("code");
        String nickname = params.get("nickname");
        String avatarUrl = params.get("avatarUrl");

        if (code == null) {
             return R.fail("Missing code");
        }

        // 1. Get OpenID from WeChat
        cn.hutool.json.JSONObject session = weChatService.code2Session(code);
        String openid = session.getStr("openid");

        if (openid == null) {
            return R.fail("Failed to get openid from WeChat");
        }

        // 2. Login/Register with OpenID
        SysUser user = sysUserService.wxLogin(openid, nickname, avatarUrl);
        return R.ok(user);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/user/{id}")
    public R<SysUser> getUserInfo(@PathVariable Long id) {
        SysUser user = sysUserService.getUserInfo(id);
        if (user == null) {
            return R.fail("用户不存在");
        }
        return R.ok(user);
    }
}
