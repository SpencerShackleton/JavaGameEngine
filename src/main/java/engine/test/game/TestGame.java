package engine.test.game;

import engine.test.components.Camera;
import engine.test.components.DirectionalLight;
import engine.test.components.MeshRenderer;
import engine.test.components.PhysicsEngineComponent;
import engine.test.components.PhysicsObjectComponent;
import engine.test.components.PointLight;
import engine.test.components.SpotLight;
import engine.test.core.Game;
import engine.test.core.GameObject;
import engine.test.core.math.quads.Quad;
import engine.test.core.math.vectors.Vector2f;
import engine.test.core.math.vectors.Vector3f;
import engine.test.physics.BoundingSphere;
import engine.test.physics.PhysicsEngine;
import engine.test.physics.PhysicsObject;
import engine.test.physics.Plane;
import engine.test.rendering.Attenuation;
import engine.test.rendering.Material;
import engine.test.rendering.Mesh;
import engine.test.rendering.RenderingEngine;
import engine.test.rendering.Texture;
import engine.test.rendering.Vertex;
import engine.test.rendering.Window;

public class TestGame extends Game {
	
	public void init() {
		Material material = new Material(new Texture("bricks.jpg"), 0.6f, 3, new Texture("bricks_normal.jpg"), 
										 new Texture("bricks_disp.png"), 0.03f, -0.5f);

		Material material2 = new Material(new Texture("test.png"), 1, 8);

		Mesh tempMesh = new Mesh("monkey.obj");

		MeshRenderer meshRenderer = new MeshRenderer(new Mesh("plane3.obj"), material);

		GameObject planeObject = new GameObject();
		planeObject.addComponent(meshRenderer);
		planeObject.getTransform().getPos().set(0, -1, 5);
		planeObject.getTransform().setScale(4f);

		GameObject directionalLightObject = new GameObject();
		DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1,1,1), 0.4f);
		directionalLightObject.addComponent(directionalLight);
		directionalLight.getTransform().setRot(new Quad(new Vector3f(1, 0, 0), (float) Math.toRadians(-30)));

		GameObject pointLightObject = new GameObject();
		pointLightObject.addComponent(new PointLight(new Vector3f(0,1,0), 0.4f, new Attenuation(0,0,1)));

		SpotLight spotLight = new SpotLight(new Vector3f(0,1,1), 0.4f,
				new Attenuation(0,0,0.02f), (float) Math.toRadians(91.1f), 10);

		GameObject spotLightObject = new GameObject();
		spotLightObject.addComponent(spotLight);
		spotLightObject.getTransform().rotate(new Vector3f(0,1,0), (float)Math.toRadians(90f));
		spotLightObject.getTransform().getPos().set(10,1,5);
		spotLightObject.getTransform().rotate(new Quad(new Vector3f(1,0,0), (float)Math.toRadians(-60.0f)));

		addToScene(planeObject);
		addToScene(directionalLightObject);
		addToScene(pointLightObject);
		addToScene(spotLightObject);
		
		GameObject obamaPyramidObj = new GameObject().addComponent(
				new MeshRenderer(new Mesh("wsb.obj"), material2));
		obamaPyramidObj.getTransform().getPos().set(0, 5, 5);
		
		addToScene(obamaPyramidObj);
		GameObject testMesh3 = new GameObject().addComponent(new MeshRenderer(tempMesh, material));

		addToScene(new GameObject().addComponent(new Camera((float)Math.toRadians(70.0f), (float)Window.getWidth()/(float)Window.getHeight(), 0.01f, 1000.0f)));
		
		addToScene(testMesh3);

		testMesh3.getTransform().getPos().set(5,5,5);
		testMesh3.getTransform().setRot(new Quad(new Vector3f(0,1,0), (float)Math.toRadians(-70.0f)));

		addToScene(new GameObject().addComponent(new MeshRenderer(new Mesh("monkey.obj"), material2)));
	
		
		
		PhysicsEngine engine = new PhysicsEngine();
		
		engine.addObject(new PhysicsObject(new BoundingSphere(new Vector3f(0, 0, 0), 1), new Vector3f(0, 0, 1)));
		engine.addObject(new PhysicsObject(new BoundingSphere(new Vector3f(0, 0, 10), 1), new Vector3f(0, 0, -1)));
		
		PhysicsEngineComponent engineComponent = new PhysicsEngineComponent(engine);
		Material sphereMat = new Material(new Texture("bricks.jpg"), 1, 8, new Texture("bricks_normal.jpg"));
		
		for (PhysicsObject obj : engineComponent.getEngine().getObjects()) {
			addToScene(new GameObject()
								.addComponent(new PhysicsObjectComponent(obj))
								.addComponent(new MeshRenderer(new Mesh("sphere.obj"), sphereMat)));
		}
		
		
		addToScene(new GameObject().addComponent(engineComponent));
		
		GameObject box = new GameObject();
		box.addComponent(new MeshRenderer(new Mesh("cube.obj"), new Material(new Texture("bricks2.jpg"), 1f, 8f,
																			 new Texture("bricks2_normal.png"),
																			 new Texture("bricks2_disp.jpg"), 0.04f, -1.0f)));
		box.getTransform().setPos(new Vector3f(14, 0, 5));
		box.getTransform().setRot(new Quad(new Vector3f(0,1,0), (float) Math.toRadians(30f)));
		addToScene(box);
	}
}
