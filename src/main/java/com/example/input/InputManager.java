package com.example.input;

/**
 
* 输入管理器（InputManager）。
 *
 * 说明：
 * - 负责收集并统一管理来自键盘、鼠标、手柄等输入设备的事件。
 * - 当前仅声明类和中文注释，example按你的要求暂不实现字段或方法。
 */
import com.example.math.Vector2;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InputManager {
    private static InputManager instance;//所有inputmanager实例的共有指针
    private Set<Integer> pressedKeys;
    private Set<Integer> justPressedKeys;
    private Map<Integer, Boolean> keyStates;
    private Vector2 mousePosition;
    private boolean[] mouseButtons;
    private boolean[] mouseButtonsJustPressed;
    
    private InputManager() {
        pressedKeys = new HashSet<>();
        justPressedKeys = new HashSet<>();
        keyStates = new HashMap<>();
        mousePosition = new Vector2();
        mouseButtons = new boolean[3];
        mouseButtonsJustPressed = new boolean[3];
    }
    
    public static InputManager getInstance() {
        if (instance == null) {
            instance = new InputManager();
        }
        return instance;
    }
    
    public void update() {
        justPressedKeys.clear();
        for (int i = 0; i < mouseButtonsJustPressed.length; i++) {
            mouseButtonsJustPressed[i] = false;
        }
    }
    
    public void onKeyPressed(int keyCode) {
        if (!pressedKeys.contains(keyCode)) {
            justPressedKeys.add(keyCode);
        }
        pressedKeys.add(keyCode);
        keyStates.put(keyCode, true);
    }
    
    public void onKeyReleased(int keyCode) {
        pressedKeys.remove(keyCode);
        keyStates.put(keyCode, false);
    }
    
    public void onMouseMoved(float x, float y) {
        mousePosition.x = x;
        mousePosition.y = y;
    }
    
    public void onMousePressed(int button) {
        if (button >= 0 && button < mouseButtons.length) {
            if (!mouseButtons[button]) {
                mouseButtonsJustPressed[button] = true;
            }
            mouseButtons[button] = true;
        }
    }
    
    public void onMouseReleased(int button) {
        if (button >= 0 && button < mouseButtons.length) {
            mouseButtons[button] = false;
        }
    }
    
    public boolean isKeyPressed(int keyCode) {
        return pressedKeys.contains(keyCode);
    }
    
    public boolean isKeyJustPressed(int keyCode) {
        return justPressedKeys.contains(keyCode);
    }
    
    public boolean isMouseButtonPressed(int button) {
        if (button >= 0 && button < mouseButtons.length) {
            return mouseButtons[button];
        }
        return false;
    }
    
    public boolean isMouseButtonJustPressed(int button) {
        if (button >= 0 && button < mouseButtons.length) {
            return mouseButtonsJustPressed[button];
        }
        return false;
    }
    
    public boolean isAnyKeyJustPressed() {
        return !justPressedKeys.isEmpty();
    }
    
    public boolean isAnyKeyPressed() {
        return !pressedKeys.isEmpty();
    }

    public java.util.Set<Integer> getJustPressedKeysSnapshot() {
        return new java.util.HashSet<>(justPressedKeys);
    }
    
    public Vector2 getMousePosition() {
        return new Vector2(mousePosition);
    }
    
    public float getMouseX() {
        return mousePosition.x;
    }
    
    public float getMouseY() {
        return mousePosition.y;
    }
}
