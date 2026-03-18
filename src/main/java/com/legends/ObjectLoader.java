package com.legends;

import com.legends.entity.Model;
import com.legends.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
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

    public Model loadOBJModel(String filename) {
        List<String> lines = Utils.readAllLines(filename);

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3i> faces = new ArrayList<>();

        for(String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v":
                    //vertices
                    Vector3f verticesVec = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]));
                    vertices.add(verticesVec);
                    break;
                case "vt":
                    //vertex Textures
                    Vector2f textureVec = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]));
                    textures.add(textureVec);
                    break;
                case "vn":
                    //vertex normals
                    Vector3f normalVec = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]));
                    normals.add(normalVec);
                    break;

                case "f":
                    //faces
                    processFaces(tokens[1], faces);
                    processFaces(tokens[2], faces);
                    processFaces(tokens[3], faces);
                    break;
                default:
                    break;

            }
        }

        List<Integer> indices = new ArrayList<>();
        float[] verticesArr = new float[vertices.size() * 3];
        int i =  0;
        for(Vector3f pos : vertices) {
            verticesArr[i * 3] = pos.x;
            verticesArr[i * 3 + 1] = pos.y;
            verticesArr[i * 3 + 2] = pos.z;
            i++;
        }

        float[] texturesArr = new float[textures.size() * 2];
        float[] normalsArr = new float[normals.size() * 3];

        for(Vector3i face : faces){

        }

        int[] indicesArr = indices.stream().mapToInt((Integer v) -> v).toArray();

        return loadModel(verticesArr, texturesArr, indicesArr);

    }

    private static void processFaces(String token, List<Vector3i> faces) {
        String[] lineToken = token.split("/");
        int length = lineToken.length;
        int pos = -1, coords = -1, normals = -1;
        pos = Integer.parseInt(lineToken[0]) - 1;
        if (length > 1){
            String textCoords = lineToken[1];
            coords = textCoords.length() > 0 ? Integer.parseInt(textCoords) : -1;
            if (length > 2){
                normals = Integer.parseInt(lineToken[2]) - 1;
            }
        }

        Vector3i facesVec = new Vector3i(pos, coords, normals);
        faces.add(facesVec);
    }

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
