package com.example.gameobjects;
import com.example.rendering.Renderer;
/**
 * 地形基类（抽象）。
 *
 * 说明：
 * - 地形类用于表示游戏中的不可移动或半固定的地表与障碍（如平地、地刺等）。
 * - 目前仅声明类继承关系，具体字段与行为按你的后续指示补充。
 */
public abstract class Terrain extends GameObject {

    /**
     * 平地（地形子类）。
     *
     * 说明：
     * - 表示普通可行走的地面。
     */
        public static class FlatTerrain extends Terrain {

            @Override
            public void update(float deltaTime) {
                // 平地地形更新（占位）
            }

            @Override
            public void render(Renderer renderer) {
                // 平地地形渲染（占位）
            }
        }

    /**
     * 地刺（地形子类）。
     *
     * 说明：
     * - 表示带有伤害或危险效果的地形，如玩家接触会受到惩罚的尖刺区域。
     */
    public static class SpikeTerrain extends Terrain {
        // 左下角坐标（像素或单位坐标系）——表示地刺区域的起始点：x 为横向，y 为纵向
        private int leftX;
        private int leftY;

        // 地刺高度（单位同上）——每根刺从地面向上的高度
        private int spikeHeight;

        // 每根刺的宽度——表示单个刺在横向上的占用宽度
        private int spikeWidth;

        // 刺的个数（非负整数）——表示在水平线上一排刺的数量
        private int spikeCount;
        @Override
        public void update(float deltaTime) {
            // 地刺逻辑每帧更新（占位）
        }

        @Override
        public void render(Renderer renderer) {
            // 地刺渲染（占位）
        }
    }
}
