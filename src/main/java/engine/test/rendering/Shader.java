package engine.test.rendering;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import engine.test.components.BaseLight;
import engine.test.components.DirectionalLight;
import engine.test.components.PointLight;
import engine.test.components.SpotLight;
import engine.test.core.Transform;
import engine.test.core.Util;
import engine.test.core.math.matrices.Matrix4f;
import engine.test.core.math.vectors.Vector3f;
import engine.test.rendering.resourcemanagement.ShaderResource;

public class Shader {
	private static HashMap<String, ShaderResource> loadedShaders = new HashMap<>();
	
	private ShaderResource resource;
	private String fileName;
	
	public Shader(String fileName) {
		this.fileName = fileName;
		resource = new ShaderResource();

		ShaderResource oldResource = loadedShaders.get(fileName);
		if (oldResource != null) {
			resource = oldResource;
			resource.addReference();
		}
		else {
			resource = new ShaderResource();
			
			String vertexShaderText = loadShader(fileName + ".vs");
			String fragmentShaderText = loadShader(fileName + ".fs");
			
			addVertexShader(vertexShaderText);
			addFragmentShader(fragmentShaderText);
			
			addAllAttributes(vertexShaderText);
			
			compileShader();
			
			addAllUniforms(vertexShaderText);
			addAllUniforms(fragmentShaderText);
			
			loadedShaders.put(fileName, resource);
		}
	}

	public void cleanUp() {
		if (resource.removeReference()) {
			if (fileName != null) loadedShaders.remove(fileName);
			resource.cleanUp();
			System.out.println("Cleaning up Shader: " + fileName);
		}
	}
	
	public void bind() {
		glUseProgram(resource.getProgram());
	}
	
	public void updateUniforms(Transform transform, Material material, RenderingEngine renderingEngine) {
		Matrix4f worldMatrix = transform.getTransformation();
		Matrix4f MVPMatrix = renderingEngine.getMainCamera().getViewProjection().mult(worldMatrix);
		
		for (int i = 0; i < resource.getUniformNames().size(); i++) {
			String uniformName = resource.getUniformNames().get(i);
			String uniformType = resource.getUniformTypes().get(i);

			if (uniformName.startsWith("T_")) {
				if (uniformName.equals("T_MVP")) 
					setUniform(uniformName, MVPMatrix);
				else if (uniformName.equals("T_model"))
					setUniform(uniformName, worldMatrix);
				else
					throw new IllegalArgumentException(uniformName + " is not a valid component of Transform");
			}
			else if (uniformName.startsWith("R_")) {
				String unprefixedUniformName = uniformName.substring(2);
				
				if (unprefixedUniformName.equals("lightMatrix")) {
					setUniform(uniformName, renderingEngine.getLightMatrix().mult(worldMatrix));
				}
				else if (uniformType.equals("sampler2D")) {
					int samplerSlot = renderingEngine.getSamplerSlot(unprefixedUniformName);
					renderingEngine.getTexture(unprefixedUniformName).bind(samplerSlot);
					setUniformi(uniformName, samplerSlot);
				}
				else if (uniformType.equals("vec3"))
					setUniform(uniformName, renderingEngine.getVector3f(unprefixedUniformName));
				else if (uniformType.equals("float"))
					setUniformf(uniformName, renderingEngine.getFloat(unprefixedUniformName));
				else if (uniformType.equals("DirectionalLight"))
					setUniformDirectionalLight(uniformName, (DirectionalLight) renderingEngine.getActiveLight());
				else if (uniformType.equals("PointLight"))
					setUniformPointLight(uniformName, (PointLight) renderingEngine.getActiveLight());
				else if (uniformType.equals("SpotLight"))
					setUniformSpotLight(uniformName, (SpotLight) renderingEngine.getActiveLight());
				else
					throw new IllegalArgumentException(uniformName + " is not a valid type in Rendering Engine");
			}
			else if (uniformName.startsWith("C_")) {
				if (uniformName.equals("C_eyePos"))
					setUniform(uniformName, renderingEngine.getMainCamera().getTransform().getTransformedPos());
				else
					throw new IllegalArgumentException(uniformName + " is not a valid component of Camera");
			}
			else if (uniformType.equals("sampler2D")) {
				int samplerSlot = renderingEngine.getSamplerSlot(uniformName);
				material.getTexture(uniformName).bind(samplerSlot);
				setUniformi(uniformName, samplerSlot);
			}
			else {
				if (uniformType.equals("vec3"))
					setUniform(uniformName, material.getVector3f(uniformName));
				else if (uniformType.equals("float"))
					setUniformf(uniformName, material.getFloat(uniformName));
				else
					throw new IllegalArgumentException(uniformName + " is not a valid type in Material");
			}
		}
		
	}
	
