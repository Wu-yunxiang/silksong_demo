package com.example.gameobjects.character;

import com.example.gameobjects.GameObject;
import com.example.gameobjects.effect.Effect;
import com.example.gameobjects.skill.Skill;
import com.example.gameobjects.staticobject.Health;
import com.example.gameobjects.staticobject.Energy;
import com.example.gameobjects.character.bodyparts.BodyPart;
import com.example.gameobjects.character.bodyparts.Body;
import com.example.gameobjects.character.bodyparts.Face;
import com.example.gameobjects.character.bodyparts.Foot;
import com.example.gameobjects.character.bodyparts.Hair;
import com.example.gameobjects.character.bodyparts.Hand;
import com.example.gameobjects.character.bodyparts.Leg;
import com.example.math.Vector2;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色类 (Character Class)
 * 说明：
 * - 一个character有状态组件（血量能量两个object），有技能组件(一个object列表)。有基本信息含mode 
 * - 渲染的时候主体根据mode分类渲染，组件要渲染
 */
public class Character extends GameObject {
    // 基础字段
    private boolean isAlive = true;      // 存活状态 (Is Alive)
    private float modeDuration;          // mode持续时间 (Mode Duration)
    private boolean isImmune;            // 是否处于免疫状态 (Is Immune)
    private float immunityTime;          // 免疫时间 (Immunity Time)
    private List<Effect> effects;        // 拥有特效列表 (Effects List)
    private Vector2 velocity;            // 速度 (Velocity)
    private Vector2 acceleration;        // 加速度 (Acceleration)
    private Vector2 baseposition;        // 基准位置 (Base Position)

    private Health health;               // 血量 (Health)
    private Energy energy;               // 能量 (Energy)
    private List<Skill> skills;          // 技能列表 (Skills List)
    private CharacterMode mode;          // 角色模式 (Character Mode)
    
    private List<BodyPart> bodyParts;    // 身体部件列表 (Body Parts List)

    public Character() {
        this.effects = new ArrayList<>();
        this.velocity = new Vector2();
        this.acceleration = new Vector2();
        this.baseposition = new Vector2();
        this.mode = CharacterMode.STANDING;
        
        this.bodyParts = new ArrayList<>();
        this.bodyParts.add(new Hair());
        this.bodyParts.add(new Face());
        this.bodyParts.add(new Body());
        this.bodyParts.add(new Hand()); // Left Hand
        this.bodyParts.add(new Hand()); // Right Hand
        this.bodyParts.add(new Leg());  // Left Leg
        this.bodyParts.add(new Leg());  // Right Leg
        this.bodyParts.add(new Foot()); // Left Foot
        this.bodyParts.add(new Foot()); // Right Foot
    }

    public List<BodyPart> getBodyParts() {
        return bodyParts;
    }

    /**
     * 对外可见的角色模式枚举（用于渲染与逻辑判断）
     */
    public enum CharacterMode {
        STANDING, // 站立
        WALKING,  // 行走
        DASHING,  // 冲刺
        HEALING   // 治疗
    }
    
    public CharacterMode getMode() {
        return mode;
    }

    public void setMode(CharacterMode mode) {
        this.mode = mode;
    }
    
    @Override
    public void update(float deltaTime) {
        
    }

    @Override
    public void render() {
        if (mode == CharacterMode.STANDING) {
            // 渲染站立状态
        } else if (mode == CharacterMode.WALKING) {
            // 渲染行走状态
        } else if (mode == CharacterMode.DASHING) {
            // 渲染冲刺状态
        } else if (mode == CharacterMode.HEALING) {
            // 渲染回血状态
        }
    }
}
