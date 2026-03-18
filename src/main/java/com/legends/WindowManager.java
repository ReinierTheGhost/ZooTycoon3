package com.legends;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static com.legends.utils.Constants.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memFree;

public class WindowManager {


    private final String title;
    private final Matrix4f projectionMatrix;

    private int width, height;
    private long window;

    private boolean resize, vSync;

    public WindowManager(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
        projectionMatrix = new Matrix4f();
    }

    public void init(){
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Basic window hints
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        boolean maximized = false;
        if (width == 0 || height == 0){
            width = 100;
            height = 100;
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
            maximized = true;
        }

        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");
        setWindowIcon(window);

        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.setResize(true);
        });
        // ESC = close
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });

        // Center window
        if (maximized){
            glfwMaximizeWindow(window);
        } else {
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);
        }

        glfwMakeContextCurrent(window);
        if (isvSync())
            glfwSwapInterval(1); // vsync
        glfwShowWindow(window);

        // Create OpenGL capabilities *after* making context current
        GL.createCapabilities();

        glClearColor(0f, 0f, 0f, 1f);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);

        glViewport(0, 0, width, height);
        updateProjectionMatrix();
        setResize(false);
//        glEnable(GL_CULL_FACE);
//        glCullFace(GL_BACK);
    }

    public void update() {
        glfwSwapBuffers(window);
    }

    public void pollEvents() {
        glfwPollEvents();
    }

    public void cleanup(){
        glfwDestroyWindow(window);
    }

    public void setClearColor(float r, float g, float b, float a){
        glClearColor(r, g, b, a);
    }

    public boolean isKeyPressed(int keyCode){
        return glfwGetKey(window, keyCode) == GLFW_PRESS;
    }

    public boolean windowShouldClose(){
        return glfwWindowShouldClose(window);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title){
        glfwSetWindowTitle(window, title);
    }

    public boolean isvSync() {
        return vSync;
    }

    public void setvSync(boolean vSync) {
        this.vSync = vSync;
    }

    public boolean isResize() {
        return resize;
    }

    public void setResize(boolean resize) {
        this.resize = resize;
    }

    private static void setWindowIcon(long window) {
        // PNGs in resources (classpath). Voeg toe/verwijder wat je wil.
        String[] paths = {
                "/icons/zt2ult.png"
        };

        try (MemoryStack stack = stackPush()) {
            GLFWImage.Buffer icons = GLFWImage.malloc(paths.length, stack);

            // We bewaren de raw pixel buffers om ze na glfwSetWindowIcon weer vrij te geven
            ByteBuffer[] pixelBuffers = new ByteBuffer[paths.length];

            for (int i = 0; i < paths.length; i++) {
                ImageData img = loadIconRGBA(paths[i]);
                pixelBuffers[i] = img.pixels;

                icons.position(i);
                icons.width(img.width);
                icons.height(img.height);
                icons.pixels(img.pixels);
            }
            icons.position(0);

            glfwSetWindowIcon(window, icons);

            // STBImage levert native memory; die moet je vrijgeven
            for (ByteBuffer p : pixelBuffers) {
                if (p != null) stbi_image_free(p);
            }
        }
    }

    private static ImageData loadIconRGBA(String resourcePath) {
        try (InputStream is = WindowManager.class.getResourceAsStream(resourcePath)) {
            if (is == null) throw new RuntimeException("Icon not found on classpath: " + resourcePath);

            byte[] bytes = is.readAllBytes();
            ByteBuffer fileData = MemoryUtil.memAlloc(bytes.length);
            fileData.put(bytes).flip();

            try (MemoryStack stack = stackPush()) {
                var w = stack.mallocInt(1);
                var h = stack.mallocInt(1);
                var comp = stack.mallocInt(1);

                // Force RGBA
                ByteBuffer pixels = stbi_load_from_memory(fileData, w, h, comp, 4);
                if (pixels == null) {
                    throw new RuntimeException("Failed to load icon " + resourcePath + ": " + stbi_failure_reason());
                }
                return new ImageData(w.get(0), h.get(0), pixels);
            } finally {
                memFree(fileData);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed reading icon: " + resourcePath, e);
        }
    }

    private record ImageData(int width, int height, ByteBuffer pixels) {}


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getWindow() {
        return window;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f updateProjectionMatrix(){
        float aspectRatio = (float) width / height;
        return projectionMatrix.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
    }

    public Matrix4f updateProjectionMatrix(Matrix4f matrix, int width, int height){
        float aspectRatio = (float) width / height;
        return matrix.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
    }
}
