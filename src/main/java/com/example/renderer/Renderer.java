package com.example.renderer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.stb.STBImage;

import com.example.gameobjects.GameObject;
import com.example.pictureconfig.GameSceneConfig;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

/**
 * 渲染器（Renderer）。
 */
 public class Renderer {
    private static long window = MemoryUtil.NULL;
    // Background texture
    private static int bgTextureId = 0;
    private static int bgWidth = 0;
    private static int bgHeight = 0;
    // Ground / Spike textures
    private static int groundFullTexId = 0;
    private static int groundFullW = 0;
    private static int groundFullH = 0;
    private static int groundHalfTexId = 0;
    private static int groundHalfH = 0;
    private static int spikeTexId = 0;
    private static int spikeW = 0;
    private static int spikeH = 0;
 
     private Renderer() {}
 
     /**
      * 初始化 GLFW 窗口与 OpenGL 上下文，窗口大小使用 GameSceneConfig 中的常量。
      */
     public static void initial() {
         GLFWErrorCallback.createPrint(System.err).set();
 
         if (!GLFW.glfwInit()) {
             throw new IllegalStateException("Unable to initialize GLFW");
         }
 
         GLFW.glfwDefaultWindowHints();
         GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
 
         int width = GameSceneConfig.ScreenWidth;
         int height = GameSceneConfig.ScreenHeight;
 
         window = GLFW.glfwCreateWindow(width, height, "Journey Demo", MemoryUtil.NULL, MemoryUtil.NULL);
         if (window == MemoryUtil.NULL) {
             throw new RuntimeException("Failed to create GLFW window");
         }
 
         GLFW.glfwMakeContextCurrent(window);
         GLFW.glfwShowWindow(window);
 
         // 创建 OpenGL 能力
         GL.createCapabilities();
 
         // 设定视口和正交投影（像素坐标，原点在左下）
         GL11.glViewport(0, 0, width, height);
         GL11.glMatrixMode(GL11.GL_PROJECTION);
         GL11.glLoadIdentity();
         GL11.glOrtho(0, width, 0, height, -1, 1);
         GL11.glMatrixMode(GL11.GL_MODELVIEW);
         GL11.glLoadIdentity();
 
         GL11.glEnable(GL11.GL_BLEND);
         GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
 
         GL11.glClearColor(0f, 0f, 0f, 1f);
     }

    /**
     * 加载纹理并返回 {texId, width, height}
     */
    public static int[] loadTextureInfoFromClasspath(String resourcePath) {
        STBImage.stbi_set_flip_vertically_on_load(true);
        try (InputStream in = Renderer.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                System.err.println("Resource not found: " + resourcePath);
                return new int[]{0,0,0};
            }
            byte[] bytes = in.readAllBytes();
            ByteBuffer imageBuffer = MemoryUtil.memAlloc(bytes.length);
            imageBuffer.put(bytes).flip();

            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer comp = stack.mallocInt(1);

                ByteBuffer image = STBImage.stbi_load_from_memory(imageBuffer, w, h, comp, 4);
                if (image == null) {
                    System.err.println("Failed to load image: " + STBImage.stbi_failure_reason());
                    return new int[]{0,0,0};
                }

                int texId = GL11.glGenTextures();
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, w.get(0), h.get(0), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);

                STBImage.stbi_image_free(image);
                return new int[]{texId, w.get(0), h.get(0)};
            } finally {
                MemoryUtil.memFree(imageBuffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new int[]{0,0,0};
        }
    }

    private static void drawTexturedQuad(int texId, float x, float y, float width, float height) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
        GL11.glBegin(GL11.GL_QUADS);
        drawQuadVertices(x, y, width, height);
        GL11.glEnd();
    }

    /**
     * 输出一个四边形的纹理坐标与顶点（不包含 glBegin/glEnd/绑定），便于批量绘制时复用。
     */
    private static void drawQuadVertices(float x, float y, float width, float height) {
        GL11.glTexCoord2f(0f, 0f); GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(1f, 0f); GL11.glVertex2f(x + width, y);
        GL11.glTexCoord2f(1f, 1f); GL11.glVertex2f(x + width, y + height);
        GL11.glTexCoord2f(0f, 1f); GL11.glVertex2f(x, y + height);
    }
 
     /**
      * 渲染一帧
      */
    public static void render(List<GameObject> gameObjects) {
        if (window == MemoryUtil.NULL) {
            System.err.println("Renderer not initialized. Call initial() first.");
            return;
        }

        // 首次渲染时加载背景、地面与地刺纹理（只加载一次）
        if (bgTextureId == 0) {
            // Background
            int[] bg = loadTextureInfoFromClasspath("/Background_Standardized_Final/1.png");
            bgTextureId = bg[0];bgWidth = bg[1];bgHeight = bg[2];
            if (bgTextureId == 0) System.err.println("Background texture load failed");

            // Ground full (single tile)
            int[] gFull = loadTextureInfoFromClasspath("/Ground_Standardized_Final/1.png");
            groundFullTexId = gFull[0]; groundFullW = gFull[1]; groundFullH = gFull[2];
            if (groundFullTexId == 0) System.err.println("Ground full texture load failed");

            // Ground half (half tile)
            int[] gHalf = loadTextureInfoFromClasspath("/Ground_Standardized_Final/2.png");
            groundHalfTexId = gHalf[0]; groundHalfH = gHalf[2];
            if (groundHalfTexId == 0) System.err.println("Ground half texture load failed");

            // Spike single image
            int[] s = loadTextureInfoFromClasspath("/Spike_Standardized_Final/1.png");
            spikeTexId = s[0]; spikeW = s[1]; spikeH = s[2];
            if (spikeTexId == 0) System.err.println("Spike texture load failed");
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        // 绘制 background、ground 与 spikes：屏幕底部按 GroundWidth 填充，每个块高度 GroundHeight
        if (bgTextureId != 0 && groundFullTexId != 0 && groundHalfTexId != 0 && spikeTexId != 0) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            drawTexturedQuad(bgTextureId, 0f, 0f, bgWidth, bgHeight);//绘制背景

            int blocks =  (int) Math.ceil((double) GameSceneConfig.ScreenWidth / groundFullW);
            for (int i = 1; i <= blocks; i++) {
                int x = (i - 1) * groundFullW;
                // spike blocks (从 1 开始编号)int
                if (i == GameSceneConfig.firstSpikesGroundIndex || i == GameSceneConfig.secondSpikesGroundIndex) {
                    // 下半部分使用半块地面图（在下方绘制半块地面）
                    drawTexturedQuad(groundHalfTexId, x, 0f, groundFullW, groundHalfH);

                    // 裁剪到上半部分并绘制地刺（确保地刺不会画到下方）
                    GL11.glEnable(GL11.GL_SCISSOR_TEST);
                    GL11.glScissor(x, groundHalfH, groundFullW, spikeH);

                    // 绑定 spike 纹理并一次性绘制多个相邻 spike
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, spikeTexId);
                    int spikesCount = (int) Math.ceil((double) groundFullW / spikeW);
                    GL11.glBegin(GL11.GL_QUADS);
                    for (int j = 0; j < spikesCount; j++) {
                        float sx = x + j * spikeW;
                        float sy = groundHalfH;
                        drawQuadVertices(sx, sy, spikeW, spikeH);
                    }

                    GL11.glEnd();
                    GL11.glDisable(GL11.GL_SCISSOR_TEST);
                } else {
                    // 普通完整地块
                    drawTexturedQuad(groundFullTexId, x, 0f, groundFullW, groundFullH);
                }
            }

            for(GameObject obj : gameObjects) { //渲染游戏对象
                obj.render();
            }

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }

        GLFW.glfwSwapBuffers(window);
    }
 
    public static long getWindowHandle() {
        return window;
    }
     
    public static boolean windowShouldClose() {
        return window != MemoryUtil.NULL && GLFW.glfwWindowShouldClose(window);
    }
 
    public static void cleanup() {
        if (bgTextureId != 0) {
            GL11.glDeleteTextures(bgTextureId);
            bgTextureId = 0;
        }
        if (groundFullTexId != 0) {
            GL11.glDeleteTextures(groundFullTexId);
            groundFullTexId = 0;
        }
        if (groundHalfTexId != 0) {
            GL11.glDeleteTextures(groundHalfTexId);
            groundHalfTexId = 0;
        }
        if (spikeTexId != 0) {
            GL11.glDeleteTextures(spikeTexId);
            spikeTexId = 0;
        }
        if (window != MemoryUtil.NULL) {
            GLFW.glfwDestroyWindow(window);
            window = MemoryUtil.NULL;
        }

        GLFW.glfwTerminate();
    }
 }