	private class GLSLStruct {
		public String name;
		public String type;
	}
	
	private HashMap<String, ArrayList<GLSLStruct>> findUniformStructs(String shaderText) {
		HashMap<String, ArrayList<GLSLStruct>> res = new HashMap<>();
		
		final String STRUCT_WORD = "struct";
		int structStartLocation = shaderText.indexOf(STRUCT_WORD);
		
		while (structStartLocation != -1) {
			int nameStart = structStartLocation + STRUCT_WORD.length() + 1;
			int braceStart = shaderText.indexOf("{", nameStart);
			int braceEnd = shaderText.indexOf("}", braceStart);
			
			String structName = shaderText.substring(nameStart, braceStart - 1).trim();
			ArrayList<GLSLStruct> glslStructs = new ArrayList<>();

			int semicolonPos = shaderText.indexOf(";", braceStart);
			
			while (semicolonPos != -1 && semicolonPos < braceEnd) {
				int componentNameStart = semicolonPos;
				
				while (!Character.isWhitespace(shaderText.charAt(componentNameStart - 1)))
					componentNameStart--;
				
				int componentTypeEnd = componentNameStart - 1;
				int componentTypeStart = componentTypeEnd;
				while (!Character.isWhitespace(shaderText.charAt(componentTypeStart - 1)))
					componentTypeStart--;
				
				String componentName = shaderText.substring(componentNameStart, semicolonPos);
				String componentType = shaderText.substring(componentTypeStart, componentTypeEnd);

				GLSLStruct glslStruct = new GLSLStruct();
				glslStruct.name = componentName;
				glslStruct.type = componentType;
				
				glslStructs.add(glslStruct);
				
				semicolonPos = shaderText.indexOf(";", semicolonPos + 1);
			}

			res.put(structName, glslStructs);	
			structStartLocation = shaderText.indexOf(STRUCT_WORD, structStartLocation + STRUCT_WORD.length());
		}
		
		return res;
	}
	
	public void addAllAttributes(String shaderText) {
		final String ATTRIBUTE_WORD = "attribute";
		int attributeStartLocation = shaderText.indexOf(ATTRIBUTE_WORD);
		int attributeCounter = 0;
		
		while (attributeStartLocation != -1) {
			int begin = attributeStartLocation + ATTRIBUTE_WORD.length() + 1;
			int end = shaderText.indexOf(";", begin);
			
			String attributeLine = shaderText.substring(begin, end);
			
			//String attributeType = attributeLine.split(" ")[0];
			String attributeName = attributeLine.split(" ")[1];
			
			setAttribLocation(attributeName, attributeCounter);
			attributeCounter++;
			
			attributeStartLocation = shaderText.indexOf(ATTRIBUTE_WORD, attributeStartLocation + ATTRIBUTE_WORD.length());
		}
	}
	
	private void addAllUniforms(String shaderText) {
		
		HashMap<String, ArrayList<GLSLStruct>> structs = findUniformStructs(shaderText);
		
		final String UNIFORM_WORD = "uniform";
		int uniformStartLocation = shaderText.indexOf(UNIFORM_WORD);
		
		while (uniformStartLocation != -1) {
			int begin = uniformStartLocation + UNIFORM_WORD.length() + 1;
			int end = shaderText.indexOf(";", begin);
			
			String uniformLine = shaderText.substring(begin, end);
			
			String uniformType = uniformLine.split(" ")[0];
			String uniformName = uniformLine.split(" ")[1];
			
			resource.getUniformNames().add(uniformName);
			resource.getUniformTypes().add(uniformType);
			addUniformStruct(uniformName, uniformType, structs);
			
			uniformStartLocation = shaderText.indexOf(UNIFORM_WORD, uniformStartLocation + UNIFORM_WORD.length());
		}
	}
	
	private void addUniformStruct(String uniformName, String uniformType, HashMap<String, ArrayList<GLSLStruct>> structs) {
		boolean addThis = true;
		ArrayList<GLSLStruct> structComponents = structs.get(uniformType);
		
		if (structComponents != null) {
			addThis = false;
			
			for (GLSLStruct struct : structComponents) {
				addUniformStruct(uniformName + "." + struct.name, struct.type, structs);
			}
		}
		
		if (!addThis) return;

		int uniformLocation = glGetUniformLocation(resource.getProgram(), uniformName);
		
		if (uniformLocation == 0xFFFFFFFF) {
			System.err.println("Error: Couldn't find uniform: " + uniformName);
			new Exception().printStackTrace();
			System.exit(1);
		}
		
		resource.getUniforms().put(uniformName, uniformLocation);
	}
	
