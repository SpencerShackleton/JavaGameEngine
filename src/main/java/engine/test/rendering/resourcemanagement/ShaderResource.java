package engine.test.rendering.resourcemanagement;

import static org.lwjgl.opengl.GL20.*;

import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL15.*;

public class ShaderResource extends ReferenceCounter {

	private int program;
	private HashMap<String, Integer> uniforms;
	private ArrayList<String> uniformNames;
	private ArrayList<String> uniformTypes;	
	
	public ShaderResource() {
		super();
		this.program = glCreateProgram();
		this.uniforms = new HashMap<>();
		this.uniformNames = new ArrayList<>();
		this.uniformTypes = new ArrayList<>();
		
		if(program == 0) {
			System.err.println("Shader creation failed: Couldn't find valid memory location in constructor");
			System.exit(1);
		}
	}
	
	public void cleanUp() {
		glDeleteProgram(program);
	}

	public int getProgram() { return program; }
	
	public HashMap<String, Integer> getUniforms() {
		return uniforms;
	}

	public ArrayList<String> getUniformNames() {
		return uniformNames;
	}

	public ArrayList<String> getUniformTypes() {
		return uniformTypes;
	}
}
