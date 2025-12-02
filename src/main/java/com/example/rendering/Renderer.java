package com.example.rendering;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

import com.example.gameobjects.GameObject;
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
        windowHandle = GLFW.glfwCreateWindow((int)scene.SCREEN_WIDTH, (int)scene.SCREEN_HEIGHT, "Journey Demo", MemoryUtil.NULL, MemoryUtil.NULL);
    }

    public void render(){

        for (GameObject obj : scene.getGameObjects()) {
            
        }
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
