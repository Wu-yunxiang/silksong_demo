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

        Map<CharacterBehavior, Float> behaviors = character.getBehaviors();
        //是否有阻塞
        for(CharacterBehavior behavior : behaviors.keySet()){
            if(CharacterConfig.getBlockingDuration(behavior) > 0){
                return;//键盘输入的行为均阻塞，hurt并非键盘输入行为故不在此处处理
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
            character.addBehavior(CharacterBehavior.WALK);
        } else{
            if(character.isOnGround() && character.getBehaviors().isEmpty()){
                character.addBehavior(CharacterBehavior.STAND);
            }
        }

    }
    
    private static void updateGameLogic(float deltaTime, GameScene scene) {
        for (GameObject obj : scene.getGameObjects()) {
            obj.update(deltaTime, scene);
        } //只有人物和龙update不为空，相当于更新人物和屏幕里的所有龙 
    }

    private static void checkGameConditions(GameScene scene) {
        Character character = scene.getCharacter();
        boundaryChecks(character, scene);// 合理化了更新之后的状态
        handleCollisions(character, scene);
    }

    private static void handleCollisions(Character character, GameScene scene) {
        Rect characterHitBox = character.getHitBox();
        Rect characterAttackBox = character.getAttackBox();
        
        for (GameObject obj : scene.getGameObjects()) {
            if (obj == character) continue; // Skip self
            if (obj instanceof Terrain){
                Terrain terrain = (Terrain) obj;
                boolean hit = false;
                boolean hitTrap = false;
                for(Rect trapBoundingBox : terrain.getTrapBoundingBoxes()){

                    if(!hit && !character.isImmune() && characterHitBox.intersects(trapBoundingBox)){
                        hit = true;
                        character.addBehavior(CharacterBehavior.HURT);
                    }

                    if(!hitTrap && character.hasBehavior(CharacterBehavior.ATTACK_DOWN)){
                        if (characterAttackBox.intersects(trapBoundingBox)) {
                            character.getVelocity().setY(CharacterConfig.JUMP_VELOCITY * 0.8f);
                            character.setRemainingAirJumps(1);
                            character.setRemainingDashes(1);
                            hitTrap = true;
                        }
                    }
                }
            }

            if(obj instanceof PurpleDragon){
                PurpleDragon purpleDragon = (PurpleDragon) obj;
                if(character.hasBehavior(CharacterBehavior.ATTACK_DOWN)){
                    if (characterAttackBox.intersects(purpleDragon.getBoundingBox())) {
                        character.getVelocity().setY(CharacterConfig.JUMP_VELOCITY * 0.8f);
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
        Vector2 vel = character.getVelocity();
        // 下面if里的条件都不一定正确，到时候修改
        // 左边界
        if(characterHitBox.x < 0){
            //修改pos的x值把受击框移到边界上
            //把x方向速度改为0
        }
        // 右边界
        if (pos.x + characterHitBox.width > scene.SCREEN_WIDTH) {
            //同上
        }
        // 上边界
        if (pos.y + characterHitBox.height > scene.SCREEN_HEIGHT) {
            //修改pos的y值把受击框移到边界上
            //把y方向速度改为0，加速度改为重力
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
