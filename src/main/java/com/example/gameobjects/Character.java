package com.example.gameobjects;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import com.example.math.Vector2;
import com.example.rendering.Renderer;

/**
 * 角色类。
 * 说明：
 * - 一个character有状态组件（血量能量两个object），有技能组件(一个object列表)。有基本信息含mode 
 * - 渲染的时候主体根据mode分类渲染，组件要渲染
 */

public class Character extends GameObject {
    // 基础字段
    private boolean isAlive = true;      // 存活状态
    private float modeDuration;          // mode持续时间
    private boolean isImmune;            // 是否处于免疫状态
    private float immunityTime;          // 免疫时间
    private List<Effect> effects;   // 拥有特效列表（owner）
    private Vector2 velocity;            // 速度
    private Vector2 acceleration;        // 加速度
    private Vector2 position;            // 位置

    private StaticObject.Health health;
    private StaticObject.Energy energy;
    private List<Skill> skills;
    private CharacterMode mode;
    private Appearance appearance;

    public Character() {
        this.effects = new ArrayList<>();
        this.velocity = new Vector2();
        this.acceleration = new Vector2();
        this.position = new Vector2();
        this.mode = CharacterMode.STANDING;
        this.appearance = new Appearance();
    }

    /**
     * 对外可见的角色模式枚举（用于渲染与逻辑判断）
     */
    public enum CharacterMode {
        STANDING,
        WALKING,
        DASHING,
        HEALING
    }
    
    @Override
    public void update(float deltaTime) {
        // update: 声明性覆盖，具体逻辑由后续填充
    }

    @Override
    public void render(Renderer renderer) {
        if (mode == CharacterMode.STANDING) {
            // 渲染站立状态
            renderer.drawCharacter(this);
        } else if (mode == CharacterMode.WALKING) {
            // 渲染行走状态
        } else if (mode == CharacterMode.DASHING) {
            // 渲染冲刺状态
        } else if (mode == CharacterMode.HEALING) {
            // 渲染回血状态
        }
    }
    
    public Appearance getAppearance() {
        return this.appearance;
    }
    
    public void setAppearance(Appearance appearance) {
        this.appearance = appearance;
    }

    /**
     * 角色外观：可用于控制身体各部位颜色与其他视觉参数
     */
    public static class Appearance {
        public enum Part {
            OUTLINE,
            SKIN,
            HAIR,
            BODY,
            ARM,
            HAND,
            LEG,
            WEAPON
        }

        public static class Color {
            public byte r, g, b, a;
            public Color(int r, int g, int b, int a) {
                this.r = (byte) r; this.g = (byte) g; this.b = (byte) b; this.a = (byte) a;
            }
        }

        private final Map<Part, Color> colors = new EnumMap<>(Part.class);
        private int version = 0;

        public Appearance() {
            // 默认外观
            colors.put(Part.OUTLINE, new Color(20, 20, 20, 255));
            colors.put(Part.SKIN, new Color(255, 205, 175, 255));
            colors.put(Part.HAIR, new Color(40, 20, 10, 255));
            colors.put(Part.BODY, new Color(50, 90, 200, 255));
            colors.put(Part.ARM, new Color(50, 90, 200, 255));
            colors.put(Part.HAND, new Color(255, 205, 175, 255));
            colors.put(Part.LEG, new Color(35, 60, 120, 255));
            colors.put(Part.WEAPON, new Color(220, 210, 190, 255));
        }

        public void setColor(Part part, Color color) {
            colors.put(part, color);
            version++;
        }

        public Color getColor(Part part) {
            return colors.get(part);
        }

        public int getVersion() {
            return version;
        }

    }
    
}
