package com.example.gameobjects.character;

import com.example.gameobjects.GameObject;
import com.example.math.PhysicsUtils;
import com.example.math.Rect;
import com.example.math.Vector2;
import com.example.scene.GameScene;
import com.example.pictureconfig.CharacterPicturesInformation;
import org.lwjgl.opengl.GL11;
import com.example.pictureconfig.GameSceneConfig;
import com.example.gameobjects.skill.PurpleDragon;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色类 (Character Class)
 */
public class Character extends GameObject {
    private GameScene scene;
    private boolean groundLock;    //当前轮次是否不能更改isOnGround状态
    private boolean isOnGround;        // 是否在地面上 (Is On Ground)
    private PurpleDragon purpleDragon; // 角色释放的龙波对象 (Purple Dragon Object)
    private boolean isAlive;            // 是否存活 (Is Alive)
    private boolean isImmune;            // 是否处于免疫状态 (Is Immune)
    private float immunityTime;          // 免疫时间 (Immunity Time)
    private Vector2 velocity;            // 速度 (Velocity)
    private Vector2 acceleration;        // 加速度 (Acceleration)
    private Vector2 position;         // 动态基准位置 （人物中心在屏幕中的位置）
    private int health;               // 血量 (Health)
    private int energy;               // 能量 (Energy)
    private Orientation orientation;
    private Map<CharacterBehavior, ActionState> behaviors; // 当前帧触发的行为事件列表
    private int remainingAirJumps;   // 当前腾空跳跃剩余次数
    private int remainingDashes;      // 当前冲刺剩余次数
    private float remainingDashCooldown; // 冲刺剩余冷却时间

    public Character(GameScene scene) {
        this.scene = scene;
        this.groundLock = false;
        this.isOnGround = true;
        this.position = new Vector2();
        this.velocity = new Vector2();
        this.acceleration = new Vector2();
        this.orientation= Orientation.RIGHT;
        this.behaviors = new HashMap<>();
        this.addBehavior(CharacterBehavior.STAND);
        this.position.x = GameSceneConfig.initialCharacterPositionX;
        this.isAlive = true;
        this.remainingAirJumps = CharacterConfig.MAX_AIR_JUMPS; 
        this.remainingDashes = CharacterConfig.MAX_DASHES;
        this.health = CharacterConfig.MAX_HEALTH;
        this.energy = CharacterConfig.MAX_ENERGY;// 初始化血量和能量
    }

    /**
     * 对外可见的角色模式枚举（用于渲染与逻辑判断）
     */
    public enum CharacterBehavior {
        STAND,
        WALK,
        JUMP,
        DASH,
        ATTACK_NORMAL,
        ATTACK_UP,
        ATTACK_DOWN,
        CAST_SKILL, 
        HEAL,       
        HURT
    }

    public enum Orientation {
        LEFT,
        RIGHT
    }

    public static class ActionState{
        public int actionNum;
        public float duration;

        public ActionState(int actionNum, float duration){
            this.actionNum = actionNum;
            this.duration = duration;
        }

        public void setActionNum(int actionNum){
            this.actionNum = actionNum;
        }

        public void setDuration(float duration){
            this.duration = duration;
        }
    }

    public ActionState getActionState(CharacterBehavior behavior){
        return behaviors.get(behavior);
    }

    public int getBehaviorSize(CharacterBehavior behavior){
        return CharacterPicturesInformation.characterPicturesInfo.get(behavior).size();
    }

    public float getDuration(CharacterBehavior behavior, int actionNum){
        return CharacterPicturesInformation.characterPicturesInfo.get(behavior).get(actionNum - 1).durationSeconds;
    }

