package com.example.core;

import com.example.input.InputManager;
import com.example.gameobjects.character.Character;
import com.example.gameobjects.character.Character.CharacterBehavior;
import com.example.gameobjects.skill.PurpleGragon;
import com.example.gameobjects.terrain.Terrain;
import com.example.math.Rect;
import com.example.scene.GameScene;
import com.example.gameobjects.GameObject;
import com.example.gameobjects.skill.PurpleDragonConfig;
import com.example.gameobjects.character.CharacterConfig;
import com.example.gameobjects.character.Character.Orientation;
import com.example.math.Vector2;
import com.example.math.Rect;
import org.lwjgl.glfw.GLFW;
import java.util.Map;

/**
 * 游戏逻辑
 * 代表单轮的处理和更新流程
 */
public class GameLogic {
    // 单轮处理：处理输入 -> 更新逻辑 -> 检查判定
    public static void processFrame(float deltaTime, GameScene scene) {
        // 处理输入
        processInput(deltaTime, scene);
        InputManager.getInstance().update(deltaTime);
        // 更新游戏逻辑
        updateGameLogic(deltaTime, scene);

        // 检查游戏判定
        checkGameConditions(scene);
    }
    private static void processInput(float deltaTime, GameScene scene) {
        Character character = scene.getMainCharacter();
        InputManager input = InputManager.getInstance();
        input.pollEvents();
        Map<CharacterBehavior, Float> behaviors = character.getBehaviors();
        //是否有阻塞
        for(Float value : behaviors.values()){
            if(value > 0){
                return;//键盘输入的行为均阻塞，hurt并非键盘输入行为故不在此处处理
            }
        }

        // 首先处理决定方向的点按AD
        if(input.isKeyJustPressed(GLFW.GLFW_KEY_A)) {
            character.setOrientation(Orientation.LEFT);
        }

        if(input.isKeyJustPressed(GLFW.GLFW_KEY_D)) {
            character.setOrientation(Orientation.RIGHT);
        }
        //处理长按AD
        boolean isWalk = false;
        if(input.getKeyHoldDuration(GLFW.GLFW_KEY_A)>= input.getLongPressThreshold()) {
            isWalk = true;
        }

        if(input.getKeyHoldDuration(GLFW.GLFW_KEY_D)>= input.getLongPressThreshold()) {
            isWalk = true;
        }

        if (isWalk) {
            character.addBehavior(CharacterBehavior.WALK);
        }
        // 优先级 1: 技能 (Q)
        if (input.isKeyJustPressed(GLFW.GLFW_KEY_Q)) {
            if(character.getCurrentEnergy() >= PurpleDragonConfig.ENERGY_COST){//能量充足
                character.addBehavior(CharacterBehavior.CAST_SKILL);
                return;
            }
        }

        // 优先级 2: 冲刺 (L)
        if (input.isKeyJustPressed(GLFW.GLFW_KEY_L)) {
            if(character.getRemainingDashes() > 0){
                character.addBehavior(CharacterBehavior.DASH);
                return;
            }
        }

        // 优先级 3: 攻击 (J)
        if (input.isKeyJustPressed(GLFW.GLFW_KEY_J)) {
            if (input.isKeyPressed(GLFW.GLFW_KEY_S)) {
                //如果人物处于腾空状态则执行下斩
                if(character.getPosition().y > scene.GROUND_Y) {
                    character.addBehavior(CharacterBehavior.ATTACK_DOWN);
                    return;
                }
            } 

            if (input.isKeyPressed(GLFW.GLFW_KEY_W)) {
                character.addBehavior(CharacterBehavior.ATTACK_UP);
            } else {
                character.addBehavior(CharacterBehavior.ATTACK_NORMAL);
            }
            return;
        }

        // 优先级 4: 跳跃 (K)
        if (input.isKeyJustPressed(GLFW.GLFW_KEY_K)) {
            if (character.getRemainingAirJumps() > 0) {
                character.addBehavior(CharacterBehavior.JUMP);
                return;
            }
        }

    }
    
    private static void updateGameLogic(float deltaTime, GameScene scene) {
        for(GameObject obj : scene.getGameObjects()) {
            obj.update(deltaTime, scene);
        }
    }

    private static void checkGameConditions(GameScene scene) {
        Character character = scene.getMainCharacter();
        handleCollisions(character, scene);
        boundaryChecks(character, scene);
    }

