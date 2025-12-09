package com.example.gameobjects.character;

import com.example.gameobjects.character.Character.CharacterBehavior;
/**
 * CharacterConfig
 * 存储角色相关的配置常量
 */
public class CharacterConfig {
    // 物理常量
    public static final float GRAVITY = -1500.0f;
    public static final float MOVE_SPEED = 300.0f;
    public static final float JUMP_VELOCITY = 600.0f;
    public static final float DASH_SPEED = 800.0f;

    // 游戏逻辑常量
    public static final int MAX_AIR_JUMPS = 2;
    public static final int MAX_DASHES = 1;
    public static final int MAX_HEALTH = 8;
    public static final int MAX_ENERGY = 12;

    // 行为阻塞时间 (秒)
    public static final float DURATION_ATTACK = 0.4f;
    public static final float DURATION_SKILL = 0.5f;
    public static final float DURATION_DASH = 0.2f;
    public static final float DURATION_HURT = 0.3f;
    public static final float DURATION_HEAL = 1.0f;
    public static final float DURATION_NONE = 0.0f;

    // 技能与回血消耗
    public static final int HEAL_COST = 3;
    public static final int HEAL_AMOUNT = 1;

    //冲刺冷却时间 (秒)
    public static final float DASH_COOLDOWN = 1.0f;
    // 受伤后免疫时间 (秒)
    public static final float IMMUNITY_DURATION = 1.0f; 
    
    /**
     * 获取指定行为的阻塞时间
     */
    public static float getBlockingDuration(CharacterBehavior behavior) {
        switch (behavior) {
            case ATTACK_NORMAL:
                return DURATION_ATTACK;
            case ATTACK_UP:
                return DURATION_ATTACK;
            case ATTACK_DOWN:
                return DURATION_ATTACK;
            case CAST_SKILL:
                return DURATION_SKILL;
            case HEAL:
                return DURATION_HEAL;
            case DASH:
                return DURATION_DASH;
            case HURT:
                return DURATION_HURT;
            default:
                return DURATION_NONE;
        }
    }
}
