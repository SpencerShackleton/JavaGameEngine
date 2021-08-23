package engine.test.components;

import engine.test.core.CoreEngine;
import engine.test.core.GameObject;
import engine.test.core.Transform;
import engine.test.rendering.RenderingEngine;
import engine.test.rendering.Shader;

public abstract class GameComponent {

	private GameObject parent;
	
	public void input(float delta) {}
	public void update(float delta) {}
	public void render(Shader shader, RenderingEngine renderingEngine) {}
	
	public void setParent(GameObject parent) { this.parent = parent; }
	public Transform getTransform() { return parent.getTransform(); }
	
	public void addToEngine(CoreEngine engine) {}
}
