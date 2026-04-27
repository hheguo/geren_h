package com.tenpai.backend.tenpai_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tenpai.backend.tenpai_backend.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户 Mapper
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据微信 openid 查询用户
     */
    @Select("SELECT * FROM sys_user WHERE openid = #{openid}")
    SysUser selectByOpenid(String openid);
}
