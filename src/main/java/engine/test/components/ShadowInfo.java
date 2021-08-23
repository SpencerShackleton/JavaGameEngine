package engine.test.components;

import engine.test.core.math.matrices.Matrix4f;

public class ShadowInfo {

	private Matrix4f projection;
	private boolean flipFaces;
	private float shadowSoftness;
	private float lightBleedReduction;
	private float varianceMin;
	private int shadowMapSizeBaseTwo;
	
	public ShadowInfo(Matrix4f projection, boolean flipFaces, int shadowMapSizeBaseTwo,
					  float shadowSoftness, float lightBleedReduction, float varianceMin) {
		this.projection = projection;
		this.flipFaces = flipFaces;
		this.shadowSoftness = shadowSoftness;
		this.lightBleedReduction = lightBleedReduction;
		this.varianceMin = varianceMin;
		this.shadowMapSizeBaseTwo = shadowMapSizeBaseTwo;
	}
	
	public ShadowInfo(Matrix4f projection, boolean flipFaces) {
		this(projection, flipFaces, 10, 1.0f, 0.2f, 0.00002f);
	}
	
	public Matrix4f getProjection() { return this.projection; }
	public boolean getFlipFaces() { return this.flipFaces; }
	public float getShadowSoftness() { return shadowSoftness; }
	public float getLightBleedReduction() { return lightBleedReduction; }
	public float getVarianceMin() { return varianceMin; }
	public int getShadowMapSizeBaseTwo() { return shadowMapSizeBaseTwo; }
}
