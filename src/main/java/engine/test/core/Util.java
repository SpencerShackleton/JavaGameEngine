package engine.test.core;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;

import engine.test.core.math.matrices.Matrix4f;
import engine.test.rendering.Vertex;

public class Util {

	public static FloatBuffer createFloatBuffer(int size) {
		return BufferUtils.createFloatBuffer(size);
	}

	public static IntBuffer createIntBuffer(int size) {
		return BufferUtils.createIntBuffer(size);
	}
	
	public static DoubleBuffer createDoubleBuffer(int size) {
		return BufferUtils.createDoubleBuffer(size);
	}
	
	public static ByteBuffer createByteBuffer(int size) {
		return BufferUtils.createByteBuffer(size);
	}

	public static IntBuffer createFlippedBuffer(int[] values) {
		IntBuffer buffer = createIntBuffer(values.length);
		buffer.put(values);
		buffer.flip();

		return buffer;
	}

	public static FloatBuffer createFlippedBuffer(Vertex[] vertices) {
		FloatBuffer buffer = createFloatBuffer(vertices.length * Vertex.SIZE);
		for (int i = 0; i < vertices.length; i++) {
			buffer.put(vertices[i].getPos().getAsFloatArray());
			buffer.put(vertices[i].getTexCoord().getAsFloatArray());
			buffer.put(vertices[i].getNormal().getAsFloatArray());
			buffer.put(vertices[i].getTangent().getAsFloatArray());
		}

		buffer.flip();

		return buffer;
	}

	public static FloatBuffer createFlippedBuffer(Matrix4f value) {
		FloatBuffer buffer = createFloatBuffer(4 * 4);
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				buffer.put(value.get(i, j));
			}
		}

		buffer.flip();

		return buffer;
	}

	public static String[] removeEmptyStrings(String[] tokens) {
		ArrayList<String> res = new ArrayList<>();
		
		for (int i = 0; i < tokens.length; i++) {
			if (!tokens[i].equals(""))
				res.add(tokens[i]);
		}
		String[] result = new String[res.size()];
		res.toArray(result);
		
		return result;
	}

	public static int[] integerToIntArray(Integer[] data) {
		
		int[] res = new int[data.length];
		for (int i = 0; i < data.length; i++) 
			res[i] = data[i].intValue();
		
		return res;
	}

}
