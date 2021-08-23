package engine.test.physics;

import java.util.ArrayList;

import engine.test.core.math.vectors.Vector3f;

public class PhysicsEngine {

	private ArrayList<PhysicsObject> objects;
	
	public PhysicsEngine() {
		this.objects = new ArrayList<>();
	}
	
	public void addObject(PhysicsObject object) {
		objects.add(object);
	}
	
	public void simulate(float delta) {
		for (PhysicsObject object : objects) {
			object.integrate(delta);
		}
	}
	
	public void handleCollisions() {
		for (int i = 0; i < objects.size(); i++) {
			for (int j = i + 1; j < objects.size(); j++) {
				PhysicsObject first = objects.get(i);
				PhysicsObject second = objects.get(j);
				IntersectData intersectData = 
						first.getCollider().intersect(
								second.getCollider());
				
				if (intersectData.getDoesIntersect()) {
					Vector3f direction = intersectData.getDirection().normalized();
					Vector3f secondDir = direction.reflect(first.getVelocity().normalized());
					
					first.setVelocity(first.getVelocity().reflect(direction));
					second.setVelocity(second.getVelocity().reflect(secondDir));
				}
			}
		}
	}
	
	public ArrayList<PhysicsObject> getObjects() { return objects; }
}
