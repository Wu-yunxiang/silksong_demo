package com.example;

import com.example.rendering.Renderer;
import com.example.core.GameEngine;
import com.example.scene.GameScene;

public class Game {
    public static void main(String[] args) {
        GameScene scene = new GameScene();
        Renderer renderer = new Renderer(scene);
        GameEngine gameEngine = new GameEngine(renderer);
        gameEngine.initialize();
        gameEngine.start();
    }
}