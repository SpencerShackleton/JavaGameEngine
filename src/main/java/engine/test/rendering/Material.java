package engine.test.rendering;

import java.util.HashMap;

import engine.test.rendering.resourcemanagement.MappedValues;

public class Material extends MappedValues {

	public Material(Texture diffuse, float specularIntensity, float specularPower) {
		this(diffuse, specularIntensity, specularPower, new Texture("default_normal.jpg"));
	}
	
	public Material(Texture diffuse, float specularIntensity, float specularPower, Texture normalMap) {
		this(diffuse, specularIntensity, specularPower, 
				normalMap, new Texture("default_disp.png"), 0f, 0f);
	}
	
	public Material(Texture diffuse, float specularIntensity, float specularPower,
			Texture normalMap, Texture dispMap, float dispMapScale, float dispMapOffset) {
		super();
		setTexture("diffuse", diffuse);
		setFloat("specularIntensity", specularIntensity);
		setFloat("specularPower", specularPower);
		setTexture("normalMap", normalMap);
		setTexture("dispMap", dispMap);
		
		float baseBias = dispMapScale/2f;
		setFloat("dispMapScale", dispMapScale);
		setFloat("dispMapBias", -baseBias + baseBias * dispMapOffset);

	}

}
