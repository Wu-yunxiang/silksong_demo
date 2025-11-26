package com.example.gameobjects;

import com.example.rendering.Renderer;

/**
 * 技能基类。
 *
 * 说明：
 * - 表示角色释放的技能实体（如火球、斩击波等）。
 * - 继承自 GameObject，因为技能通常有位置、碰撞和生命周期。
 */
public abstract class Skill extends GameObject {
    // 技能逻辑待实现
    
    /**
     * 黑波技能（示例具体技能）。
     * 说明：名称为 “黑波”，作为游戏中某个具体技能实体。
     *       下面覆盖了 update 与 render 方法，当前为占位实现。
     */
    public static class BlackWave extends Skill {

        @Override
        public void update(float deltaTime) {
            // 黑波技能每帧更新（占位）
        }

        @Override
        public void render(Renderer renderer) {
            // 黑波技能渲染（占位）
        }
    }
}
