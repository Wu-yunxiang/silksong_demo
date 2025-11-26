package com.example.gameobjects.character.bodyparts;

import com.example.gameobjects.character.Character.CharacterMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 身体 (Body)
 */
public class Body extends BodyPart {

    public enum BodyState {
        UPRIGHT,    // 直立
        LEANING,    // 倾斜 (奔跑时)
        CROUCHING   // 蹲伏/防御
    }

    private static final Map<BodyState, PixelMap> configs = new HashMap<>();

    static {
        configs.put(BodyState.UPRIGHT, new PixelMap());
        configs.put(BodyState.LEANING, new PixelMap());
        configs.put(BodyState.CROUCHING, new PixelMap());
    }

    @Override
    public PixelMap getPixels(CharacterMode mode) {
        BodyState state;
        switch (mode) {
            case DASHING:
            case WALKING:
                state = BodyState.LEANING;
                break;
            case HEALING:
            case STANDING:
            default:
                state = BodyState.UPRIGHT;
                break;
        }
        return configs.get(state);
    }
}
