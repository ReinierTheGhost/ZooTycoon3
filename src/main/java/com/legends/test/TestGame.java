package com.legends.test;

import com.legends.*;
import com.legends.entity.Entity;
import com.legends.entity.Model;
import com.legends.entity.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static com.legends.utils.Constants.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class TestGame implements ILogic {



    private final RenderManager renderer;
    private final WindowManager window;
    private final ObjectLoader loader;

    private Entity entity;
    private Camera camera;

    Vector3f cameraInc;

    public TestGame() {
        this.renderer = new RenderManager();
        this.window = Main.getWindow();
        this.loader = new ObjectLoader();
        this.camera = new Camera();
        this.cameraInc = new Vector3f(0,0,0);
    }



    @Override
    public void init() throws Exception {
        renderer.init();
        camera.setPosition(0, 0, 2.5f);


        Model model = loader.loadOBJModel("/models/red_panda_adult.obj");
        model.setTexture(new Texture(loader.loadTexture("/textures/animals/red_panda_adult.png")));
        entity = new Entity(model, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1);
    }

    @Override
    public void input() {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W))
            cameraInc.z = -1;
        if (window.isKeyPressed(GLFW_KEY_S))
            cameraInc.z = 1;

        if (window.isKeyPressed(GLFW_KEY_A))
            cameraInc.x = -1;
        if (window.isKeyPressed(GLFW_KEY_D))
            cameraInc.x = 1;

        if (window.isKeyPressed(GLFW_KEY_Z))
            cameraInc.y = -1;
        if (window.isKeyPressed(GLFW_KEY_X))
            cameraInc.y = 1;

    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        camera.movePosition(cameraInc.x * CAMERA_MOVE_SPEED, cameraInc.y* CAMERA_MOVE_SPEED, cameraInc.z* CAMERA_MOVE_SPEED);

        if(mouseInput.isRightButtonPressed()){
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }
        entity.incRotation(0.0f, 0.5f, 0.0f);
    }

    @Override
    public void render() {
        if (window.isResize()){
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.updateProjectionMatrix();
            window.setResize(true);
        }

        window.setClearColor(0.0f,0.0f,0.0f, 0.0f);
        renderer.render(entity, camera);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
