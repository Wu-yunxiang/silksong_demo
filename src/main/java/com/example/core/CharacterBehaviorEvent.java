package com.example.core;

/**
 * 人物行为事件枚举
 * 定义了由输入触发的各种角色行为
 */
public enum CharacterBehaviorEvent {
    MOVE_UP,
    MOVE_DOWN,
    MOVE_LEFT,
    MOVE_RIGHT,
    JUMP,
    DOUBLE_JUMP,
    DASH,
    ATTACK_NORMAL,
    ATTACK_UP,
    ATTACK_DOWN,
    CAST_SKILL, // 短按Q释放技能
    HEAL        // 长按Q回血，但是先实现不论长短都视为释放技能
}
