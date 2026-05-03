package com.tenpai.backend.tenpai_backend.controller;

import com.tenpai.backend.tenpai_backend.common.R;
import com.tenpai.backend.tenpai_backend.entity.GameRoom;
import com.tenpai.backend.tenpai_backend.service.GameRoomService;
import com.tenpai.backend.tenpai_backend.service.WeChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 房间管理 Controller
 */
@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class GameRoomController {

    private final GameRoomService gameRoomService;
    
    @Autowired
    private WeChatService weChatService;

    /**
     * 创建房间
     * 请求体: { "ownerId": 1, "rules": "{\"baseScore\":1}", "scoreMode": 1 }
     */
    @PostMapping("/create")
    public R<GameRoom> create(@RequestBody Map<String, Object> params) {
        Long ownerId = Long.valueOf(params.get("ownerId").toString());
        String rules = params.get("rules") != null ? params.get("rules").toString() : null;
        Integer scoreMode = params.containsKey("scoreMode") ? Integer.valueOf(params.get("scoreMode").toString()) : 0;

        GameRoom room = gameRoomService.createRoom(ownerId, rules, scoreMode);
        return R.ok(room);
    }

    /**
     * 加入房间
     * 请求体: { "roomCode": "1234" }
     */
    @PostMapping("/join")
    public R<GameRoom> join(@RequestBody Map<String, String> params) {
        String roomCode = params.get("roomCode");
        GameRoom room = gameRoomService.joinRoom(roomCode);
        return R.ok(room);
    }

    /**
     * 结束房间
     * 请求体: { "roomId": 1 }
     */
    @PostMapping("/end")
    public R<Void> end(@RequestBody Map<String, Object> params) {
        Long roomId = Long.valueOf(params.get("roomId").toString());
        gameRoomService.endRoom(roomId);
        return R.ok();
    }

    /**
     * 更新房间信息(如玩家列表)
     * 请求体: { "id": 1, "players": "[...]" }
     */
    @PostMapping("/update")
    public R<Void> update(@RequestBody GameRoom room) {
        gameRoomService.updateRoom(room);
        return R.ok();
    }

    /**
     * 获取用户房间列表（须写在 /{roomCode} 之前，避免 list 被当作房间号）
     */
    @GetMapping("/list")
    public R<java.util.List<GameRoom>> list(@RequestParam Long userId) {
        java.util.List<GameRoom> list = gameRoomService.listRooms(userId);
        return R.ok(list);
    }

    /**
     * 获取当前用户一个进行中的房间（房主或玩家），用于创建冲突时跳转
     */
    @GetMapping("/active")
    public R<GameRoom> activeRoom(@RequestParam Long userId) {
        GameRoom room = gameRoomService.getActiveRoomForUser(userId);
        if (room == null) {
            return R.fail(404, "没有进行中的房间");
        }
        return R.ok(room);
    }

    /**
     * 获取小程序码（须写在 /{roomCode} 之前）
     */
    @GetMapping("/qrcode")
    public R<String> getRoomQRCode(@RequestParam(required = false) String code,
                                   @RequestParam(required = false) String uuid) {
        // 兼容旧参数 uuid，新版本使用 code
        String scene = (code != null && !code.isEmpty()) ? code : uuid;
        if (scene == null || scene.isEmpty()) {
            return R.fail("Missing room code");
        }
        String imageBase64 = weChatService.getUnlimitedQRCode(scene, "pages/room/room");
        if (imageBase64 == null) {
            return R.fail("Failed to generate QR code");
        }
        return R.ok(imageBase64);
    }

    /**
     * 获取房间详情 (包含完整记录)
     */
    @GetMapping("/{roomCode}")
    public R<Map<String, Object>> detail(@PathVariable String roomCode) {
        GameRoom room = gameRoomService.getRoomByCode(roomCode);
        
        // Fetch records via service
        java.util.List<com.tenpai.backend.tenpai_backend.entity.GameRecord> records = gameRoomService.getRoomRecords(room.getId());
        
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("room", room);
        data.put("records", records);
        
        return R.ok(data);
    }
}
