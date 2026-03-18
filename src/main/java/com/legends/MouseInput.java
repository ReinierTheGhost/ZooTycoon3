package com.legends;

import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {
    private final Vector2f priviousPosition, curentPosition;
    private final Vector2f displVec;

    private boolean inWindow = false, leftButtonPressed = false, rightButtonPressed = false;

    public MouseInput(){
        priviousPosition = new Vector2f(-1, -1);
        curentPosition = new Vector2f(0, 0);
        displVec = new Vector2f();
    }

    public void init(){
        glfwSetCursorPosCallback(Main.getWindow().getWindow(), (window, xpos, ypos) -> {
            curentPosition.x = (float) xpos;
            curentPosition.y = (float) ypos;
        });

        glfwSetCursorEnterCallback(Main.getWindow().getWindow(), (window, entered) -> {
            inWindow = entered;
        });

        glfwSetMouseButtonCallback(Main.getWindow().getWindow(), (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_1) {
                leftButtonPressed = action == GLFW_PRESS;
            }
            if (button == GLFW_MOUSE_BUTTON_2) {
                rightButtonPressed = action == GLFW_PRESS;
            }
        });
    }

    public void input(){
        displVec.x = 0;
        displVec.y = 0;
        if (priviousPosition.x > 0 && priviousPosition.y > 0 && inWindow) {
            double x = curentPosition.x - priviousPosition.x;
            double y = curentPosition.y - priviousPosition.y;
            boolean rotateX = x != 0;
            boolean rotateY = y != 0;
            if (rotateX)
                displVec.y = (float) x;
            if (rotateY)
                displVec.x = (float) y;
        }
        priviousPosition.x = curentPosition.x;
        priviousPosition.y = curentPosition.y;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }

    public Vector2f getDisplVec() {
        return displVec;
    }
}
