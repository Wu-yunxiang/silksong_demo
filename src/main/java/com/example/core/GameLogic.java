package com.example.core;

import com.example.input.InputManager;
import com.example.gameobjects.character.Character;
import com.example.gameobjects.character.Character.CharacterBehavior;
import com.example.gameobjects.skill.PurpleDragon;
import com.example.math.Rect;
import com.example.scene.GameScene;
import com.example.gameobjects.GameObject;
import com.example.gameobjects.skill.PurpleDragonConfig;
import com.example.gameobjects.character.CharacterConfig;
import com.example.gameobjects.character.Character.Orientation;
import com.example.pictureconfig.*;
import com.example.math.Vector2;
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
        InputManager.getInstance().update();
        // 更新游戏逻辑
        updateGameLogic(deltaTime, scene);

        // 检查游戏判定
        checkGameConditions(scene);    
    }

    private static void processInput(float deltaTime, GameScene scene) {
        Character character = scene.getCharacter();
        InputManager input = InputManager.getInstance();
        input.pollEvents();

        Map<CharacterBehavior, Character.ActionState> behaviors = character.getBehaviors();

        for(CharacterBehavior behavior : behaviors.keySet()){
            if(CharacterConfig.isBlocking(behavior)){
                return; // 键盘输入的行为均阻塞
            }
        }

        // 首先确定这一帧的方向
        if(input.isKeyJustPressed(GLFW.GLFW_KEY_A)||input.isKeyJustPressed(GLFW.GLFW_KEY_D)) {
            if(input.isKeyJustPressed(GLFW.GLFW_KEY_A) && character.getOrientation() != Orientation.LEFT){
                character.setOrientation(Orientation.LEFT);
            } else if(input.isKeyJustPressed(GLFW.GLFW_KEY_D) && character.getOrientation() != Orientation.RIGHT){
                character.setOrientation(Orientation.RIGHT);
            }
        }// 如果刚刚有按下A或D键（如果存在和原方向不同的，改成为和原方向不同的方向)

        else{
            if(input.isKeyPressed(GLFW.GLFW_KEY_A)||input.isKeyPressed(GLFW.GLFW_KEY_D)) {
                if(input.isKeyPressed(GLFW.GLFW_KEY_A) && input.isKeyPressed(GLFW.GLFW_KEY_D)){
                    //同时按下不改变方向
                } else if(input.isKeyPressed(GLFW.GLFW_KEY_A)){
                    character.setOrientation(Orientation.LEFT);
                } else if(input.isKeyPressed(GLFW.GLFW_KEY_D)){
                    character.setOrientation(Orientation.RIGHT);
                }
            }
        }//如果刚刚没有按下方向键

        // 优先级 1: 技能 (Q)
        if (input.isKeyJustPressed(GLFW.GLFW_KEY_Q)) {
            if(character.getCurrentEnergy() >= PurpleDragonConfig.ENERGY_COST){//能量充足
                character.addBehavior(CharacterBehavior.CAST_SKILL);
                character.consumeEnergy(PurpleDragonConfig.ENERGY_COST);
                return;
            }
        }

        // 优先级 1.5: 回血 (H)
        if (input.isKeyJustPressed(GLFW.GLFW_KEY_H)||input.isKeyPressed(GLFW.GLFW_KEY_H)) {
            if(character.getCurrentEnergy() >= CharacterConfig.HEAL_COST && character.isOnGround()){//能量充足且在地面
                character.addBehavior(CharacterBehavior.HEAL);
                character.consumeEnergy(CharacterConfig.HEAL_COST);
                return;
            }
        }

        // 优先级 2: 冲刺 (L)
        if (input.isKeyJustPressed(GLFW.GLFW_KEY_L)) {
            if(character.getRemainingDashes() > 0 && character.getDashCooldown() <= 0){
                character.addBehavior(CharacterBehavior.DASH);
                return;
            }
        }

        // 优先级 3: 攻击 (J)
        if (input.isKeyJustPressed(GLFW.GLFW_KEY_J)) {
            if (input.isKeyPressed(GLFW.GLFW_KEY_S)) {
                //如果人物处于腾空状态则执行下斩
                if(!character.isOnGround()) {
                    character.addBehavior(CharacterBehavior.ATTACK_DOWN);
                    return;
                }
            } 

            if (input.isKeyPressed(GLFW.GLFW_KEY_W)) {
                character.addBehavior(CharacterBehavior.ATTACK_UP);
            } else {
                character.addBehavior(CharacterBehavior.ATTACK_NORMAL);
            }
        }

        // 优先级 4: 跳跃 (K)
        if (input.isKeyJustPressed(GLFW.GLFW_KEY_K)) {
            if (character.getRemainingAirJumps() > 0) {
                character.addBehavior(CharacterBehavior.JUMP);
            }
        }

        // walk 和 stand的处理放在最后
        if(input.isKeyPressed(GLFW.GLFW_KEY_A)||input.isKeyPressed(GLFW.GLFW_KEY_D)){
            if(!character.hasBehavior(CharacterBehavior.WALK)){
                character.addBehavior(CharacterBehavior.WALK);
            }
        } else{
            if(character.hasBehavior(CharacterBehavior.WALK)){
                character.removeBehavior(CharacterBehavior.WALK);
            }
            
            character.addBehavior(CharacterBehavior.STAND);
        }
    }
    
    private static void updateGameLogic(float deltaTime, GameScene scene) {
        //只有人物和龙update不为空，相当于更新人物和屏幕里的所有龙, 先更新人物再更新龙
        for (GameObject obj : scene.getGameObjects()) {
            if(obj instanceof Character){
                obj.update(deltaTime, scene);
            }
        }  

        for(GameObject obj : scene.getGameObjects()) {
            if(obj instanceof PurpleDragon){
                obj.update(deltaTime, scene);
            }
        }
    }

    private static void checkGameConditions(GameScene scene) {
        Character character = scene.getCharacter();
        handleCollisions(character, scene);
        boundaryChecks(character, scene);
    }

    private static void handleCollisions(Character character, GameScene scene) {
        Rect characterHitBox = character.getHitBox();
        Rect characterAttackBox = character.getAttackBox();
        
        for (GameObject obj : scene.getGameObjects()) {
            if(!(obj instanceof Character)) continue;
            boolean hurt = false;
            boolean attackSpikes = false;
            for(Rect spikesBounding : GameSceneConfig.spikesRanges){
                if(!hurt && !character.isImmune() && characterHitBox.intersects(spikesBounding)){
                    hurt = true;
                    character.addBehavior(CharacterBehavior.HURT);
                }

                if(!attackSpikes && character.hasBehavior(CharacterBehavior.ATTACK_DOWN)){
                    if (characterAttackBox.intersects(spikesBounding)) {
                        attackSpikes = true;
                        character.getVelocity().setY(CharacterConfig.DOUBLE_JUMP_VELOCITY);
                        character.setRemainingAirJumps(1);
                        character.setRemainingDashes(1);
                    }
                }
            }

            for(GameObject object : scene.getGameObjects()) {
                if(!(object instanceof PurpleDragon)) continue;
                PurpleDragon purpleDragon = (PurpleDragon) object;
                if(character.hasBehavior(CharacterBehavior.ATTACK_DOWN)){
                    if (characterAttackBox.intersects(purpleDragon.getBoundingBox())) {
                        character.getVelocity().setY(CharacterConfig.DOUBLE_JUMP_VELOCITY);
                        character.setRemainingAirJumps(1);
                        character.setRemainingDashes(1);
                    }
                }
            }
        }
    }

    private static void boundaryChecks(Character character, GameScene scene) {
        Rect characterHitBox = character.getHitBox();
        Vector2 pos = character.getPosition();
        // 下边界
        if(characterHitBox.y < GameSceneConfig.GroundHeight){
            pos.y -= characterHitBox.y - GameSceneConfig.GroundHeight;
            character.setRemainingAirJumps(CharacterConfig.MAX_AIR_JUMPS);
        }
        // 左边界
        if(characterHitBox.x < 0){
            pos.x -= characterHitBox.x;
        }
        // 右边界
        if (pos.x + characterHitBox.width >= GameSceneConfig.ScreenWidth) {
            pos.x -= (pos.x + characterHitBox.width) - (GameSceneConfig.ScreenWidth - 1);
        }
        // 上边界
        if (pos.y + characterHitBox.height >= GameSceneConfig.ScreenHeight) {
            pos.y -= (pos.y + characterHitBox.height) - (GameSceneConfig.ScreenHeight - 1);
        }   
        //龙的边界检查
        for(GameObject obj : scene.getGameObjects()) {
            if(!(obj instanceof PurpleDragon)) continue;
            PurpleDragon purpleDragon = (PurpleDragon) obj;
            Rect dragonBox = purpleDragon.getBoundingBox();
            if(dragonBox.x + dragonBox.width < 0 || dragonBox.x >= GameSceneConfig.ScreenWidth){
                purpleDragon.setAlive(false);
            }
        }
    }
}
