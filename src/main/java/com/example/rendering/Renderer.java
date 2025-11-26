package com.example.rendering;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

import com.example.gameobjects.character.bodyparts.PixelMap;
import com.example.math.Vector2;
import com.example.scene.GameScene;

/**
 * 渲染器（Renderer）。
 *
 * 说明：
 * - 目前提供最基础的初始化与像素绘制接口，具体实现可继续扩展。
 */
public class Renderer {
    private long windowHandle;
    private GameScene scene;

    public Renderer(GameScene scene){
        this.scene = scene;
        windowHandle = GLFW.glfwCreateWindow(800, 600, "Journey Demo", MemoryUtil.NULL, MemoryUtil.NULL);
    }

        
    public void renderPixelMap(PixelMap pixels, Vector2 baseposition) {
        // TODO: 在指定位置绘制像素图
    }

    public void render(){
        scene.render();
    }

    public GameScene getScene() {
        return scene;
    }

    public void setScene(GameScene scene) {
        this.scene = scene;
    }

    public long getWindowHandle() {
        return windowHandle;
    }
}
