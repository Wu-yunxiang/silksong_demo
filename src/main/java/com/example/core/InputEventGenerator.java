package com.example.core;

import com.example.input.InputManager;
import com.example.gameobjects.character.Character;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;

/**
 * 负责根据 InputManager 的状态生成 CharacterBehaviorEvent 列表
 */
public class InputEventGenerator {

    private InputEventGenerator() {
        // 私有构造函数，防止实例化
    }

    public static List<CharacterBehaviorEvent> generate(InputManager input, Character character) {
        List<CharacterBehaviorEvent> events = new ArrayList<>();

        // --- 移动 (AD) ---
        if (input.isKeyPressed(GLFW.GLFW_KEY_A)) events.add(CharacterBehaviorEvent.MOVE_LEFT);
        if (input.isKeyPressed(GLFW.GLFW_KEY_D)) events.add(CharacterBehaviorEvent.MOVE_RIGHT);

        // --- 跳跃 (K) ---
        if (input.isKeyJustPressed(GLFW.GLFW_KEY_K)) {
            if (character.isOnGround()) {
                events.add(CharacterBehaviorEvent.JUMP);
            } else if (character.getRemainingAirJumps() > 0) {
                events.add(CharacterBehaviorEvent.DOUBLE_JUMP);
            }
        }

        // --- 冲刺 (L) ---
        if (input.isKeyJustPressed(GLFW.GLFW_KEY_L)) {
            events.add(CharacterBehaviorEvent.DASH);
        }

        // --- 攻击 (J) ---
        if (input.isKeyJustPressed(GLFW.GLFW_KEY_J)) {
            if (input.isKeyPressed(GLFW.GLFW_KEY_W)) {
                events.add(CharacterBehaviorEvent.ATTACK_UP);
            } else if (input.isKeyPressed(GLFW.GLFW_KEY_S)) {
                events.add(CharacterBehaviorEvent.ATTACK_DOWN);
            } else {
                events.add(CharacterBehaviorEvent.ATTACK_NORMAL);
            }
        }

        // --- 技能/回血 (Q) ---,先都视为技能
        if (input.isKeyJustPressed(GLFW.GLFW_KEY_Q)) {
            events.add(CharacterBehaviorEvent.CAST_SKILL);
        }

        return events;
    }
}
