package com.example.gameobjects;

import com.example.rendering.Renderer;

/**
 * 游戏对象基类（抽象）。
 *
 * 说明：
 * - 本类为游戏中所有对象的共同祖先（例如地形、特效、角色、怪物等）。
 * - 提供每个对象应实现的生命周期方法（声明）：
 *     - update(float deltaTime)
 *     - render(Renderer renderer)
 * - 这里只声明抽象方法，具体实现由子类提供。
 */
public abstract class GameObject {
	public Renderer renderer;
	public boolean isRendered;
	/**
	 * 每帧更新对象状态（逻辑层调用）
	 * @param deltaTime 时间增量（秒）
	 */
	public abstract void update(float deltaTime);

	/**
	 * 渲染回调（由渲染器/渲染层调用）
	 * @param renderer 渲染器接口
	 */
	public abstract void render();
}
