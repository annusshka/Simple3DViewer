package com.cgvsu.render_engine;

import java.util.ArrayList;

import com.cgvsu.math.Math.Matrix.Matrix;
import com.cgvsu.math.Math.Matrix.Matrix4f;
import com.cgvsu.math.Math.Vector.Vector;
import com.cgvsu.math.Math.Vector.Vector3f;
import com.cgvsu.model.TransformedModel;
import javafx.scene.canvas.GraphicsContext;
import javax.vecmath.*;
import com.cgvsu.model.Model;
import static com.cgvsu.render_engine.MyGraphicConveyor.*;

public class RenderEngine {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final TransformedModel transformedModel,
            final Model mesh,
            final int width,
            final int height)
    {
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

        //TransformedModel transformedModel = new TransformedModel(mesh);
        Matrix4f modelMatrix = transformedModel.rotateScaleTranslate();

        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        // Надо поменять умножение
        Matrix4f modelViewProjectionMatrix = new Matrix4f(modelMatrix.getVector());
        try {
            modelViewProjectionMatrix =
                    (Matrix4f) Matrix4f.multiplicateMatrices(viewMatrix, modelViewProjectionMatrix);
            modelViewProjectionMatrix =
                    (Matrix4f) Matrix4f.multiplicateMatrices(projectionMatrix, modelViewProjectionMatrix);
        } catch (Matrix.MatrixException e) {
            throw new RuntimeException(e);
        }

        final int nPolygons = mesh.polygons.size();
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            final int nVerticesInPolygon = mesh.polygons.get(polygonInd).getVertexIndices().size();

            ArrayList<Point2f> resultPoints = new ArrayList<>();
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                Vector3f vertex = mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().
                        get(vertexInPolygonInd));

                //Vector3f vertexVecmath = new Vector3f(new float[]{vertex.get(0), vertex.get(1), vertex.get(2)});

                Point2f resultPoint = null;
                try {
                    resultPoint = vertexToPoint(
                            multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertex), width, height);
                } catch (Vector.VectorException | Matrix.MatrixException e) {
                    throw new RuntimeException(e);
                }
                resultPoints.add(resultPoint);
            }

            for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                graphicsContext.strokeLine(
                        resultPoints.get(vertexInPolygonInd - 1).x,
                        resultPoints.get(vertexInPolygonInd - 1).y,
                        resultPoints.get(vertexInPolygonInd).x,
                        resultPoints.get(vertexInPolygonInd).y);
            }

            if (nVerticesInPolygon > 0)
                graphicsContext.strokeLine(
                        resultPoints.get(nVerticesInPolygon - 1).x,
                        resultPoints.get(nVerticesInPolygon - 1).y,
                        resultPoints.get(0).x,
                        resultPoints.get(0).y);
        }
    }
}