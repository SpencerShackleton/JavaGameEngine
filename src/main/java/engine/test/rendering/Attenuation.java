package engine.test.rendering;

import engine.test.core.math.vectors.Vector3f;

public class Attenuation extends Vector3f {

	public Attenuation(float constant, float linear, float power) {
		super(constant, linear, power);
	}

	public float getConstant() { return getX(); } 
	public float getLinear() { return getY(); } 
	public float getExponent() { return getZ(); } 
}
