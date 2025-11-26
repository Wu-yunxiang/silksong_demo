package com.example.gameobjects.character.bodyparts;

import com.example.gameobjects.character.Character.CharacterMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 脚 (Foot)
 */
public class Foot extends BodyPart {

    public enum FootState {
        FLAT,       // 平放
        TIPI_TOE,   // 踮脚 (冲刺/跳跃)
        LIFTED      // 抬起
    }

    private static final Map<FootState, PixelMap> configs = new HashMap<>();

    static {
        configs.put(FootState.FLAT, new PixelMap());
        configs.put(FootState.TIPI_TOE, new PixelMap());
        configs.put(FootState.LIFTED, new PixelMap());
    }

    @Override
    public PixelMap getPixels(CharacterMode mode) {
        FootState state;
        switch (mode) {
            case DASHING:
                state = FootState.TIPI_TOE;
                break;
            case WALKING:
                state = FootState.LIFTED; // 简化处理，行走时交替抬起，此处仅示例
                break;
            case STANDING:
            case HEALING:
            default:
                state = FootState.FLAT;
                break;
        }
        return configs.get(state);
    }
}
