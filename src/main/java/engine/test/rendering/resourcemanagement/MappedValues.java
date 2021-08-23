package engine.test.rendering.resourcemanagement;

import java.util.HashMap;

import engine.test.core.math.vectors.Vector3f;
import engine.test.rendering.Texture;

public abstract class MappedValues {

	private HashMap<String, Vector3f> vectorMap;
	private HashMap<String, Float> floatMap;
	private HashMap<String, Texture> textureMap;
	
	public MappedValues() {
		vectorMap = new HashMap<>();
		floatMap = new HashMap<>();
		this.textureMap = new HashMap<>();
	}
	
	public void setVector3f(String name, Vector3f vector) { vectorMap.put(name, vector); }
	public void setFloat(String name, float num) { floatMap.put(name, num); }
	public void setTexture(String name, Texture texture) { textureMap.put(name, texture); }
	
	public Vector3f getVector3f(String name) { 
		if (!vectorMap.containsKey(name)) return new Vector3f(0,0,0);
		return vectorMap.get(name); 
	}
	
	public float getFloat(String name) { 
		if (!floatMap.containsKey(name)) return 0f;
		return floatMap.get(name); 
	}

	public Texture getTexture(String name) { 
		if (!textureMap.containsKey(name)) return new Texture("test.png");
		return textureMap.get(name); 
	}
}
