package com.example.math;

/**
 * 矩形类 (Rect)
 * 用于碰撞检测和范围判定
 */
public class Rect {
    public float x, y; //在界面里的左下角坐标
    public float width, height; 

    public Rect(float x, float y, float width, float height) { 
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rect() {
        this(0, 0, 0, 0);
    }

    public void set(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean intersects(Rect other) {
        return this.x <= other.x + other.width &&
               this.x + this.width >= other.x &&
               this.y <= other.y + other.height &&
               this.y + this.height >= other.y;
    }
}
