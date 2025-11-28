package com.example.math;

/**
 * 物理计算工具类 (PhysicsUtils)
 */
public class PhysicsUtils {

    /**
     * 更新物理状态
     * 根据公式:
     * 位移: s = v * t + 0.5 * a * t^2
     * 速度: v = v + a * t
     */
    public static void updatePhysicsState(Vector2 position, Vector2 velocity, Vector2 acceleration, float deltaTime) {
        // 计算位移: s = v * t + 0.5 * a * t^2
        float dx = velocity.x * deltaTime + 0.5f * acceleration.x * deltaTime * deltaTime;
        float dy = velocity.y * deltaTime + 0.5f * acceleration.y * deltaTime * deltaTime;

        // 更新位置
        position.setX(position.x + dx);
        position.setY(position.y + dy);

        // 更新速度: v = v + a * t
        velocity.setX(velocity.x + acceleration.x * deltaTime);
        velocity.setY(velocity.y + acceleration.y * deltaTime);
    }
}
