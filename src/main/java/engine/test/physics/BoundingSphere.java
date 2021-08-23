package engine.test.physics;

import engine.test.core.math.vectors.Vector3f;

public class BoundingSphere extends Collider {

	private Vector3f center;
	private float radius;
	
	public BoundingSphere(Vector3f center, float radius) {
		super(ColliderType.SPHERE);
		this.center = center;
		this.radius = radius;
	}
	
	@Override
	public void transform(Vector3f translation) {
		this.center = center.add(translation);
	}
	
	@Override
	public Vector3f getCenter() { 
		return center; 
	}
	
	@Override
	public IntersectData intersectBoundingSphere(BoundingSphere other) {
		float radiusDistance = radius + other.getRadius();
		Vector3f direction = other.getCenter().sub(center);
		float centerDistance = direction.length();
		direction = direction.div(centerDistance);
		float penetrationDistance = centerDistance - radiusDistance;
		
		return new IntersectData(penetrationDistance < 0, direction.mult(penetrationDistance));
	}
	
	@Override
	public IntersectData intersectPlane(Plane other) {
		return other.intersectBoundingSphere(this);
	}
	
	@Override
	public IntersectData intersectAABB(AxisAlignedBB other) {
		
		return null;
	}
	
	public float getRadius() { return radius; }
	public void setCenter(Vector3f center) { this.center = center; }
	public void setRadius(float radius) { this.radius = radius; }
}
