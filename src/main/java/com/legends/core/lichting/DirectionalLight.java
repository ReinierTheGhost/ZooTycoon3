package com.legends.core.lichting;

import org.joml.Vector3f;

public class DirectionalLight {

    Vector3f color, directiom;
    private float intensity;

    public DirectionalLight(Vector3f color, Vector3f direction, float intensity) {
        this.color = color;
        this.directiom = direction;
        this.intensity = intensity;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getDirectiom() {
        return directiom;
    }

    public void setDirectiom(Vector3f directiom) {
        this.directiom = directiom;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