    private static void handleCollisions(Character character, GameScene scene) {
        Rect charBox = character.getBoundingBox();
        
        // 计算攻击判定框 (Attack Bounding Boxes)
        Rect attackUpBox = new Rect(
            charBox.x + CharacterConfig.ATTACK_UP_OFFSET_X,
            charBox.y + CharacterConfig.ATTACK_UP_OFFSET_Y,
            charBox.width + CharacterConfig.ATTACK_UP_OFFSET_WIDTH,
            charBox.height + CharacterConfig.ATTACK_UP_OFFSET_HEIGHT
        );

        Rect attackDownBox = new Rect(
            charBox.x + CharacterConfig.ATTACK_DOWN_OFFSET_X,
            charBox.y + CharacterConfig.ATTACK_DOWN_OFFSET_Y,
            charBox.width + CharacterConfig.ATTACK_DOWN_OFFSET_WIDTH,
            charBox.height + CharacterConfig.ATTACK_DOWN_OFFSET_HEIGHT
        );

        Rect attackLeftBox = new Rect(
            charBox.x + CharacterConfig.ATTACK_LEFT_OFFSET_X,
            charBox.y + CharacterConfig.ATTACK_LEFT_OFFSET_Y,
            charBox.width + CharacterConfig.ATTACK_LEFT_OFFSET_WIDTH,
            charBox.height + CharacterConfig.ATTACK_LEFT_OFFSET_HEIGHT
        );

        Rect attackRightBox = new Rect(
            charBox.x + CharacterConfig.ATTACK_RIGHT_OFFSET_X,
            charBox.y + CharacterConfig.ATTACK_RIGHT_OFFSET_Y,
            charBox.width + CharacterConfig.ATTACK_RIGHT_OFFSET_WIDTH,
            charBox.height + CharacterConfig.ATTACK_RIGHT_OFFSET_HEIGHT
        );
        
        for (GameObject obj : scene.getGameObjects()) {
            if (obj == character) continue; // Skip self
            if(obj instanceof Terrain){
                Terrain terrain = (Terrain) obj;
                boolean hurted = false;
                boolean hitTrap = false;
                for(Rect trapBoundingBox : terrain.getTrapBoundingBoxes()){

                    if(!hurted && character.getBoundingBox().intersects(trapBoundingBox)){
                        character.clearBehaviors();
                        character.addBehavior(CharacterBehavior.HURT);
                        hurted = true;
                    }

                    if(!hitTrap && character.hasBehavior(CharacterBehavior.ATTACK_DOWN)){//可能update前设置为下批，但是现在已经没资格下批了
                        if (attackDownBox.intersects(trapBoundingBox)) {
                            character.getVelocity().y = CharacterConfig.JUMP_VELOCITY / 2;
                            character.setRemainingAirJumps(1);
                            character.setRemainingDashes(1);
                            hitTrap = true;
                        }
                    }
                }
            }

            if(obj instanceof PurpleGragon){
                PurpleGragon purpleGragon = (PurpleGragon) obj;
                if(character.hasBehavior(CharacterBehavior.ATTACK_DOWN)){
                    if (attackDownBox.intersects(purpleGragon.getBoundingBox())) {
                        character.getVelocity().y = CharacterConfig.JUMP_VELOCITY / 2;
                        character.setRemainingAirJumps(1);
                        character.setRemainingDashes(1);
                    }
                }
            }
        }
    }

    private static void boundaryChecks(Character character, GameScene scene) {
        Rect boundingbox = character.getBoundingBox();
        Vector2 pos = character.getPosition();
        Vector2 vel = character.getVelocity();

        // 左边界
        if (pos.x < 0) {
            pos.x = 0;
            vel.x = 0;
        }
        // 右边界
        if (pos.x + boundingbox.width > scene.SCREEN_WIDTH) {
            pos.x = scene.SCREEN_WIDTH - boundingbox.width;
            vel.x = 0;
        }
        // 上边界
        if (pos.y + boundingbox.height > scene.SCREEN_HEIGHT) {
            pos.y = scene.SCREEN_HEIGHT - boundingbox.height;
            vel.y = 0;
        }
        // 下边界 (Ground)
        if (pos.y < scene.GROUND_Y) {
            pos.y = scene.GROUND_Y;
            vel.y = 0;
            character.setRemainingAirJumps(CharacterConfig.MAX_AIR_JUMPS);
            character.setRemainingDashes(CharacterConfig.MAX_DASHES);
        }
    }
}
