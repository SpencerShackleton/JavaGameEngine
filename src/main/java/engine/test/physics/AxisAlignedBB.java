package engine.test.physics;

import engine.test.core.math.vectors.Vector3f;

public class AxisAlignedBB extends Collider {

	private Vector3f minExtents;
	private Vector3f maxExtents; 
	
	public AxisAlignedBB(Vector3f minExtents, Vector3f maxExtents) {
		super(ColliderType.AABB);
		this.minExtents = minExtents;
		this.maxExtents = maxExtents;
	}

	@Override
	public void transform(Vector3f translation) {
		
	}

	@Override
	public Vector3f getCenter() {
		
		return null;
	}

	@Override
	public IntersectData intersectBoundingSphere(BoundingSphere sphere) {
		
		return null;
	}

	@Override
	public IntersectData intersectPlane(Plane other) {
		Vector3f c = minExtents.add(maxExtents).mult(0.5f);
		Vector3f e = maxExtents.sub(c);
		
		float r1 = e.getX() * Math.abs(other.getNormal().getX());
		float r2 = e.getY() * Math.abs(other.getNormal().getY());
		float r3 = e.getZ() * Math.abs(other.getNormal().getZ());
		float r = r1 + r2 + r3;
		
		float s = other.getNormal().dot(c) - other.getDistance();
		
		return new IntersectData(Math.abs(s) <= r, new Vector3f(0,0,0));
	}

	@Override
	public IntersectData intersectAABB(AxisAlignedBB other) {
		Vector3f distance1 = other.getMinExtents().sub(maxExtents);
		Vector3f distance2 = minExtents.sub(other.getMaxExtents());
		Vector3f distances = distance1.max(distance2);
		
		float maxDistance = distances.max();
		
		return new IntersectData(maxDistance < 0, distances);
	}
	
	public Vector3f getMinExtents() { return minExtents; }
	public Vector3f getMaxExtents() { return maxExtents; }
	public void setMinExtents(Vector3f minExtents) { this.minExtents = minExtents; }
	public void setMaxExtents(Vector3f maxExtents) { this.maxExtents = maxExtents; }
	
}
