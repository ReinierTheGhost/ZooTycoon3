package com.legends.test;

import com.legends.*;
import com.legends.entity.Model;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class TestGame implements ILogic {

    private int direction = 0;
    private float color = 0.0f;

    private final RenderManager renderer;
    private final WindowManager window;
    private final ObjectLoader loader;

    private Model model;

    public TestGame() {
        this.renderer = new RenderManager();
        this.window = Main.getWindow();
        this.loader = new ObjectLoader();
    }



    @Override
    public void init() throws Exception {
        renderer.init();

        float[] vertices = {
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,
                -0.5f, 0.5f, 0f
        };

        int[] indices = {
                0, 1, 3,
                3, 1, 2
        };

        model = loader.loadModel(vertices, indices);
    }

    @Override
    public void input() {
        if(window.isKeyPressed(GLFW_KEY_UP)){
            direction = 1;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            direction = -1;
        } else {
            direction = 0;
        }
    }

    @Override
    public void update() {
        color += direction * 0.01f;
        if(color > 1){
            color = 1.0f;
        }
        if (color < 0){
            color = 0.0f;
        }
    }

    @Override
    public void render() {
        if (window.isResize()){
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResize(true);
        }

        window.setClearColor(color, color, color, 0.0f);
        renderer.render(model);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
