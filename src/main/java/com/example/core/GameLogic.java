package com.example.core;

import com.example.input.InputManager;
import com.example.gameobjects.character.Character;
import com.example.gameobjects.character.Character.CharacterMode;
import com.example.gameobjects.character.Character.Facing;
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
        
        // 1. Determine Facing
        for (CharacterBehaviorEvent event : events) {
            if (event == CharacterBehaviorEvent.MOVE_LEFT) {
                character.setFacing(Facing.LEFT);
            } else if (event == CharacterBehaviorEvent.MOVE_RIGHT) {
                character.setFacing(Facing.RIGHT);
            }
        }

        // 2. Determine Character Mode
        CharacterMode newMode = CharacterMode.STANDING;
        
        boolean isMoving = events.contains(CharacterBehaviorEvent.MOVE_LEFT) || events.contains(CharacterBehaviorEvent.MOVE_RIGHT);
        if (isMoving) {
            newMode = CharacterMode.WALKING;
        }
        
        if (events.contains(CharacterBehaviorEvent.DASH)) {
            newMode = CharacterMode.DASHING;
        }
        
        if (events.contains(CharacterBehaviorEvent.HEAL)) {
            newMode = CharacterMode.HEALING;
        }
        
        if (events.contains(CharacterBehaviorEvent.ATTACK_NORMAL) || 
            events.contains(CharacterBehaviorEvent.ATTACK_UP) || 
            events.contains(CharacterBehaviorEvent.ATTACK_DOWN)) {
            newMode = CharacterMode.ATTACK;
        }
        
        character.setMode(newMode);

        // 3. Effects Logic
        for (CharacterBehaviorEvent event : events) {
            switch (event) {
                
            }
        }
    }
    
    private static void updateGameLogic(float deltaTime) {
        // 更新游戏对象状态
    }
    private static void checkGameConditions() {
        // 检查游戏胜负等条件
    }
}
