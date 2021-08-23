package engine.test.components;

import engine.test.rendering.Material;
import engine.test.rendering.Mesh;
import engine.test.rendering.RenderingEngine;
import engine.test.rendering.Shader;

public class MeshRenderer extends GameComponent {

	private Mesh mesh;
	private Material material;
	
	public MeshRenderer(Mesh mesh, Material material) {
		this.mesh = mesh;
		this.material = material;
	}
	
	@Override
	public void render(Shader shader, RenderingEngine renderingEngine) {
		shader.bind();
		shader.updateUniforms(getTransform(), material, renderingEngine);
		mesh.draw();
	}

}
