package com.tenpai.backend.tenpai_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tenpai.backend.tenpai_backend.entity.UserGameRecord;
import com.tenpai.backend.tenpai_backend.mapper.UserGameRecordMapper;
import com.tenpai.backend.tenpai_backend.service.UserGameRecordService;
import org.springframework.stereotype.Service;

@Service
public class UserGameRecordServiceImpl extends ServiceImpl<UserGameRecordMapper, UserGameRecord> implements UserGameRecordService {
}
