package engine.test.core;

import java.util.ArrayList;

import engine.test.components.GameComponent;
import engine.test.core.math.quads.Quad;
import engine.test.core.math.vectors.Vector3f;
import engine.test.rendering.RenderingEngine;
import engine.test.rendering.Shader;

public class GameObject {
	
	private ArrayList<GameObject> children;
	private ArrayList<GameComponent> components;
	private Transform transform;
	private CoreEngine engine;
	
	public GameObject() {
		this.children = new ArrayList<>();
		this.components = new ArrayList<>();
		this.transform = new Transform();
		this.engine = null;
	}
	
	public GameObject(Vector3f pos, Quad rot, float scale) {
		this();
		transform.setPos(pos);
		transform.setRot(rot);
		transform.setScale(scale);
	}
	
	public GameObject(float scale) { 
		this(new Vector3f(0,0,0), new Quad(0,0,0,1), scale); 
	}
	
	public GameObject addChild(GameObject child) {
		children.add(child);
		child.setEngine(engine);
		child.getTransform().setParent(transform);
		return this;
	}
	
	public GameObject addComponent(GameComponent component) {
		components.add(component);
		component.setParent(this);
		return this;
	}
	
	public void inputAll(float delta) {
		input(delta);
		
		for (GameObject child : children)
			child.inputAll(delta);
	}
	
	public void updateAll(float delta) {
		update(delta);
		
		for (GameObject child : children)
			child.updateAll(delta);
	}
	
	public void renderAll(Shader shader, RenderingEngine renderingEngine) {
		render(shader, renderingEngine);
		
		for (GameObject child : children)
			child.renderAll(shader, renderingEngine);
	}
	
	public void input(float delta) {
		transform.update();
		
		for (GameComponent component : components)
			component.input(delta);
	}
	
	public void update(float delta) {
		for (GameComponent component : components)
			component.update(delta);
	}
	
	public void render(Shader shader, RenderingEngine renderingEngine) {
		for (GameComponent component : components) {
			component.render(shader, renderingEngine);
			RenderingEngine.checkGlError("Rendering " + component.toString());
		}
			
	}
	
	public ArrayList<GameObject> getAllAttached() {
		ArrayList<GameObject> res = new ArrayList<>();
		
		for (GameObject child : children)
			res.addAll(child.getAllAttached());
		
		res.add(this);
		return res;
	}
	
	public Transform getTransform() { return transform; }
	
	public void setEngine(CoreEngine engine) {
		if (this.engine != engine) {
			this.engine = engine;
			for (GameComponent component : components)
				component.addToEngine(engine);
			for (GameObject child : children)
				child.setEngine(engine);
		}
	}	
}