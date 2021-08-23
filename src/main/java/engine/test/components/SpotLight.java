package engine.test.components;

import engine.test.core.math.matrices.Matrix4f;
import engine.test.core.math.vectors.Vector3f;
import engine.test.rendering.Attenuation;
import engine.test.rendering.Shader;

public class SpotLight extends PointLight {

	private float cutoff;

	public SpotLight(Vector3f color, float intensity, Attenuation atten, float viewAngle,
					 int shadowMapSizeBaseTwo, float shadowSoftness, float lightBleedReduction, float minVariance) {
		super(color, intensity, atten);
		this.cutoff = (float) Math.cos(viewAngle/2);
		setShader(new Shader("forward-spot"));
		if (shadowMapSizeBaseTwo != 0)
			setShadowInfo(new ShadowInfo(new Matrix4f().initPerspective(viewAngle, 1, 0.1f, this.getRange()), false, shadowMapSizeBaseTwo, shadowSoftness, lightBleedReduction, minVariance));
	}
	
	public SpotLight(Vector3f color, float intensity, Attenuation atten, float viewAngle,
			 int shadowMapSizeBaseTwo) {
		this(color, intensity, atten, viewAngle, shadowMapSizeBaseTwo, 1.0f, 0.2f, 0.00002f);
	}

	public Vector3f getDirection() { return getTransform().getTransformedRot().getForward(); }

	public float getCutoff() { return cutoff; }
	public void setCutoff(float cutoff) { this.cutoff = cutoff; }

}