	public void addVertexShaderFromFile(String fileName) {
		addProgram(loadShader(fileName), GL_VERTEX_SHADER);
	}
	
	public void addGeometryShaderFromFile(String fileName) {
		addProgram(loadShader(fileName), GL_GEOMETRY_SHADER);
	}
	
	public void addFragmentShaderFromFile(String fileName) {
		addProgram(loadShader(fileName), GL_FRAGMENT_SHADER);
	}
	
	public void setAttribLocation(String name, int location) {
		glBindAttribLocation(resource.getProgram(), location, name);
	}
	
	public void addVertexShader(String text)
	{
		addProgram(text, GL_VERTEX_SHADER);
	}
	
	public void addGeometryShader(String text)
	{
		addProgram(text, GL_GEOMETRY_SHADER);
	}
	
	public void addFragmentShader(String text)
	{
		addProgram(text, GL_FRAGMENT_SHADER);
	}
	
	public void compileShader() {
		glLinkProgram(resource.getProgram());
		
		if (glGetProgrami(resource.getProgram(), GL_LINK_STATUS) == 0) {
			System.err.println(glGetShaderInfoLog(resource.getProgram(), 1024));
			System.exit(1);
		}
		
		glValidateProgram(resource.getProgram());
		
		if (glGetProgrami(resource.getProgram(), GL_VALIDATE_STATUS) == 0) {
			System.err.println(glGetShaderInfoLog(resource.getProgram(), 1024));
			System.exit(1);
		}
	}
	
	private void addProgram(String text, int type) {
		int shader = glCreateShader(type);
		
		if (shader == 0) {
			System.err.println("Shader creation failed: Couldn't find valid memory location while adding to program");
			System.exit(1);
		}
		
		glShaderSource(shader, text);
		glCompileShader(shader);
		
		if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
			System.err.println(glGetShaderInfoLog(shader, 1024));
			System.exit(1);
		}
		
		glAttachShader(resource.getProgram(), shader);
	}
	
	public void setUniformi(String uniform, int value) {
		glUniform1i(resource.getUniforms().get(uniform), value);
	}
	
	public void setUniformf(String uniform, float value) {
		glUniform1f(resource.getUniforms().get(uniform), value);
	}
	
	public void setUniform(String uniform, Vector3f value) {
		glUniform3f(resource.getUniforms().get(uniform), value.getX(), value.getY(), value.getZ());
	}
	
	public void setUniform(String uniform, Matrix4f value) {
		glUniformMatrix4fv(resource.getUniforms().get(uniform), true, Util.createFlippedBuffer(value));
	}
	
	public void setUniformBaseLight(String uniform, BaseLight baseLight) {
		setUniform(uniform + ".color", baseLight.getColor());
		setUniformf(uniform + ".intensity", baseLight.getIntensity());
	}
	
	public void setUniformDirectionalLight(String uniform, DirectionalLight directionalLight) {
		setUniformBaseLight(uniform + ".base", directionalLight);
		setUniform(uniform + ".direction", directionalLight.getDirection());
	}
	
	public void setUniformPointLight(String uniform, PointLight pointLight) {
		setUniformBaseLight(uniform + ".base", pointLight);
		setUniformf(uniform + ".atten.constant", pointLight.getAttenuation().getConstant());
		setUniformf(uniform + ".atten.linear", pointLight.getAttenuation().getLinear());
		setUniformf(uniform + ".atten.exponent", pointLight.getAttenuation().getExponent());
		setUniform(uniform + ".position", pointLight.getTransform().getTransformedPos());
		setUniformf(uniform + ".range", pointLight.getRange());
	}
	
	public void setUniformSpotLight(String uniform, SpotLight spotLight) {
		setUniformPointLight(uniform + ".pointLight", spotLight);
		setUniform(uniform + ".direction", spotLight.getDirection());
		setUniformf(uniform + ".cutoff", spotLight.getCutoff());
	}
	
	public static String loadShader(String fileName) {
		StringBuilder shaderSource = new StringBuilder();
		BufferedReader reader = null;
		
		final String INCLUDE_LEX = "#include";
		
		try {
			reader = new BufferedReader(new FileReader("./res/shaders/" + fileName));
			String line;
			while ((line = reader.readLine()) != null) {
				
				if (line.startsWith(INCLUDE_LEX)) {
					line = loadShader(line.substring(INCLUDE_LEX.length() + 2, line.length() - 1));
				}

				shaderSource.append(line).append("\n");
			}
			
			reader.close();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return shaderSource.toString();
	}
}
