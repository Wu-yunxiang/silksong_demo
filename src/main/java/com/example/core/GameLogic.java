package com.example.core;

import com.example.input.InputManager;
import com.example.gameobjects.character.Character;
import java.util.List;


/**
 * 游戏逻辑
 * 代表单轮的处理和更新流程
 */
public class GameLogic {
    // 单轮处理：处理输入 -> 更新逻辑 -> 检查判定
    public static void processFrame(float deltaTime,Character character) {
        // 处理输入
        processInput(character);

        // 更新游戏逻辑
        updateGameLogic(deltaTime);

        // 检查游戏判定
        checkGameConditions();
    }
    private static void processInput(Character character) {
        InputManager inputManager = InputManager.getInstance();
        inputManager.pollEvents();
        List<CharacterBehaviorEvent> events = InputEventGenerator.generate(inputManager, character);
        
    }
    private static void updateGameLogic(float deltaTime) {
        // 更新游戏对象状态
    }
    private static void checkGameConditions() {
        // 检查游戏胜负等条件
    }
}
