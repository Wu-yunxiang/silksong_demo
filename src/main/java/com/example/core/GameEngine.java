package com.example.core;

import org.lwjgl.glfw.GLFW;

import com.example.input.InputManager;
import com.example.rendering.Renderer;
import com.example.scene.GameScene;
import com.example.gameobjects.character.Character;

/**
 * 游戏引擎
 * 代表了整个游戏的持续运行
 */
public class GameEngine {
    Renderer renderer;
    long windowHandle;
    InputManager inputManager;
    boolean isRunning;
    
    private final int targetFPS = 60;
    public GameEngine(Renderer renderer){
        this.renderer = renderer;
    }

    public void initialize(){
        this.windowHandle = renderer.getWindowHandle();
        this.inputManager = InputManager.getInstance();
        this.isRunning = true;
        GameScene scene = renderer.getScene();
        scene.getGameObjects().add(new Character(scene));
        setupInput();
    }

    public void start(){
        long lastFrameTime = System.nanoTime();
        long frameTimeNanos = (long)(1_000_000_000.0 / targetFPS);
        while (isRunning) {
            long currentTime = System.nanoTime();
            
            if (currentTime - lastFrameTime >= frameTimeNanos) {
                GameLogic.processFrame((currentTime - lastFrameTime) / 1_000_000_000.0f,renderer.getScene());
                renderer.render();
                lastFrameTime = currentTime;
            }
            
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                isRunning = false;
            }
        }
    }

    private void setupInput(){
        GLFW.glfwSetKeyCallback(windowHandle, (windowHandle, key, scancode, action, mods) -> {
            if (action == GLFW.GLFW_PRESS) {
                inputManager.onKeyPressed(key);
            } else if (action == GLFW.GLFW_RELEASE) {
                inputManager.onKeyReleased(key);
            }
        });
        
        GLFW.glfwSetMouseButtonCallback(windowHandle, (windowHandle, button, action, mods) -> {
            if (action == GLFW.GLFW_PRESS) {
                inputManager.onMousePressed(button);
            } else if (action == GLFW.GLFW_RELEASE) {
                inputManager.onMouseReleased(button);
            }
        });
        
        GLFW.glfwSetCursorPosCallback(windowHandle, (windowHandle, xpos, ypos) -> {
            inputManager.onMouseMoved((int)xpos, (int)ypos);
        });
    }
}
