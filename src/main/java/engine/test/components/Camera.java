package engine.test.components;

import org.lwjgl.glfw.GLFW;

import engine.test.core.CoreEngine;
import engine.test.core.Input;
import engine.test.core.math.matrices.Matrix4f;
import engine.test.core.math.vectors.Vector2f;
import engine.test.core.math.vectors.Vector3f;
import engine.test.rendering.RenderingEngine;
import engine.test.rendering.Window;

public class Camera extends GameComponent {

	public static final Vector3f yAxis = new Vector3f(0, 1, 0);
	
	private Matrix4f projection;
	
	public Camera(float fov, float aspect, float zNear, float zFar) {
		this.projection = new Matrix4f().initPerspective(fov, aspect, zNear, zFar);
	}
	
	public Camera(Matrix4f matrix) {
		this.projection = matrix;
	}
	
	public Matrix4f getViewProjection() {
		Matrix4f cameraRotation = getTransform().getTransformedRot().conjugate().toRotationMatrix();
		Vector3f cameraPos = getTransform().getTransformedPos().mult(-1);

		Matrix4f cameraTranslation = new Matrix4f().initTranslation(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());

		return projection.mult(cameraRotation.mult(cameraTranslation));
	}
	
	public void setProjection(Matrix4f projection) { this.projection = projection; }
	
	@Override
	public void addToEngine(CoreEngine engine) {
		engine.getRenderingEngine().addCamera(this);
	}
	
	private Vector2f centerPos = new Vector2f(Window.getWidth()/2, Window.getHeight()/2);
	private boolean isMouseTrapped;
	
	@Override
	public void input(float delta) {
		float movAmnt = (float) (10 * delta);
		if (Input.getKey(Input.KEY_LEFT_SHIFT))
			movAmnt *= 5f;
		
		if (Input.getKey(Input.KEY_W))
			move(getTransform().getRot().getForward(), movAmnt);
		if (Input.getKey(Input.KEY_S))
			move(getTransform().getRot().getForward(), -movAmnt);
		if (Input.getKey(Input.KEY_A))
			move(getTransform().getRot().getLeft(), movAmnt);
		if (Input.getKey(Input.KEY_D))
			move(getTransform().getRot().getRight(), movAmnt);
		
		if (Input.getKey(Input.KEY_SPACE))
			move(getTransform().getRot().getUp(), movAmnt);
		if (Input.getKey(Input.KEY_LEFT_CONTROL))
			move(getTransform().getRot().getUp(), -movAmnt);
		
		if (Input.getKeyDown(Input.KEY_ESCAPE)) {
			isMouseTrapped = false;
			Input.setCursor(true);
		}
		
		if (Input.getMouseDown(0)) {
			isMouseTrapped = true;
			Input.setMousePosition(centerPos);
			Input.setCursor(false);
		}
		
		if (isMouseTrapped) {
			Vector2f deltaPos = Input.getMousePosition().sub(centerPos);
			boolean rotX = deltaPos.getX() != 0;
			boolean rotY = deltaPos.getY() != 0;
			float sens = 0.25f;
			
			if(rotX)
				getTransform().rotate(yAxis, (float) Math.toRadians(deltaPos.getX() * sens));
			if(rotY)
				getTransform().rotate(getTransform().getRot().getRight(), (float) Math.toRadians(deltaPos.getY() * sens));
				
			if(rotY || rotX)
				Input.setMousePosition(new Vector2f(Window.getWidth()/2, Window.getHeight()/2));
		}
	}
	
	public void move(Vector3f dir, float amount) { 
		getTransform().setPos(getTransform().getPos().add(dir.mult(amount))); 
	}	
}
