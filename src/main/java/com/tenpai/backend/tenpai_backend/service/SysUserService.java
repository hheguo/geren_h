package com.tenpai.backend.tenpai_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tenpai.backend.tenpai_backend.entity.SysUser;

/**
 * 用户 Service
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 微信登录（通过 openid 查找或创建用户）
     *
     * @param openid  微信openid
     * @param nickname 昵称
     * @param avatarUrl 头像
     * @return 用户信息
     */
    SysUser wxLogin(String openid, String nickname, String avatarUrl);

    /**
     * 根据ID获取用户信息
     */
    SysUser getUserInfo(Long id);
}