    public void updateRemainingTime(float deltaTime, CharacterBehavior behavior) {
        ActionState actionState = getActionState(behavior);
        float remainingTime = actionState.duration - deltaTime;
        if (remainingTime <= 0) {
            if(actionState.actionNum == getBehaviorSize(behavior)){
                if (behavior == CharacterBehavior.STAND || behavior == CharacterBehavior.WALK) { // Loop STAND and WALK
                    actionState.setActionNum(1);
                    actionState.setDuration(getDuration(behavior, 1));
                } else {
                    if(behavior == CharacterBehavior.HEAL){
                        heal(CharacterConfig.HEAL_AMOUNT);
                    }
                    if(behavior == CharacterBehavior.DASH){
                        remainingDashCooldown = CharacterConfig.DASH_COOLDOWN;
                    }
                    removeBehavior(behavior);
                }
            }else{
                if(behavior ==CharacterBehavior.JUMP){
                    if(getPrimaryBehavior() != CharacterBehavior.JUMP) return;
                    if(actionState.actionNum == CharacterConfig.flyingActionNum){
                        if(velocity.y > 0) return;
                    }else if(actionState.actionNum == CharacterConfig.fallingActionNum){
                        if(!isOnGround){
                            return;
                        }
                    }
                }
                actionState.setActionNum(actionState.actionNum + 1);
                actionState.setDuration(getDuration(behavior, actionState.actionNum));
                if(behavior == CharacterBehavior.CAST_SKILL){
                    if(actionState.actionNum == CharacterConfig.skillStartActionNum){
                        scene.addGameObject(new PurpleDragon(this));
                    } 
                }

                if(behavior ==CharacterBehavior.JUMP){
                    if(CharacterConfig.flyingActionNum == actionState.actionNum){
                        if(remainingAirJumps > 1){
                            velocity.setY(CharacterConfig.JUMP_VELOCITY);
                        } else {
                            velocity.setY(CharacterConfig.DOUBLE_JUMP_VELOCITY);
                        }
                        acceleration.setY(CharacterConfig.GRAVITY);
                        isOnGround = false;
                        groundLock = true;
                        remainingAirJumps--;
                    }
                }
            }
            fixHeight();
        } else {
            actionState.setDuration(remainingTime);
        }
    }

    @Override
    public void update(float deltaTime, GameScene scene) {
        if(!isAlive){
            scene.removeGameObject(this);
            return;
        }

        if(isImmune){
            immunityTime -= deltaTime;
            if(immunityTime <= 0){
                isImmune = false;
            }
        }//更新免疫状态

        if(remainingDashCooldown > 0){
            remainingDashCooldown -= deltaTime;
            if(remainingDashCooldown < 0){
                remainingDashCooldown = 0;
            }   
        }//更新冲刺冷却时间

        // 物理计算
        if(orientation == Orientation.LEFT){
            velocity.x = -Math.abs(velocity.x);
        } else{
            velocity.x = Math.abs(velocity.x);
        }
        PhysicsUtils.updatePhysicsState(position, velocity, acceleration, deltaTime);
        // 使用副本遍历，防止遍历过程中修改原始 Map 导致异常
        List<CharacterBehavior> behaviorsCopy = new java.util.ArrayList<>(behaviors.keySet());
        for(CharacterBehavior behavior : behaviorsCopy){
            if(behaviors.containsKey(behavior)){
                updateRemainingTime(deltaTime, behavior);
            }
        } // 更新剩余时间 / 改变动作 / 去除掉已经结束的behavior
    }

    public void fixHeight(){
        if(isOnGround){
            position.y -= getHitBox().y - GameSceneConfig.GroundHeight;
            velocity.setY(0);
            acceleration.setY(0);
        }
    }

    @Override
    public void render() {
        // 根据当前主要行为与动作编号获取贴图信息
        CharacterBehavior primaryBehavior = getPrimaryBehavior();
        if(!behaviors.containsKey(primaryBehavior)){
            return; // behaviors is empty, nothing to render
        }
        List<CharacterPicturesInformation.PictureInformation> pics = CharacterPicturesInformation.characterPicturesInfo.get(primaryBehavior);
        CharacterPicturesInformation.PictureInformation pictureInfo = pics.get(behaviors.get(primaryBehavior).actionNum - 1);

        int texId = pictureInfo.textureId;
        float w = pictureInfo.pictureSize.x;
        float h = pictureInfo.pictureSize.y;
        float baseX = pictureInfo.basePosition.x;
        float baseY = pictureInfo.basePosition.y;

        // 计算绘制左下角坐标，使得图片中以 basePosition 为基点的位置对齐到角色的 world position
        float drawX;
        if (this.orientation == Orientation.RIGHT) {
            drawX = this.position.x - baseX;
        } else {
            // 水平镜像时，图片左边相对于基点的偏移会变为 (w - baseX)
            drawX = this.position.x - (w - baseX);
        }
        float drawY = this.position.y - baseY;

        // 绘制四边形并根据朝向选择纹理坐标（水平镜像通过交换 u）
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
        GL11.glBegin(GL11.GL_QUADS);
        if (this.orientation == Orientation.RIGHT) {
            GL11.glTexCoord2f(0f, 0f); GL11.glVertex2f(drawX, drawY);
            GL11.glTexCoord2f(1f, 0f); GL11.glVertex2f(drawX + w, drawY);
            GL11.glTexCoord2f(1f, 1f); GL11.glVertex2f(drawX + w, drawY + h);
            GL11.glTexCoord2f(0f, 1f); GL11.glVertex2f(drawX, drawY + h);
        } else {
            // 水平镜像：交换 u=0/1
            GL11.glTexCoord2f(1f, 0f); GL11.glVertex2f(drawX, drawY);
            GL11.glTexCoord2f(0f, 0f); GL11.glVertex2f(drawX + w, drawY);
            GL11.glTexCoord2f(0f, 1f); GL11.glVertex2f(drawX + w, drawY + h);
            GL11.glTexCoord2f(1f, 1f); GL11.glVertex2f(drawX, drawY + h);
        }
        GL11.glEnd();
    }

