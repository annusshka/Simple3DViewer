package com.cgvsu.math;

// Это заготовка для собственной библиотеки для работы с линейной алгеброй
public class Vector3f extends com.cgvsu.math.Math.Vector.Vector3f {
    public Vector3f(float[] vector) {
        this.x = vector[0];
        this.y = vector[1];
        this.z = vector[2];
    }

    public boolean equals(Vector3f other) {
        // todo: желательно, чтобы это была глобальная константа
        final float eps = 1e-7f;
        return Math.abs(x - other.x) < eps && Math.abs(y - other.y) < eps && Math.abs(z - other.z) < eps;
    }

    public float x, y, z;
}
