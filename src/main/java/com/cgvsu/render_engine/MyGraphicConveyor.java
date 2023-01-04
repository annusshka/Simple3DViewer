package com.cgvsu.render_engine;

import com.cgvsu.math.Math.Matrix.Matrix;
import com.cgvsu.math.Math.Matrix.Matrix4f;
import com.cgvsu.math.Math.Vector.Vector;
import com.cgvsu.math.Math.Vector.Vector3f;
import com.cgvsu.math.Math.Vector.Vector4f;

import javax.vecmath.Point2f;

public class MyGraphicConveyor {

    private static final float EPS = 1e-5f;

    public static Matrix4f rotateScaleTranslate(Vector3f rotateVector, Vector3f scaleVector, Vector3f translateVector)
            throws Matrix.MatrixException {
        Matrix4f matrix4f = (Matrix4f) new Matrix4f().createIdentityMatrix();
        setScaleMatrix(matrix4f, scaleVector);
        //getRotateMatrix(matrix4f, rotateVector);
        matrix4f = (Matrix4f) Matrix4f.multiplicateMatrices(getRotateMatrix(rotateVector), matrix4f);
        addTranslate(matrix4f, translateVector);
        return matrix4f;
    }

    public static Matrix getRotateMatrix(Vector3f rotateVector) throws Matrix.MatrixException {
        Matrix4f matrix4f = (Matrix4f) new Matrix4f().createIdentityMatrix();
        if (rotateVector.get(0) > EPS) {
            matrix4f = (Matrix4f) Matrix4f.multiplicateMatrices(getXRotationMatrix(rotateVector.get(0)), matrix4f);
        }
        if (rotateVector.get(1) > EPS) {
            matrix4f = (Matrix4f) Matrix4f.multiplicateMatrices(getYRotationMatrix(rotateVector.get(1)), matrix4f);
        }
        if (rotateVector.get(2) > EPS) {
            matrix4f = (Matrix4f) Matrix4f.multiplicateMatrices(getZRotationMatrix(rotateVector.get(2)), matrix4f);
        }
        return matrix4f;
    }

    public static Matrix getXRotationMatrix(float alfa) {
        alfa = (float) Math.toRadians(alfa);
        final float cos = (float) Math.cos(alfa);
        final float sin = (float) Math.sin(alfa);

        return new Matrix4f(new float[]{
                1, 0, 0, 0,
                0, cos, sin, 0,
                0, -sin, cos, 0,
                0, 0, 0, 1});
    }

    public static Matrix getYRotationMatrix(float alfa) {
        alfa = (float) Math.toRadians(alfa);
        final float cos = (float) Math.cos(alfa);
        final float sin = (float) Math.sin(alfa);

        return new Matrix4f(new float[]{
                cos, 0, -sin, 0,
                0, 1, 0, 0,
                sin, 0, cos, 0,
                0, 0, 0, 1});
    }

    public static Matrix getZRotationMatrix(float alfa) {
        alfa = (float) Math.toRadians(alfa);
        final float cos = (float) Math.cos(alfa);
        final float sin = (float) Math.sin(alfa);

        return new Matrix4f(new float[]{
                cos, sin, 0, 0,
                -sin, cos, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1});
    }

    public static void setScaleMatrix(Matrix4f matrix4f, Vector3f scaleVector) {
        int index = 0;
        int size = matrix4f.getSize();
        for (float value : scaleVector.getVector()) {
            if (Math.abs(value) > EPS) {
                matrix4f.set(index * size + index, value);
            }
            index++;
        }
    }

    public static void addTranslate(Matrix4f matrix4f, Vector3f translateVector) {
        int indexRow = 0;
        int size = matrix4f.getSize();
        for (float value : translateVector.getVector()) {
            matrix4f.set(indexRow * size + (size - 1), value);
            indexRow++;
        }
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target) throws Vector.VectorException {
        return lookAt(eye, target, new Vector3f(new float[]{0, 1, 0}));
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) throws Vector.VectorException {
        Vector3f resultX = new Vector3f();
        Vector3f resultY = new Vector3f();
        Vector3f resultZ = new Vector3f();

        resultZ = (Vector3f) resultZ.minusVector(target, eye);
        resultX.crossProduct(up, resultZ);
        resultY.crossProduct(resultZ, resultX);

        resultX = (Vector3f) resultX.normalizeVector();
        resultY = (Vector3f) resultY.normalizeVector();
        resultZ = (Vector3f) resultZ.normalizeVector();

        // Переход в систему координат камеры
        float[] matrix = new float[]{
                resultX.get(0), resultY.get(0), resultZ.get(0), -resultX.dotProduct(eye),
                resultX.get(1), resultY.get(1), resultZ.get(1), -resultY.dotProduct(eye),
                resultX.get(2), resultY.get(2), resultZ.get(2), -resultZ.dotProduct(eye),
                0, 0, 0, 1
        };

        return new Matrix4f(matrix);
    }

