package com.example.gameobjects;

import com.example.rendering.Renderer;

/**
 * 特效基类（抽象）。
 *
 * 说明：
 * - 用于表现视觉或短暂机制效果（如冲刺特效、回血特效、跳跃特效等）。
 * - 暂不在此处添加实现，等待你的进一步设计。
 */
public abstract class Effect extends GameObject {

    /**
     * 二段跳特效。
     *
     * 说明：
     * - 当角色触发二段跳时显示的视觉/特效（例如起跳时的空气粒子、音效或浮动光环）。
     */
    public static class DoubleJumpEffect extends Effect {

        @Override
        public void update(float deltaTime) {
            // 二段跳特效更新（占位）
        }

        @Override
        public void render(Renderer renderer) {
            // 二段跳特效渲染（占位）
        }
    }

    /**
     * 回血成功特效。
     *
     * 说明：
     * - 当角色在地面或通过技能/道具恢复生命值并完成回血后显示的特效（例如闪光、恢复数字、绿光环等）。
     */
    public static class HealSuccessEffect extends Effect {

        @Override
        public void update(float deltaTime) {
            // 回血成功特效更新（占位）
        }

        @Override
        public void render(Renderer renderer) {
            // 回血成功特效渲染（占位）
        }
    }

    /**
     * 命中特效（被批中/攻击命中时的特效）。
     *
     * 说明：
     * - 表示当一次攻击命中对方时播放的短时视觉或音效（例如冲击波、闪烁或命中数值飘起）。
     */
    public static class AttackHitEffect extends Effect {

        @Override
        public void update(float deltaTime) {
            // 攻击命中特效更新（占位）
        }

        @Override
        public void render(Renderer renderer) {
            // 攻击命中特效渲染（占位）
        }
    }

    /**
     * 击杀特效（击败/击杀对手时的特效）。
     *
     * 说明：
     * - 当一名角色成功击败对手时触发的视觉或音效（例如爆发特效、奖励粒子、比分变动提示等）。
     */
    public static class KillEffect extends Effect {

        @Override
        public void update(float deltaTime) {
            // 击杀特效更新（占位）
        }

        @Override
        public void render(Renderer renderer) {
            // 击杀特效渲染（占位）
        }
    }
}
