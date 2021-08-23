package engine.test.physics;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import engine.test.core.math.vectors.Vector3f;

public class TestPhysicsObject {
	
	@Test
	public void testIntegrate() {
		PhysicsObject test = new PhysicsObject(new BoundingSphere(new Vector3f(0,1,0), 1), new Vector3f(1,2,4));
		test.integrate(20f);
		assertTrue(test.getPosition().equals(new Vector3f(20,41,80)));
		
		PhysicsObject test2 = new PhysicsObject(new BoundingSphere(new Vector3f(100,100,100), 1), new Vector3f(2,2,4));
		test2.integrate(0f);
		assertTrue(test2.getPosition().equals(new Vector3f(100,100,100)));
		
		
	}
	
}
