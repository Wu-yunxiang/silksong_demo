package com.example.math;

/**
 * 二维向量类（Vector2）。
 *
 * 说明：
 * - 表示二维空间中的向量或坐标点，包含 `x` 和 `y` 两个分量。
 * - 目前仅声明字段和两个简单构造函数，按你的要求不添加过多实现。
 */
public class Vector2 {
    /** x 分量 */
    public float x;

    /** y 分量 */
    public float y;

    /** 默认构造：(0,0) */
    public Vector2() {
        this.x = 0.0f;
        this.y = 0.0f;
    }

    /** 构造指定坐标 */
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /** 拷贝构造器 */
    public Vector2(Vector2 other) {
        this.x = other.x;
        this.y = other.y;
    }
}
