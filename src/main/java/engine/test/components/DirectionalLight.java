package engine.test.components;

import engine.test.core.math.matrices.Matrix4f;
import engine.test.core.math.quads.Quad;
import engine.test.core.math.vectors.Vector3f;
import engine.test.rendering.Shader;

public class DirectionalLight extends BaseLight {
	
	private float halfShadowArea;
	
	public DirectionalLight(Vector3f color, float intensity, int shadowMapSizeBaseTwo, 
			float shadowArea, float shadowSoftness, float lightBleedReduction, float minVariance) {
		super(color, intensity);
		setShader(new Shader("forward-directional"));
		
		this.halfShadowArea = shadowArea/2;
		if (shadowMapSizeBaseTwo != 0)
			setShadowInfo(new ShadowInfo(new Matrix4f().initOrthographic(-halfShadowArea, halfShadowArea, -halfShadowArea, halfShadowArea, -halfShadowArea, halfShadowArea),
										 true, shadowMapSizeBaseTwo, shadowSoftness, lightBleedReduction, minVariance));
	}
	
	public DirectionalLight(Vector3f color, float intensity) {
		this(color, intensity, 10, 80, 1, 0.2f, 0.00002f);
	}
	
	@Override
	public ShadowCameraTransform calcShadowCameraTransform(Vector3f cameraPos, Quad cameraRot) {
		ShadowCameraTransform res = new ShadowCameraTransform();
		res.pos = cameraPos.add(cameraRot.getForward().mult(halfShadowArea));
		res.rot = getTransform().getTransformedRot();
		
		float worldTexelSize = (halfShadowArea * 2)/(float) (1<<getShadowInfo().getShadowMapSizeBaseTwo());
		Vector3f lightCameraPos = res.pos.rotate(res.rot.conjugate());
		
		lightCameraPos.setX((float) (worldTexelSize * Math.floor(lightCameraPos.getX()/worldTexelSize)));
		lightCameraPos.setY((float) (worldTexelSize * Math.floor(lightCameraPos.getY()/worldTexelSize)));
		
		res.pos = lightCameraPos.rotate(res.rot);
		return res;
	}
	
	public Vector3f getDirection() {
		return getTransform().getTransformedRot().getForward();
	}	
} 
