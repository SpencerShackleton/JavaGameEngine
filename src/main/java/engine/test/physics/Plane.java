package engine.test.physics;

import engine.test.core.math.vectors.Vector3f;

public class Plane extends Collider {
	
	private Vector3f normal;
	private float distance;
	
	public Plane(Vector3f normal, float distance) {
		super(ColliderType.PLANE);
		this.normal = normal;
		this.distance = distance;
	}
	
	public Plane normalized() {
		float len = normal.length();
		return new Plane(normal.normalized(), distance/len);
	}

	@Override
	public void transform(Vector3f translation) {

	}

	@Override
	public Vector3f getCenter() {
		return normal.mult(distance);
	}
	
	@Override
	public IntersectData intersectBoundingSphere(BoundingSphere sphere) {
		float distanceToCenter = Math.abs(normal.dot(sphere.getCenter()) + distance);
		float distanceToSphere = distanceToCenter - sphere.getRadius();
		return new IntersectData(distanceToSphere < 0, normal.mult(distanceToSphere));
	}
	
	@Override
	public IntersectData intersectPlane(Plane other) {
		return new IntersectData(false, new Vector3f(0,0,0));
	}
	
	@Override
	public IntersectData intersectAABB(AxisAlignedBB other) {
		return other.intersectPlane(this);
	}
	
	public Vector3f getNormal() { return normal; }
	public float getDistance() { return distance; }
	public void setNormal(Vector3f normal) { this.normal = normal; }
	public void setDistance(float distance) { this.distance = distance; }
}
