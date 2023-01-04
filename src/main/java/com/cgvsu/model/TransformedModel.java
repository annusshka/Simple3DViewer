package com.cgvsu.model;

import com.cgvsu.math.Math.Matrix.Matrix;
import com.cgvsu.math.Math.Matrix.Matrix4f;
import com.cgvsu.math.Math.Vector.Vector3f;
import com.cgvsu.render_engine.MyGraphicConveyor;

import java.util.ArrayList;

public class TransformedModel {
    final private float TRANSLATION_PARAMS = 5.5F;
    final private float SCALE_PARAMS = 5.5F;
    final private float ROTATE_PARAMS = 5.5F;

    public Model actualModel;

    private Matrix4f transformedMatrix;

    private float scaleXParams = 1;
    private float scaleYParams = 1;
    private float scaleZParams = 1;

    private float rotateXParams = 0;
    private float rotateYParams = 0;
    private float rotateZParams = 0;

    private float translateXParams = 0;
    private float translateYParams = 0;
    private float translateZParams = 0;

    public TransformedModel(
            Model actualMatrix,
            float scaleXParams, float scaleYParams, float scaleZParams,
            float rotateXParams, float rotateYParams, float rotateZParams,
            float translateXParams, float translateYParams, float translateZParams) {
        this.actualModel = actualMatrix;
        this.scaleXParams = scaleXParams;
        this.scaleYParams = scaleYParams;
        this.scaleZParams = scaleZParams;
        this.rotateXParams = rotateXParams;
        this.rotateYParams = rotateYParams;
        this.rotateZParams = rotateZParams;
        this.translateXParams = translateXParams;
        this.translateYParams = translateYParams;
        this.translateZParams = translateZParams;
    }

    public TransformedModel(Model actualMatrix) {
        this.actualModel = actualMatrix;
    }

    public void setRotateParams(float rotateXParams, float rotateYParams, float rotateZParams) {
        this.rotateXParams = rotateXParams;
        this.rotateYParams = rotateYParams;
        this.rotateZParams = rotateZParams;
    }

    public void setRotateXParam(float rotateParam) {
        this.rotateXParams = rotateParam + rotateXParams;
    }

    public void setRotateYParam(float rotateParam) {
        this.rotateYParams = rotateParam + rotateYParams;
    }

    public void setRotateZParam(float rotateParams) {
        this.rotateZParams = rotateParams + rotateZParams;
    }

    public Vector3f getRotateParams() {
        return new Vector3f(new float[]{rotateXParams, rotateYParams, rotateZParams});
    }

    public void setScaleParams(float scaleXParams, float scaleYParams, float scaleZParams) {
        this.scaleXParams = scaleXParams;
        this.scaleYParams = scaleYParams;
        this.scaleZParams = scaleZParams;
    }

    public void scaleXParams(float scaleParam) {
        this.scaleXParams = scaleParam + scaleXParams;
    }

    public void scaleYParams(float scaleParam) {
        this.scaleYParams = scaleParam + scaleYParams;
    }

    public void scaleZParams(float scaleParam) {
        this.scaleZParams = scaleParam + scaleZParams;
    }

    public Vector3f getScaleParams() {
        return new Vector3f(new float[]{scaleXParams, scaleYParams, scaleZParams});
    }

    public void setTranslateParams(float translateXParams, float translateYParams, float translateZParams) {
        this.translateXParams = translateXParams;
        this.translateYParams = translateYParams;
        this.translateZParams = translateZParams;
    }

    public void setTranslateXParam(float translateParam) {
        this.translateXParams = translateParam + translateXParams;
    }

    public void setTranslateYParam(float translateParam) {
        this.translateYParams = translateParam + translateYParams;
    }

    public void setTranslateZParam(float translateParam) {
        this.translateZParams = translateParam + translateZParams;
    }

    public Vector3f getTranslateParams() {
        return new Vector3f(new float[]{translateXParams, translateYParams, translateZParams});
    }

    public Model getModelMatrix() {
        return this.actualModel;
    }

    /*
    Matrix4f modelMatrix = new Matrix4f();
        TransformedModel model = new TransformedModel(mesh);
        try {
            modelMatrix = model.rotateScaleTranslate();
        } catch (Matrix.MatrixException e) {
            throw new RuntimeException(e);
        }
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        // Надо поменять умножение
        Matrix4f modelViewProjectionMatrix = new Matrix4f(modelMatrix.getVector());
        modelViewProjectionMatrix.mul(viewMatrix);
        modelViewProjectionMatrix.mul(projectionMatrix);
     */
    public Matrix4f rotateScaleTranslate() {
        try {
            this.transformedMatrix =
                    MyGraphicConveyor.rotateScaleTranslate(getRotateParams(), getScaleParams(), getTranslateParams());
            return transformedMatrix;
        } catch (Matrix.MatrixException e) {
            throw new RuntimeException(e);
        }
    }

    public Model getTransformedModel() {
        int size = transformedMatrix.getSize();
        Model transformedModel = new Model();
        for (int index = 0; index < size - 1; index++) {
            float[] vertex = new float[]{
                    transformedMatrix.get(index),
                    transformedMatrix.get(size + index),
                    transformedMatrix.get(2 * size + index)};
            transformedModel.vertices.add(new Vector3f(vertex));
        }
        return transformedModel;
    }
}