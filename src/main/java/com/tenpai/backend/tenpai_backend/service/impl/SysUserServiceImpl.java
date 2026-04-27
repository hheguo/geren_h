package com.tenpai.backend.tenpai_backend.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tenpai.backend.tenpai_backend.entity.SysUser;
import com.tenpai.backend.tenpai_backend.mapper.SysUserMapper;
import com.tenpai.backend.tenpai_backend.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户 Service 实现
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserMapper sysUserMapper;

    @Override
    public SysUser wxLogin(String openid, String nickname, String avatarUrl) {
        if (StrUtil.isBlank(openid)) {
            throw new RuntimeException("OpenID cannot be empty");
        }

        // Must use 'selectByOpenid' or wrapper to find existing user
        SysUser user = sysUserMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getOpenid, openid)
                .last("LIMIT 1"));

        if (user == null) {
            // Register new user
            user = new SysUser();
            user.setOpenid(openid);
            user.setNickname(StrUtil.isNotBlank(nickname) ? nickname : "玩家" + openid.substring(openid.length() - 4));
            user.setAvatarUrl(avatarUrl); // Can be null/empty, frontend handles it
            user.setCreateTime(java.time.LocalDateTime.now());
            sysUserMapper.insert(user);
        } else {
            // Update existing user
            boolean changed = false;
            // Update nickname if provided and different
            if (StrUtil.isNotBlank(nickname) && !nickname.equals(user.getNickname())) {
                user.setNickname(nickname);
                changed = true;
            }
            // Update avatar if provided and different
            if (StrUtil.isNotBlank(avatarUrl) && !avatarUrl.equals(user.getAvatarUrl())) {
                user.setAvatarUrl(avatarUrl);
                changed = true;
            }

            if (changed) {
                sysUserMapper.updateById(user);
            }
        }
        return user;
    }



    @Override
    public SysUser getUserInfo(Long id) {
        return sysUserMapper.selectById(id);
    }
}
