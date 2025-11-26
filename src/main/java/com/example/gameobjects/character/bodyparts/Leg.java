package com.example.gameobjects.character.bodyparts;

import com.example.gameobjects.character.Character.CharacterMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 腿 (Leg)
 */
public class Leg extends BodyPart {

    public enum LegState {
        STANDING,   // 站立
        STRIDING,   // 行走
        BENT        // 弯曲
    }

    private static final Map<LegState, PixelMap> configs = new HashMap<>();

    static {
        configs.put(LegState.STANDING, new PixelMap());
        configs.put(LegState.STRIDING, new PixelMap());
        configs.put(LegState.BENT, new PixelMap());
    }

    @Override
    public PixelMap getPixels(CharacterMode mode) {
        LegState state;
        switch (mode) {
            case WALKING:
            case DASHING:
                state = LegState.STRIDING;
                break;
            case HEALING:
            case STANDING:
            default:
                state = LegState.STANDING;
                break;
        }
        return configs.get(state);
    }
}
