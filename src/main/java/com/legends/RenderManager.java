package com.legends;

import com.legends.entity.Model;
import com.legends.utils.Utils;

import static org.lwjgl.opengl.GL30.*;

public class RenderManager {
    private final WindowManager window;
    private ShaderManager shader;

    public RenderManager(){
        window = Main.getWindow();
    }

    public void init() throws Exception{
        shader = new ShaderManager();
        shader.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
        shader.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
        shader.link();
    }

    public void render(Model model){
        clear();
        shader.bind();
        glBindVertexArray(model.getId());
        glEnableVertexAttribArray(0);
        glDrawArrays(GL_TRIANGLES, 0, model.getVertexCount());
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        shader.unbind();
    }

    public void clear(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup(){
        shader.cleanup();
    }
}
