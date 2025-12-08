package com.example.gameobjects;

import com.example.scene.GameScene;

/**
 * 游戏对象基类（抽象）。
 */
public abstract class GameObject {
	public abstract void update(float deltaTime, GameScene scene);
}
