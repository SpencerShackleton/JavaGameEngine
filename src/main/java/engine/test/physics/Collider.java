package engine.test.physics;

import engine.test.core.math.vectors.Vector3f;

public abstract class Collider {

	private ColliderType type;
	
	public Collider(ColliderType type) {
		this.type = type;
	}
	
	public IntersectData intersect(Collider other) {
		if (other.getType() == ColliderType.SPHERE)
			return intersectBoundingSphere((BoundingSphere) other);
		if (other.getType() == ColliderType.AABB)
			return intersectAABB((AxisAlignedBB) other);
		if (other.getType() == ColliderType.PLANE)
			return intersectPlane((Plane) other);
		System.err.println("Error: Invalid collision types");
		System.exit(1);
		return null;
	}
	
	public abstract IntersectData intersectBoundingSphere(BoundingSphere sphere);
	public abstract IntersectData intersectPlane(Plane other);
	public abstract IntersectData intersectAABB(AxisAlignedBB other);
	
	public abstract void transform(Vector3f translation);
	public abstract Vector3f getCenter();
	
	public ColliderType getType() { return this.type; }
	
	public enum ColliderType {
		SPHERE, AABB, PLANE;
	}

}
