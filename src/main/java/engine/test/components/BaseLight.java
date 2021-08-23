package engine.test.components;

import engine.test.core.CoreEngine;
import engine.test.core.math.quads.Quad;
import engine.test.core.math.vectors.Vector3f;
import engine.test.rendering.Shader;

public abstract class BaseLight extends GameComponent {

	private Vector3f color;
	private float intensity;
	private Shader shader;
	private ShadowInfo shadowInfo;

	public BaseLight(Vector3f color, float intensity) {
		this.color = color;
		this.intensity = intensity;
	}
	
	@Override
	public void addToEngine(CoreEngine engine) {
		engine.getRenderingEngine().addLight(this);
	}
	
	public ShadowCameraTransform calcShadowCameraTransform(Vector3f cameraPos, Quad cameraRot) {
		ShadowCameraTransform res = new ShadowCameraTransform();
		res.pos = getTransform().getTransformedPos();
		res.rot = getTransform().getTransformedRot();
		return res;
	}
	
	public float getIntensity() { return intensity; }
	public Vector3f getColor() { return color; }
	public Shader getShader() { return shader; }
	public ShadowInfo getShadowInfo() { return shadowInfo; }
	
	public void setShader(Shader shader) {
		this.shader = shader;
	}
	
	public void setShadowInfo(ShadowInfo shadowInfo) {
		this.shadowInfo = shadowInfo;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}	
	
	public class ShadowCameraTransform {
		public Vector3f pos;
		public Quad rot;
	}
}