    public CharacterBehavior getPrimaryBehavior(){
        for(CharacterBehavior behavior : behaviors.keySet()){
            if(CharacterConfig.isBlocking(behavior)){
                return behavior;
            }
        }

        if(behaviors.containsKey(CharacterBehavior.JUMP)){
            return CharacterBehavior.JUMP;
        } else if(behaviors.containsKey(CharacterBehavior.WALK)){
            return CharacterBehavior.WALK;
        } else{
            return CharacterBehavior.STAND;
        }
    }

    public Rect getHitBox() {
        CharacterBehavior primaryBehavior = getPrimaryBehavior();
        CharacterPicturesInformation.PictureInformation pictureInfo = CharacterPicturesInformation.characterPicturesInfo.get(primaryBehavior).get(behaviors.get(primaryBehavior).actionNum-1);
        if(Orientation.RIGHT == this.orientation){
            return new Rect(position.x + pictureInfo.hitBox.x - pictureInfo.basePosition.x,
                            position.y + pictureInfo.hitBox.y - pictureInfo.basePosition.y,
                            pictureInfo.hitBox.width,
                            pictureInfo.hitBox.height);
        } else{
            return new Rect(position.x + pictureInfo.basePosition.x - pictureInfo.hitBox.x - pictureInfo.hitBox.width,
                            position.y + pictureInfo.hitBox.y - pictureInfo.basePosition.y,
                            pictureInfo.hitBox.width,
                            pictureInfo.hitBox.height);
        }
    }

    public Rect getAttackBox() {
        CharacterBehavior primaryBehavior = getPrimaryBehavior();
        CharacterPicturesInformation.PictureInformation pictureInfo = CharacterPicturesInformation.characterPicturesInfo.get(primaryBehavior).get(behaviors.get(primaryBehavior).actionNum-1);

        if(pictureInfo.attackBox == null){
            return null;
        }
        if(Orientation.RIGHT == this.orientation){
            return new Rect(position.x + pictureInfo.attackBox.x - pictureInfo.basePosition.x,
                            position.y + pictureInfo.attackBox.y - pictureInfo.basePosition.y,
                            pictureInfo.attackBox.width,
                            pictureInfo.attackBox.height);
        } else{
            return new Rect(position.x + pictureInfo.basePosition.x - pictureInfo.attackBox.x - pictureInfo.attackBox.width,
                            position.y + pictureInfo.attackBox.y - pictureInfo.basePosition.y,
                            pictureInfo.attackBox.width,
                            pictureInfo.attackBox.height);
        }
    }

    public boolean isAttack(CharacterBehavior behavior){
        return behavior == CharacterBehavior.ATTACK_NORMAL || behavior == CharacterBehavior.ATTACK_UP || behavior == CharacterBehavior.ATTACK_DOWN;
    }

    public boolean hasAttackBehavior(){
        return hasBehavior(CharacterBehavior.ATTACK_NORMAL) ||
               hasBehavior(CharacterBehavior.ATTACK_UP) ||
               hasBehavior(CharacterBehavior.ATTACK_DOWN);
    }

    public void addBehavior(CharacterBehavior behavior){

        if(behavior == CharacterBehavior.HURT){
            clearBehaviors();
        } else{
            if(behavior != CharacterBehavior.STAND){
                removeBehavior(CharacterBehavior.STAND);
            }

            if(CharacterConfig.isBlocking(behavior)){
                if(!isAttack(behavior) && !hasAttackBehavior()){
                    removeBehavior(CharacterBehavior.WALK);
                }
            }
        }

        this.behaviors.put(behavior, new ActionState(1, getDuration(behavior, 1)));

        switch (behavior) {
            case WALK:
                velocity.setX((orientation == Orientation.RIGHT) ? CharacterConfig.WALK_SPEED : -CharacterConfig.WALK_SPEED);
                acceleration.setX(0);
                break;
            case DASH:
                velocity.set((orientation == Orientation.RIGHT) ? CharacterConfig.DASH_SPEED : -CharacterConfig.DASH_SPEED, 0);
                acceleration.set(0,0);
                remainingDashes--;
                break;
            case CAST_SKILL:
            case HEAL:
                velocity.set(0,0);
                acceleration.set(0,0);
                break;
            case HURT:
                velocity.set(0,0);
                if(isOnGround()){
                    acceleration.set(0,0);
                } else{
                    acceleration.set(0,CharacterConfig.GRAVITY);
                }
                takeDamage(1);
                setImmune(true);
                break;
            case STAND:
                velocity.set(0,0);
                acceleration.set(0,0);
                break;
            default:
                break;
        }

        fixHeight();
    }

