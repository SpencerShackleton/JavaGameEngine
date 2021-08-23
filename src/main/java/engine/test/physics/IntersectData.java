package engine.test.physics;

import engine.test.core.math.vectors.Vector3f;

public class IntersectData {
	
	private boolean doesIntersect;
	private Vector3f direction;
	
	public IntersectData(boolean doesIntersect, Vector3f direction) {
		this.doesIntersect = doesIntersect;
		this.direction = direction;
	}
	
	public boolean getDoesIntersect() { return doesIntersect; }
	public float getDistance() { return direction.length(); }
	public Vector3f getDirection() { return direction; }
	
	public void setDoesIntersect(boolean doesIntersect) {
		this.doesIntersect = doesIntersect;
	}
	
}
