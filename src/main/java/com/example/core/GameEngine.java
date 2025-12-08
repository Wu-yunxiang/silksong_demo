package com.example.core;

import org.lwjgl.glfw.GLFW;

import com.example.input.InputManager;
import com.example.pictureconfig.CharacterConfigLoader;
import com.example.renderer.Renderer;
import com.example.scene.GameScene;
import com.example.Game;
import com.example.gameobjects.character.Character;
import com.example.gameobjects.character.CharacterConfig;

/**
 * 游戏引擎
 * 代表了整个游戏的持续运行
 */
public class GameEngine {
    private GameScene scene;
    private InputManager inputManager;
    private boolean isRunning;
    
    private final int targetFPS = 60;

    public GameEngine(GameScene scene){
        this.scene = scene;
    }

    public void initialize(){
        this.isRunning = false;
        this.inputManager = InputManager.getInstance();
        setupInput();
        CharacterConfigLoader.loadCharacterConfigs();
        scene.getGameObjects().add(new Character(scene));
    }

    public void start(){
        this.isRunning = true;
        long lastFrameTime = System.nanoTime();
        long frameTimeNanos = (long)(1_000_000_000.0 / targetFPS);
        while (isRunning) {
            long currentTime = System.nanoTime();
            
            if (currentTime - lastFrameTime >= frameTimeNanos) {
                GameLogic.processFrame((currentTime - lastFrameTime) / 1_000_000_000.0f,renderer.getScene());
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
