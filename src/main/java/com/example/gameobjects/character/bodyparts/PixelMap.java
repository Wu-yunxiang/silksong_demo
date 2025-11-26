package com.example.gameobjects.character.bodyparts;

import java.util.List;

public class PixelMap {
    /**
     * 像素数据 (Pixel Data)
     */
    public static class PixelData {
        public int relativeX;
        public int relativeY;
        public int r, g, b, a;
    }

    public List<PixelData> pixels;
}
