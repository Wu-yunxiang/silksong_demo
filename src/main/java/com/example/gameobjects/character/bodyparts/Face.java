package com.example.gameobjects.character.bodyparts;

import com.example.gameobjects.character.Character.CharacterMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 脸部 (Face)
 */
public class Face extends BodyPart {

    public enum FaceState {
        NORMAL,     // 正常
        ANGRY,      // 愤怒 (攻击时)
        PAIN        // 痛苦 (受伤时)
    }

    private static final Map<FaceState, PixelMap> configs = new HashMap<>();

    static {
        configs.put(FaceState.NORMAL, new PixelMap());
        configs.put(FaceState.ANGRY, new PixelMap());
        configs.put(FaceState.PAIN, new PixelMap());
    }

    @Override
    public PixelMap getPixels(CharacterMode mode) {
        FaceState state;
        switch (mode) {
            case DASHING: // 假设冲刺时表情严肃/愤怒
                state = FaceState.ANGRY;
                break;
            case HEALING:
            case STANDING:
            case WALKING:
            default:
                state = FaceState.NORMAL;
                break;
        }
        return configs.get(state);
    }
}