    public static Matrix4f perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        Matrix4f result = new Matrix4f();
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));
        result.set(0, tangentMinusOnDegree / aspectRatio);
        result.set(5, tangentMinusOnDegree);
        result.set(10, (farPlane + nearPlane) / (farPlane - nearPlane));
        result.set(11, 2 * (nearPlane * farPlane) / (nearPlane - farPlane));
        result.set(14, 1.0F);
        return result;
    }

    //метод не нужен, но нужно последнее действие
    public static Vector3f multiplyMatrix4ByVector3(final Matrix4f matrix, final Vector3f vertex)
            throws Vector.VectorException, Matrix.MatrixException {
        Vector4f vector4f = (Vector4f) matrix.multiplicateOnVector(
                new Vector4f(new float[]{vertex.x, vertex.y, vertex.z, 1}));
        /*
        final float x = (vertex.x * matrix.get(0)) + (vertex.y * matrix.get(1)) + (vertex.z * matrix.get(2))
                + matrix.get(3);
        final float y = (vertex.x * matrix.get(4)) + (vertex.y * matrix.get(5)) + (vertex.z * matrix.get(6))
                + matrix.get(7);
        final float z = (vertex.x * matrix.get(8)) + (vertex.y * matrix.get(9)) + (vertex.z * matrix.get(10))
                + matrix.get(11);
        final float w = (vertex.get(0) * matrix.get(12)) + (vertex.get(1) * matrix.get(13)) +
                (vertex.get(2) * matrix.get(14)) + matrix.get(15);
        //return (Vector3f) vertex.divideVectorOnConstant(w);
        return new Vector3f(new float[]{x / w, y / w, z / w});

         */
        float w = vector4f.get(3);
        return new Vector3f(new float[]{vector4f.get(0) / w, vector4f.get(1) / w, vector4f.get(2) / w});
    }

    public static Point2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        return new Point2f(vertex.get(0) * width + width / 2.0F, -vertex.get(1) * height + height / 2.0F);
    }





    /*
    public Vector getRotation(final Matrix3d matrix, final Vector3d vector) throws Matrix.MatrixException {
        Vector4d vector4f = new Vector4d();
        for (int i  = 0; i < vector.getSize(); i++) {
            vector4f.getVector()[i] = vector.getVector()[i];
        }
        vector4f.getVector()[vector4f.getSize() - 1] = 1;

        return matrix.multiplicateOnVector(vector4f);
    }

    public Matrix getGeneralRotationMatrix(final double alfa, final double beta, final double gamma)
            throws Matrix.MatrixException {
        Matrix matrix = Matrix.multiplicateMatrices(getXRotationMatrix(alfa), getYRotationMatrix(beta));
        return Matrix.multiplicateMatrices(matrix, getZRotationMatrix(gamma));
    }

    public Matrix getSqueezingMatrix(final double coeffX, final double coeffY, final double coeffZ) {
        Matrix3d matrix3x = new Matrix3d();
        Vector3d vector3f = new Vector3d(new double[]{coeffX, coeffY, coeffZ});

        for (int index = 0; index < matrix3x.getSize(); index++) {
            matrix3x.getVector()[index * matrix3x.getSize() + index] = vector3f.getVector()[index];
        }

        return matrix3x;
    }

    public Vector getShearing(Vector3d vector3f, Vector4d vector4f) throws Matrix.MatrixException {
        Matrix3d matrix3x = new Matrix3d();
        matrix3x.createIdentityMatrix();

        Matrix matrix = rotateScaleTranslate();
        return matrix.multiplicateOnVector(vector4f);
    }

     */
}










