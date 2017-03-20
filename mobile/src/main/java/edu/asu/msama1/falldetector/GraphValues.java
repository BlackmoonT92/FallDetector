package edu.asu.msama1.falldetector;

/**
 * Copyright 2017 Mitikaa Sama,
 *
 * The Instructor and the Arizona State University
 * has the right to build and evaluate the software package
 * for the purpose of determining the grade and program assessment.
 *
 * Purpose: Masters Applied Project
 *
 * @author Mitikaa Sama on 3/10/17.
 *
 * This is a model class and consists of accelerometer and gyroscope arrays as member variables, used to plot line graphs on GraphView
 */
public class GraphValues {

    /**
     * float array of accelerometer norm values
     */
    private float[] normA;

    /**
     * float array of gyroscope norm values
     */
    private float[] normG;

    /**
     * empty coonstructor
     */
    public GraphValues() {
        normA = new float[10];
        normG = new float[10];
    }

    /**
     * Accelerometer norm getter
     * @return array of accelerometer norm values
     */
    public float[] getNormA() {
        return normA;
    }

    /**
     * Accelerometer norm setter
     * @param normA : array of accelerometer norm values
     */
    public void setNormA(float[] normA) {
        this.normA = normA;
    }

    /**
     * Gyroscope norm getter
     * @return array of gyroscope norm values
     */
    public float[] getNormG() {
        return normG;
    }

    /**
     * Gyroscope norm setter
     * @param normG : array of gyroscope norm values
     */
    public void setNormG(float[] normG) {
        this.normG = normG;
    }
}
