package com.example.pictureconfig;

public class GameSceneConfig {
    public static final int ScreenWidth = 1693; //游戏窗口尺寸硬编码
    public static final int ScreenHeight = 952;
    public static final int GroundWidth = 162; //地面块尺寸硬编码
    public static final int GroundHeight = 114; 
    public static final int firstSpikesGroundIndex = 3; //第一个有尖刺的地板索引
    public static final int secondSpikesGroundIndex = 8; //第二个有尖刺的地板索引
    public static final int initialCharacterPositionX = GroundWidth; //角色初始位置X
    //角色初始位置Y（中心y和脚底y之差 + GroundHeight）
    public static final int initialCharacterPositionY = 100 + GroundHeight; 

    private GameSceneConfig() {
    }
}
