package com.example.core;

import org.lwjgl.glfw.GLFW;

import com.example.input.InputManager;
import com.example.pictureconfig.*;
import com.example.renderer.Renderer;
import com.example.scene.GameScene;
import com.example.gameobjects.character.Character;

/**
 * 游戏引擎
 * 代表了整个游戏的持续运行
 */
public class GameEngine {
    private GameScene scene;
    private InputManager inputManager;
    private boolean isRunning;
    
    public static final int TARGET_FPS = 60;

    public GameEngine(GameScene scene){
        this.scene = scene;
    }

    public void initialize(){
        this.isRunning = false;
        Renderer.initial(); 
        Renderer.render(scene.getGameObjects()); 
        CharacterConfigLoader.loadCharacterConfigs();
        PurpleDragonConfigLoader.loadPurpleDragonConfig();
        this.inputManager = InputManager.getInstance();
    }

    public void start(){
        this.isRunning = true;
        scene.getGameObjects().add(new Character(scene));
        setupInput();
        long lastFrameTime = System.nanoTime();
        long frameTimeNanos = (long)(1_000_000_000.0 / TARGET_FPS);
        while (isRunning) {
            long currentTime = System.nanoTime();
            
            if (currentTime - lastFrameTime >= frameTimeNanos) {
                GameLogic.processFrame((currentTime - lastFrameTime) / 1_000_000_000.0f, scene);
                scene.render();
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
        GLFW.glfwSetKeyCallback(scene.getWindowHandle(), (windowHandle, key, scancode, action, mods) -> {
            if (action == GLFW.GLFW_PRESS) {
                inputManager.onKeyPressed(key);
            } else if (action == GLFW.GLFW_RELEASE) {
                inputManager.onKeyReleased(key);
            }
        });
        
        GLFW.glfwSetMouseButtonCallback(scene.getWindowHandle(), (windowHandle, button, action, mods) -> {
            if (action == GLFW.GLFW_PRESS) {
                inputManager.onMousePressed(button);
            } else if (action == GLFW.GLFW_RELEASE) {
                inputManager.onMouseReleased(button);
            }
        });
        
        GLFW.glfwSetCursorPosCallback(scene.getWindowHandle(), (windowHandle, xpos, ypos) -> {
            inputManager.onMouseMoved((int)xpos, (int)ypos);
        });
    }
}