    public void clearBehaviors() { // 处理中断情况

        // 使用副本遍历，防止遍历过程中修改原始 Map 导致异常
        List<CharacterBehavior> behaviorsCopy = new java.util.ArrayList<>(behaviors.keySet());
        for(CharacterBehavior behavior : behaviorsCopy){
            if(!behaviors.containsKey(behavior)){
                continue;
            }

            if(behavior == CharacterBehavior.JUMP){
                continue;
            }

            ActionState actionState = getActionState(behavior);
            if(behavior == CharacterBehavior.CAST_SKILL){
                if(actionState.actionNum >= CharacterConfig.skillStartActionNum && actionState.actionNum < CharacterConfig.skillCompleteActionNum){
                    purpleDragon.setAlive(false);
                }
            }

            if(behavior == CharacterBehavior.DASH){
                remainingDashCooldown = CharacterConfig.DASH_COOLDOWN;
            }

            removeBehavior(behavior);
        }
    }

    public void removeBehavior(CharacterBehavior behavior){ //单纯移除行为
        if(isOnGround()){
            acceleration.set(0,0);
        } else{
            acceleration.set(0,CharacterConfig.GRAVITY);
        }

        if(behavior == CharacterBehavior.WALK){
            velocity.setX(0);
        }

        if(!isAttack(behavior) && CharacterConfig.isBlocking(behavior)){
            velocity.set(0,0);
        }

        this.behaviors.remove(behavior);
        if(behaviors.isEmpty()){
            addBehavior(CharacterBehavior.STAND);
        }
    }

    public Map<CharacterBehavior, ActionState> getBehaviors() {
        return behaviors;
    }

    public boolean hasBehavior(CharacterBehavior characterBehavior){
        return behaviors.containsKey(characterBehavior);
    }
    public void setBehaviors(Map<CharacterBehavior, ActionState> behaviors) {
        this.behaviors = behaviors;
    }

    public void setOrientation(Orientation orientation){
        this.orientation = orientation;
    }

    public Orientation getOrientation(){
        return orientation;
    }

    public int getRemainingAirJumps() {
        return remainingAirJumps;
    }

    public void setRemainingAirJumps(int remainingAirJumps) {
        this.remainingAirJumps = remainingAirJumps;
    }

    public int getRemainingDashes() {
        return remainingDashes;
    }

    public void setRemainingDashes(int remainingDashes) {
        this.remainingDashes = remainingDashes;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setImmune(boolean immune) {
        isImmune = immune;
        if (immune) {
            immunityTime = CharacterConfig.IMMUNITY_DURATION;
        }
    }

    public boolean isImmune() {
        return isImmune;
    }

    public boolean isOnGround() {
        return isOnGround;
    }

    public void setIsOnGround(boolean isOnGround) {
        this.isOnGround = isOnGround;
    }

    public float getDashCooldown() {
        return remainingDashCooldown;
    }

    // --- 血量与能量接口 ---

    public int getCurrentHealth() {
        return health;
    }

    public void takeDamage(int amount) {
        health -= amount;
        if (health <= 0) {
            health = 0;
            isAlive = false;
        }
    }

    public void heal(int amount) {
        health += amount;
        if (health > CharacterConfig.MAX_HEALTH) {
            health = CharacterConfig.MAX_HEALTH;
        }
    }

    public int getCurrentEnergy() {
        return energy;
    }

    public void consumeEnergy(int amount) {
        energy -= amount;
    }

    public void restoreEnergy(int amount) {
        energy += amount;
        if (energy > CharacterConfig.MAX_ENERGY) {
            energy = CharacterConfig.MAX_ENERGY;
        }
    }

    // --- 
    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Vector2 getAcceleration() {
        return acceleration;
    }   

    public int getActionNum() {
        return behaviors.get(getPrimaryBehavior()).actionNum;
    }

    public void setPurpleDragon(PurpleDragon purpleDragon) {
        this.purpleDragon = purpleDragon;
    }

    public void setGroundLock(boolean groundLock) {
        this.groundLock = groundLock;
    }   

    public boolean getGroundLock() {
        return groundLock;
    }
}
