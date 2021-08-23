package engine.test.rendering.resourcemanagement;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class MeshResource extends ReferenceCounter {

	private int vbo;
	private int vao;
	private int size;
	
	public MeshResource(int size) {
		this.vbo = glGenBuffers();
		this.vao = glGenBuffers();
		this.size = size;
	}
	
	public void cleanUp() {
		glDeleteBuffers(vbo);
		glDeleteVertexArrays(vbo);
		glDeleteBuffers(vao);
		glDeleteVertexArrays(vao);
	}
	
	public int getVbo() {
		return vbo;
	}

	public int getVao() {
		return vao;
	}

	public int getSize() {
		return size;
	}
		
}
