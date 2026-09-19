package io.github.aliceblackrose.wynnclone.combat.projectile;

public record Vec3(double x, double y, double z) {
    private static final double EPSILON = 1.0e-12;

    public Vec3 {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            throw new IllegalArgumentException("Vector components must be finite");
        }
    }

    public Vec3 add(Vec3 other) {
        return new Vec3(x + other.x, y + other.y, z + other.z);
    }

    public Vec3 multiply(double scalar) {
        if (!Double.isFinite(scalar)) {
            throw new IllegalArgumentException("Scalar must be finite");
        }
        return new Vec3(x * scalar, y * scalar, z * scalar);
    }

    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public Vec3 normalize() {
        double length = length();
        if (length <= EPSILON) {
            throw new IllegalStateException("Cannot normalize a zero-length vector");
        }
        return multiply(1.0 / length);
    }
}
