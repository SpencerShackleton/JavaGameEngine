package engine.test.physics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import engine.test.core.math.vectors.Vector3f;

public class TestPlane {

	@Test
	public void testintersectSphere() {
		Plane test = new Plane(new Vector3f(0,1,0), 0);
		BoundingSphere sphere = new BoundingSphere(new Vector3f(0,0,0), 3);
		
		assertTrue(test.intersectBoundingSphere(sphere).getDoesIntersect());
		
		Plane test2 = new Plane(new Vector3f(0,1,0), 0);
		BoundingSphere sphere2 = new BoundingSphere(new Vector3f(0,1,0), 1);
		
		assertTrue(!test2.intersectBoundingSphere(sphere2).getDoesIntersect());
		assertEquals(test2.intersectBoundingSphere(sphere2).getDistance(), 0f, 0f);
	}
}
