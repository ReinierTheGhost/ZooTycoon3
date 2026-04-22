package com.legends.core;

import com.legends.Main;
import com.legends.core.lichting.DirectionalLight;
import com.legends.core.lichting.PointLight;
import com.legends.entity.Entity;
import com.legends.utils.Constants;
import com.legends.utils.Transformation;
import com.legends.utils.Utils;

import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
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
        shader.createUniform("textureSampler");
        shader.createUniform("transformationMatrix");
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
        shader.createUniform("ambientLight");
        shader.createMaterialUniform("material");
        shader.createUniform("specularPower");
        shader.createDirectionalLightUniform("directionalLight");
        shader.createUniform("pointLight");
    }

    public void render(Entity entity, Camera camera, DirectionalLight directionalLight, PointLight pointLight){
        clear();
        shader.bind();
        shader.setUniform("textureSampler", 0);
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(entity));
        shader.setUniform("projectionMatrix", window.getProjectionMatrix());
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
        shader.setUniform("material", entity.getModel().getMaterial());
        shader.setUniform("ambientLight", Constants.AMBIENT_LIGHT);
        shader.setUniform("specularPower", Constants.SPECULAR_POWER);
        shader.setUniform("directionalLight", directionalLight);
        shader.setUniform("pointLight", pointLight);

        glBindVertexArray(entity.getModel().getId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, entity.getModel().getMaterial().getTexture().getId());
        glDrawElements(GL_TRIANGLES, entity.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
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
