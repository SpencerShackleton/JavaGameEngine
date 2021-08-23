package engine.test.physics;

import engine.test.core.math.vectors.Vector3f;

public class PhysicsObject {

	private Vector3f position;
	private Vector3f lastPosition;
	private Vector3f velocity;
	
	private Collider collider;
	
	public PhysicsObject(Collider collider, Vector3f velocity) {
		this.position = collider.getCenter();
		this.lastPosition = position;
		this.velocity = velocity;
		this.collider = collider;
	}
	
	public void integrate(float delta) {
		this.position = position.add(velocity.mult(delta));
	}

	public Collider getCollider() {
		Vector3f translation = position.sub(lastPosition);
		lastPosition = position;
		collider.transform(translation);
		return collider;
	}
	
	public Vector3f getPosition() { return position; }
	public Vector3f getVelocity() { return velocity; }

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void setVelocity(Vector3f velocity) {
		this.velocity = velocity;
	}

}
