package com.example.gameobjects.character;

import com.example.gameobjects.character.Character.CharacterBehavior;
/**
 * CharacterConfig
 * 存储角色相关的配置常量
 */
public class CharacterConfig {
    // 物理常量
    public static final float GRAVITY = -3000.0f;
    public static final float WALK_SPEED = 450.0f;
    public static final float JUMP_VELOCITY = 1250.0f;
    public static final float DOUBLE_JUMP_VELOCITY = 1100.0f;
    public static final float DASH_SPEED = 1000.0f;

    // 游戏逻辑常量
    public static final int MAX_AIR_JUMPS = 2;
    public static final int MAX_DASHES = 1;
    public static final int MAX_HEALTH = 8;
    public static final int MAX_ENERGY = 12;
    public static final int skillStartActionNum = 3;
    public static final int skillCompleteActionNum = 5;
    public static final int flyingActionNum = 3;
    public static final int fallingActionNum = 4;

    // 回血
    public static final int HEAL_COST = 3;
    public static final int HEAL_AMOUNT = 1;

    //冲刺冷却时间 (秒)
    public static final float DASH_COOLDOWN = 0.1f;
    // 受伤后免疫时间 (秒)
    public static final float IMMUNITY_DURATION = 1.0f; 

    /**
     * 判断行为是否阻塞
     */
    public static boolean isBlocking(CharacterBehavior behavior) {
        switch (behavior) {
            case ATTACK_NORMAL:
            case ATTACK_UP:
            case ATTACK_DOWN:
            case CAST_SKILL:
            case HEAL:
            case DASH:
            case HURT:
                return true;
            default:
                return false;
        }
    }
}
