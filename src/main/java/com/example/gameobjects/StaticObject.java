package com.example.gameobjects;
import com.example.rendering.Renderer;
/**
 * 静态对象基类（抽象）。
 *
 * 说明：
 * - 表示属于 `GameObject` 的静态显示/状态类（例如血量、能量等属于 UI/状态显示的对象），
 *   它们通常不随角色物理移动直接位移，而是在界面或场景中固定显示或共享状态。
 * - 本文件同时包含 `Health`（血量）和 `Energy`（能量）两个子类的声明，均保持为空类体，
 *   具体实现与字段将由你后续逐步指定。
 */
public abstract class StaticObject extends GameObject {

    /**
     * 血量（静态对象子类）。
     *
     * 说明：
     * - 表示游戏中的血量显示或血量状态对象。
     */
    public static class Health extends StaticObject {

        @Override
        public void update(float deltaTime) {
            // 血量对象更新（占位）
        }

        @Override
        public void render(Renderer renderer) {
            // 血量对象渲染（占位）
        }
    }

    /**
     * 能量（静态对象子类）。
     *
     * 说明：
     * - 表示游戏中的能量/能量条显示或状态对象。
     */
    public static class Energy extends StaticObject {

        @Override
        public void update(float deltaTime) {
            // 能量对象更新（占位）
        }

        @Override
        public void render(Renderer renderer) {
            // 能量对象渲染（占位）
        }
    }
}
