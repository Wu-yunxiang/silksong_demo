package com.example.gameobjects.character;

import com.example.gameobjects.GameObject;
import com.example.math.PhysicsUtils;
import com.example.math.Rect;
import com.example.math.Vector2;
import com.example.scene.GameScene;
import com.example.gameobjects.skill.PurpleDragonConfig;
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
    private boolean isAlive = true;      // 存活状态 (Is Alive)
    private float modeDuration;          // mode持续时间 (Mode Duration)
    private boolean isImmune;            // 是否处于免疫状态 (Is Immune)
    private float immunityTime;          // 免疫时间 (Immunity Time)
    private Vector2 velocity;            // 速度 (Velocity)
    private Vector2 acceleration;        // 加速度 (Acceleration)
    private Vector2 basePosition;        // 基准位置 (Base Position)

    private int health;               // 血量 (Health)
    private int energy;               // 能量 (Energy)
    private Orientation orientation;
    private Map<CharacterBehavior, Float> behaviors; // 当前帧触发的行为事件列表
    private int remainingAirJumps;   // 当前腾空跳跃剩余次数
    private int remainingDashes;      // 当前冲刺剩余次数
    private Rect boundingBox; // 角色的边界框，用于碰撞检测等

    public Character() {
        this.velocity = new Vector2();
        this.acceleration = new Vector2();
        this.basePosition = new Vector2();
        this.orientation= Orientation.RIGHT;
        this.behaviors = new HashMap<>();
        this.behaviors.put(CharacterBehavior.STAND, 0.0f);
        this.remainingAirJumps = CharacterConfig.MAX_AIR_JUMPS; 
        this.remainingDashes = CharacterConfig.MAX_DASHES;
        this.boundingBox = new Rect(0, 0, CharacterConfig.DEFAULT_WIDTH, CharacterConfig.DEFAULT_HEIGHT); // 设置默认包围盒大小
        
        // 初始化血量和能量
        this.health = CharacterConfig.MAX_HEALTH;
        this.energy = 0;
    }

    /**
     * 对外可见的角色模式枚举（用于渲染与逻辑判断）
     */
    public enum CharacterBehavior {//每一轮的最后根据情况添加几个初始behavior用于阻塞或其它
        STAND,
        WALK,
        JUMP,
        FALL,
        DASH,
        ATTACK_NORMAL,
        ATTACK_UP,
        ATTACK_DOWN,
        CAST_SKILL, // 短按Q释放技能
        HEAL,       // 长按Q回血，但是先实现不论长短都视为释放技能
        HURT
    }

    public enum Orientation {
        LEFT,
        RIGHT
    }

    public void updateRemainingTime(float deltaTime, CharacterBehavior behavior) {
        float remainingTime = behaviors.get(behavior) - deltaTime;
        if (remainingTime <= 0) {
            behaviors.remove(behavior);
        } else {
            behaviors.put(behavior, remainingTime);
        }
    }

    @Override
    public void update(float deltaTime, GameScene scene) {//此时hasBehavior无冲突且合理地包含了当前所有行为
        // 1. 根据 behaviors 更新状态 (速度/加速度)
        if(hasBehavior(CharacterBehavior.STAND)){
            velocity.set(0,0);
            acceleration.set(0,0);
            updateRemainingTime(deltaTime, CharacterBehavior.STAND);
        }

        if(hasBehavior(CharacterBehavior.WALK)){
            velocity.setX((orientation == Orientation.RIGHT) ? CharacterConfig.MOVE_SPEED : -CharacterConfig.MOVE_SPEED);
            acceleration.setX(0);
        }
        
        if(hasBehavior(CharacterBehavior.DASH)){
            velocity.set((orientation == Orientation.RIGHT) ? CharacterConfig.DASH_SPEED : -CharacterConfig.DASH_SPEED, 0);
            acceleration.set(0,0);
            remainingDashes--;
            updateRemainingTime(deltaTime, CharacterBehavior.DASH);
        }

        // 处理跳跃 (瞬时速度)
        if (hasBehavior(CharacterBehavior.JUMP)) {
            velocity.setY(CharacterConfig.JUMP_VELOCITY);
            acceleration.setY(CharacterConfig.GRAVITY);
            remainingAirJumps--;
        }

        if(hasBehavior(CharacterBehavior.CAST_SKILL)){
            velocity.set(0,0);
            acceleration.set(0,0);
            updateRemainingTime(deltaTime, CharacterBehavior.CAST_SKILL);
            consumeEnergy(PurpleDragonConfig.ENERGY_COST);
        }

        if(hasBehavior(CharacterBehavior.HURT)){
            updateRemainingTime(deltaTime, CharacterBehavior.HURT);
        }

        // 物理计算
        PhysicsUtils.updatePhysicsState(basePosition, velocity, acceleration, deltaTime);
        
        // 同步判定箱
        boundingBox.setPosition(basePosition.x, basePosition.y);
    }

    public void clearBehaviors() {
        behaviors.clear();
    }

    public Rect getBoundingBox() {
        return boundingBox;
    }
    
    public Map<CharacterBehavior, Float> getBehaviors() {
        return behaviors;
    }

    public void addBehavior(CharacterBehavior behavior){
        this.behaviors.put(behavior, CharacterConfig.getBlockingDuration(behavior));
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

    // --- 血量与能量接口 ---

    public int getCurrentHealth() {
        return health;
    }

    public void takeDamage(int amount) {
        health -= amount;
        if (health <= 0) {
            health = 0;
            isAlive = false;
            // 可以在这里添加死亡逻辑
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
        return basePosition;
    }

    public Vector2 getVelocity() {
        return velocity;
    }
}
