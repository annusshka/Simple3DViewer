package com.cgvsu.model;
import com.cgvsu.math.Math.Vector.Vector2f;
import com.cgvsu.math.Math.Vector.Vector3f;

import java.util.*;

public class Model {

    public ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
    public ArrayList<Vector2f> textureVertices = new ArrayList<Vector2f>();
    public ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
    public ArrayList<Polygon> polygons = new ArrayList<Polygon>();

    public Model(final ArrayList<Vector3f> vertices, final ArrayList<Vector2f> textureVertices,
                 final ArrayList<Vector3f> normals, final ArrayList<Polygon> polygons) {
        this.vertices = vertices;
        this.textureVertices = textureVertices;
        this.normals = normals;
        this.polygons = polygons;
    }

    public Model() {
        vertices = new ArrayList<>();
        textureVertices = new ArrayList<>();
        normals = new ArrayList<>();
        polygons = new ArrayList<>();
    }

    public ArrayList<Vector3f> getVertices() {
        return vertices;
    }

    public ArrayList<Vector2f> getTextureVertices() {
        return textureVertices;
    }

    public ArrayList<Vector3f> getNormals() {
        return normals;
    }

    public ArrayList<Polygon> getPolygons() {
        return polygons;
    }

    public void setVertices(final ArrayList<Vector3f> vertices) {
        this.vertices = vertices;
    }

    public void setTextureVertices(final ArrayList<Vector2f> vertices) {
        this.textureVertices = vertices;
    }

    public void setNormals(final ArrayList<Vector3f> vertices) {
        this.normals = vertices;
    }

    public void setPolygons(final ArrayList<Polygon> vertices) {
        this.polygons = vertices;
    }
}
