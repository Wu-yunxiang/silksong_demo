package com.example;

import com.example.core.GameEngine;
import com.example.scene.GameScene;

public class Game {
    public static void main(String[] args) {
        GameScene scene = new GameScene();
        GameEngine gameEngine = new GameEngine(scene);
        gameEngine.initialize();
        gameEngine.start();
    }
}