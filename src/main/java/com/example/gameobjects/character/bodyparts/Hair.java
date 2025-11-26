package com.example.gameobjects.character.bodyparts;

import com.example.gameobjects.character.Character.CharacterMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 头发 (Hair)
 */
public class Hair extends BodyPart {

    /**
     * 头发特有的状态枚举
     */
    public enum HairState {
        IDLE,       // 静止
        MOVING,     // 飘动
        MESSY       // 凌乱 (例如受伤时)
    }

    // 静态配置：所有 Hair 实例共享同一份像素数据配置
    private static final Map<HairState, PixelMap> configs = new HashMap<>();

    static {
        // 初始化配置 (此处仅为示例，实际数据需填充)
        configs.put(HairState.IDLE, new PixelMap());
        configs.put(HairState.MOVING, new PixelMap());
        configs.put(HairState.MESSY, new PixelMap());
    }

    @Override
    public PixelMap getPixels(CharacterMode mode) {
        // 将通用的 CharacterMode 映射到具体的 HairState
        HairState state;
        switch (mode) {
            case WALKING:
            case DASHING:
                state = HairState.MOVING;
                break;
            case HEALING: // 假设治疗时头发静止
            case STANDING:
            default:
                state = HairState.IDLE;
                break;
        }
        return configs.get(state);
    }
}
