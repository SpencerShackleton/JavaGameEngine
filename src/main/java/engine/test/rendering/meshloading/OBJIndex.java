package engine.test.rendering.meshloading;

public class OBJIndex {

	public int vertexIndex;
	public int texCoordIndex;
	public int normalIndex;
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OBJIndex) {
			OBJIndex index = (OBJIndex) obj;
			return vertexIndex == index.vertexIndex &&
					texCoordIndex == index.texCoordIndex &&
					normalIndex == index.normalIndex;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		final int BASE = 17;
		final int MULT = 31;
		
		int res = BASE;
		
		res = MULT * res + vertexIndex;
		res = MULT * res + texCoordIndex;
		res = MULT * res + normalIndex;
		
		return res;
	}
}
