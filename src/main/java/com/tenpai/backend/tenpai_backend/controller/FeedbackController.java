package com.tenpai.backend.tenpai_backend.controller;

import com.tenpai.backend.tenpai_backend.common.R;
import com.tenpai.backend.tenpai_backend.entity.Feedback;
import com.tenpai.backend.tenpai_backend.mapper.FeedbackMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 反馈 Controller
 */
@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackMapper feedbackMapper;

    /**
     * 提交反馈
     */
    @PostMapping("/submit")
    public R<Feedback> submit(@RequestBody Map<String, Object> params) {
        Feedback fb = new Feedback();

        if (params.get("userId") != null) {
            try {
                fb.setUserId(Long.valueOf(params.get("userId").toString()));
            } catch (NumberFormatException ignored) {}
        }

        fb.setType(params.getOrDefault("type", "other").toString());
        fb.setContent(params.getOrDefault("content", "").toString());
        fb.setContact(params.getOrDefault("contact", "").toString());

        feedbackMapper.insert(fb);
        return R.ok(fb);
    }
}
