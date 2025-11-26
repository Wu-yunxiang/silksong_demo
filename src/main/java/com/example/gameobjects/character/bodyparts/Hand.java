package com.example.gameobjects.character.bodyparts;

import com.example.gameobjects.character.Character.CharacterMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 手 (Hand)
 */
public class Hand extends BodyPart {

    public enum HandState {
        RELAXED,    // 放松
        SWINGING,   // 摆动 (行走时)
        FIST        // 握拳 (攻击/冲刺时)
    }

    private static final Map<HandState, PixelMap> configs = new HashMap<>();

    static {
        configs.put(HandState.RELAXED, new PixelMap());
        configs.put(HandState.SWINGING, new PixelMap());
        configs.put(HandState.FIST, new PixelMap());
    }

    @Override
    public PixelMap getPixels(CharacterMode mode) {
        HandState state;
        switch (mode) {
            case WALKING:
                state = HandState.SWINGING;
                break;
            case DASHING:
                state = HandState.FIST;
                break;
            case STANDING:
            case HEALING:
            default:
                state = HandState.RELAXED;
                break;
        }
        return configs.get(state);
    }
}
