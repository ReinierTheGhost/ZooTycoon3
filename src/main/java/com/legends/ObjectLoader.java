package com.legends;

import com.legends.entity.Model;
import com.legends.utils.Utils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class ObjectLoader {

    /**
     * VAOS = Vertex Array Object
     * VBOS = Vertex Buffer Object
     */
    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    public Model loadModel(float[] vertices, float[] texturesCoords, int[] indices){
        int id = createVAO();
        storeIndicesBuffer(indices);
        storeDataInAttribList(0, 3, vertices);
        storeDataInAttribList(1, 2, texturesCoords);
        unbind();
        return new Model(id, indices.length);
    }


    public int loadTexture(String resourcePath) throws Exception {
        int width, height;
        ByteBuffer image;
        ByteBuffer fileBuffer = null;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            fileBuffer = Utils.loadResourceToByteBuffer(resourcePath);

            image = STBImage.stbi_load_from_memory(fileBuffer, w, h, c, 4);
            if (image == null) {
                throw new Exception("Image resource " + resourcePath + " not loaded: " + STBImage.stbi_failure_reason());
            }

            width = w.get(0);
            height = h.get(0);
        } finally {
            if (fileBuffer != null) {
                org.lwjgl.system.MemoryUtil.memFree(fileBuffer);
            }
        }

        int id = glGenTextures();
        textures.add(id);

        glBindTexture(GL_TEXTURE_2D, id);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        glGenerateMipmap(GL_TEXTURE_2D);

        STBImage.stbi_image_free(image);
        return id;
    }

//    public int loadTexture(String filename) throws Exception{
//        int width, height;
//        ByteBuffer buffer;
//        try(MemoryStack stack = MemoryStack.stackPush()) {
//            IntBuffer w = stack.mallocInt(1);
//            IntBuffer h = stack.mallocInt(1);
//            IntBuffer c = stack.mallocInt(1);
//
//            buffer = STBImage.stbi_load(filename, w, h, c, 4);
//            if(buffer == null){
//                throw new Exception("Image File " + filename + " not loaded " + STBImage.stbi_failure_reason());
//            }
//
//            width = w.get();
//            height = h.get();
//        }
//
//        int id = glGenTextures();
//        textures.add(id);
//        glBindTexture(GL_TEXTURE_2D, id);
//        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
//        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
//        glGenerateMipmap(GL_TEXTURE_2D);
//        STBImage.stbi_image_free(buffer);
//        return id;
//    }

    private int createVAO(){
        int id = glGenVertexArrays();
        vaos.add(id);
        glBindVertexArray(id);
        return id;
    }

    private void storeIndicesBuffer(int[] indices){
        int vbo = glGenBuffers();
        vbos.add(vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    private void storeDataInAttribList(int attribNo, int vertexCount, float[] data){
        int vbo = glGenBuffers();
        vbos.add(vbo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attribNo, vertexCount, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void unbind(){
        glBindVertexArray(0);
    }

    public void cleanup(){
        for (int vao : vaos){
            glDeleteVertexArrays(vao);
        }
        for (int vbo : vbos){
            glDeleteBuffers(vbo);
        }
        for (int texture : textures){
            glDeleteTextures(texture);
        }
    }
}
