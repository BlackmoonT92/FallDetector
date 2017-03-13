package edu.asu.msama1.falldetector;

/**
 * Created by Mitikaa on 3/10/17.
 */

public class GraphValues {

    private float[] normA;
    private float[] normG;

    public GraphValues() {
        normA = new float[10];
        normG = new float[10];
    }

    public float[] getNormA() {
        return normA;
    }

    public void setNormA(float[] normA) {
        this.normA = normA;
    }

    public float[] getNormG() {
        return normG;
    }

    public void setNormG(float[] normG) {
        this.normG = normG;
    }
}
