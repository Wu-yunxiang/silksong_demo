package com.example.gameobjects.character;

import com.example.gameobjects.GameObject;
import com.example.math.PhysicsUtils;
import com.example.math.Rect;
import com.example.math.Vector2;
import com.example.scene.GameScene;
import com.example.gameobjects.skill.PurpleDragon;
import com.example.pictureconfig.CharacterPicturesInformation;
import java.util.HashMap;
import java.util.Map;


/**
 * 角色类 (Character Class)
 * 说明：
 * - 一个character有状态组件（血量能量两个object），有技能组件(一个object列表)。有基本信息含mode 
 * - 渲染的时候主体根据mode分类渲染，组件要渲染
 */
public class Character extends GameObject {
    // 基础字段
    private boolean isAlive;            // 是否存活 (Is Alive)
    private boolean isImmune;            // 是否处于免疫状态 (Is Immune)
    private float immunityTime;          // 免疫时间 (Immunity Time)
    private Vector2 velocity;            // 速度 (Velocity)
    private Vector2 acceleration;        // 加速度 (Acceleration)
    private Vector2 position;         // 动态基准位置 （人物中心在屏幕中的位置）

    private int health;               // 血量 (Health)
    private int energy;               // 能量 (Energy)
    private Orientation orientation;
    private Map<CharacterBehavior, Float> behaviors; // 当前帧触发的行为事件列表
    private int actionNum;
    private int remainingAirJumps;   // 当前腾空跳跃剩余次数
    private int remainingDashes;      // 当前冲刺剩余次数
    private float remainingDashCooldown; // 冲刺剩余冷却时间
    private GameScene scene;

    public Character(GameScene scene) {
        this.scene = scene;
        this.velocity = new Vector2();
        this.acceleration = new Vector2();
        this.position = new Vector2();
        this.orientation= Orientation.RIGHT;
        this.behaviors = new HashMap<>();
        this.addBehavior(CharacterBehavior.STAND);
        this.actionNum =1;
        this.isAlive = true;
        this.remainingAirJumps = CharacterConfig.MAX_AIR_JUMPS; 
        this.remainingDashes = CharacterConfig.MAX_DASHES;
        //attackBox和hitBox的初始化留待后续根据贴图和需求实现 
        this.health = CharacterConfig.MAX_HEALTH;
        this.energy = 0;// 初始化血量和能量
    }

    /**
     * 对外可见的角色模式枚举（用于渲染与逻辑判断）
     */
    public enum CharacterBehavior {//每一轮的最后根据情况添加几个初始behavior用于阻塞或其它
        STAND,
        WALK,
        JUMP,
        DASH,
        ATTACK_NORMAL,
        ATTACK_UP,
        ATTACK_DOWN,
        CAST_SKILL, // 短按Q释放技能
        HEAL,       // 按H回血
        HURT
    }

    public enum Orientation {
        LEFT,
        RIGHT
    }

    public void updateRemainingTime(float deltaTime, CharacterBehavior behavior) {
        if(CharacterConfig.getBlockingDuration(behavior) == CharacterConfig.DURATION_NONE){
            return;
        }
        float remainingTime = behaviors.get(behavior) - deltaTime;
        if (remainingTime <= 0) {
            behaviors.put(behavior, remainingTime);
            removeBehavior(behavior);
        } else {
            behaviors.put(behavior, remainingTime);
        }
    }

    @Override
    public void update(float deltaTime, GameScene scene) {//每帧update前设置behaviors，update结束后清除walk。此时hasBehavior无冲突且合理地包含了当前所有行为，jump和walk、dash、cast_skill、hurt不互斥，其余behavior均互斥
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
        PhysicsUtils.updatePhysicsState(position, velocity, acceleration, deltaTime);

        for(Map.Entry<CharacterBehavior, Float> entry : behaviors.entrySet()){
            CharacterBehavior behavior = entry.getKey();
            updateRemainingTime(deltaTime, behavior);
        }//去除掉已经结束的behavior，或更新剩余时间

    }

    public void clearBehaviors() {
        for(CharacterBehavior behavior : behaviors.keySet()){
            if(behavior == CharacterBehavior.JUMP){
                continue;
            }
            removeBehavior(behavior);
        }
    }

    public void removeBehavior(CharacterBehavior behavior){
        if(isOnGround()){
            acceleration.set(0,0);
        } else{
            acceleration.set(0,CharacterConfig.GRAVITY);
        }

        if(behavior == CharacterBehavior.HEAL && behaviors.get(behavior) <= 0 ){
            heal(CharacterConfig.HEAL_AMOUNT);
        }

        if(behavior == CharacterBehavior.CAST_SKILL && behaviors.get(behavior) <= 0 ){
            scene.addGameObject(new PurpleDragon(position));
        }

        if(behavior == CharacterBehavior.WALK){
            velocity.setX(0);
        }

        if(!isAttack(behavior) && CharacterConfig.getBlockingDuration(behavior) != CharacterConfig.DURATION_NONE){
            velocity.set(0,0);
        }

        if(behavior == CharacterBehavior.DASH){
            remainingDashCooldown = CharacterConfig.DASH_COOLDOWN;
        }

        this.behaviors.remove(behavior);
    }

    public CharacterBehavior getPrimaryBehavior(){
        for(CharacterBehavior behavior : behaviors.keySet()){
            if(CharacterConfig.getBlockingDuration(behavior) != CharacterConfig.DURATION_NONE){
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
        CharacterPicturesInformation.PictureInformation pictureInfo = CharacterPicturesInformation.characterPicturesInfo.get(primaryBehavior).get(actionNum-1);
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
        CharacterPicturesInformation.PictureInformation pictureInfo = CharacterPicturesInformation.characterPicturesInfo.get(primaryBehavior).get(actionNum-1);
        
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
    
    public Map<CharacterBehavior, Float> getBehaviors() {
        return behaviors;
    }

    public boolean isFalling() {
        return true;//暂时先这样写，后续根据碰撞检测结果实现
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

            if(CharacterConfig.getBlockingDuration(behavior) != CharacterConfig.DURATION_NONE){
                if(!isAttack(behavior) && !hasAttackBehavior()){
                    removeBehavior(CharacterBehavior.WALK);
                }
            }
        }

        this.behaviors.put(behavior, CharacterConfig.getBlockingDuration(behavior));

        switch (behavior) {
            case WALK:
                velocity.setX((orientation == Orientation.RIGHT) ? CharacterConfig.MOVE_SPEED : -CharacterConfig.MOVE_SPEED);
                acceleration.setX(0);
                break;
            case JUMP:
                velocity.setY(CharacterConfig.JUMP_VELOCITY);
                acceleration.setY(CharacterConfig.GRAVITY);
                remainingAirJumps--;
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
    }

    public boolean hasBehavior(CharacterBehavior characterBehavior){
        return behaviors.containsKey(characterBehavior);
    }
    public void setBehaviors(Map<CharacterBehavior, Float> behaviors) {
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
        return true;//暂时先这样写，后续根据碰撞检测结果实现
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

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }
}
