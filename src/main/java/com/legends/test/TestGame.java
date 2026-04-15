package com.legends.test;

import com.legends.*;
import com.legends.core.*;
import com.legends.core.lichting.DirectionalLight;
import com.legends.entity.Entity;
import com.legends.entity.Model;
import com.legends.entity.Texture;
import com.legends.interfaces.ILogic;
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

    private float lightAngle;
    private DirectionalLight directionalLight;

    public TestGame() {
        this.renderer = new RenderManager();
        this.window = Main.getWindow();
        this.loader = new ObjectLoader();
        this.camera = new Camera();
        this.cameraInc = new Vector3f(0,0,0);
        lightAngle = -90;
    }



    @Override
    public void init() throws Exception {
        renderer.init();
        camera.setPosition(0, 1, 4f);


        Model model = loader.loadOBJModel("/models/red_panda_adult.obj");
        model.setTexture(new Texture(loader.loadTexture("/textures/animals/red_panda_adult.png")), 1f);
        entity = new Entity(model, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1);

        float lightIntensity = 0.0f;
        Vector3f lightPosition = new Vector3f(-1, -10, 0);
        Vector3f lightColor = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightPosition, lightColor, lightIntensity);
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
        //entity.incRotation(0.0f, 0.25f, 0.0f);

        lightAngle += 0.05f;
        if (lightAngle > 90) {
            directionalLight.setIntensity(0);
            if (lightAngle >= 360) {
                lightAngle = -90;
            }
        } else if (lightAngle <= -80 || lightAngle > 80) {
            float factor = 1 - (Math.abs(lightAngle) - 80) / 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        } else{
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().y = 1;
            directionalLight.getColor().z = 1;
        }

        double angleRad = Math.toRadians(lightAngle);
        directionalLight.getDirectiom().x = (float) Math.sin(angleRad);
        directionalLight.getDirectiom().y = (float) Math.cos(angleRad);

    }

    @Override
    public void render() {
        if (window.isResize()){
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.updateProjectionMatrix();
            window.setResize(true);
        }

        renderer.render(entity, camera, directionalLight);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
