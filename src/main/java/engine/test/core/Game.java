package engine.test.core;

import engine.test.rendering.RenderingEngine;

public abstract class Game {

	private GameObject root;
	
	private ProfileTimer updateTimer = new ProfileTimer();
	private ProfileTimer inputTimer = new ProfileTimer();
	
	public void init() {}
	
	public void input(float delta) {
		inputTimer.startInvocation();
		getRootObject().inputAll(delta);
		inputTimer.stopInvocation();
	}
	
	public void update(float delta) {
		updateTimer.startInvocation();
		getRootObject().updateAll(delta);
		updateTimer.stopInvocation();
	}
	
	public void render(RenderingEngine renderingEngine) {
		renderingEngine.render(getRootObject());
	}
	
	public void addToScene(GameObject object) {
		getRootObject().addChild(object);
	}
	
	private GameObject getRootObject() {
		if (root == null)
			root = new GameObject();
		return root;
	}
	
	double displayInputTime(double dividend) { return inputTimer.displayAndReset("Input Time: ", dividend); }
	double displayUpdateTime(double dividend) { return updateTimer.displayAndReset("Update Time: ", dividend); }
	
	public void setEngine(CoreEngine engine) { getRootObject().setEngine(engine); }
}
