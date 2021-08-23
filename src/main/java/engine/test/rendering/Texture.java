package engine.test.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import engine.test.core.Util;
import engine.test.rendering.resourcemanagement.TextureResource;

public class Texture {

	private static HashMap<String, TextureResource> loadedTextures = new HashMap<>();
	
	private TextureResource resource;
	private String fileName;
	
	public Texture(String fileName) {
		this.fileName = fileName;
		
		TextureResource oldResource = loadedTextures.get(fileName);
		if (oldResource != null) {
			resource = oldResource;
			resource.addReference();
		}
		else {
			resource = loadTexture(fileName);
			loadedTextures.put(fileName, resource);
		}
	}
	
	public Texture(int width, int height, ByteBuffer data, 
				   int textureTarget, int filter, int attachment, 
				   int internalFormat, int format, boolean clamp) {
		resource = new TextureResource(textureTarget, width, height, 1, data, filter, attachment, internalFormat, format, clamp);
	}
	
	public void cleanUp() {
		if (resource.removeReference()) {
			if (fileName != null) loadedTextures.remove(fileName);
			resource.cleanUp();
			System.out.println("Cleaning up Texture: " + fileName);
		}
	}
	
	public void bind() {
		bind(0);
	}
	
	public void bind(int samplerSlot) {
		assert(samplerSlot >= 0 && samplerSlot <= 31);
		glActiveTexture(GL_TEXTURE0 + samplerSlot);
		resource.bind(0);
	}

	public void bindAsRenderTarget() {
		resource.bindAsRenderTarget();
	}
	
	public int getId() { return resource.getId(); }
	public int getWidth() { return resource.getWidth(); }
	public int getHeight() { return resource.getHeight(); }
	
	private static TextureResource loadTexture(String fileName) {
		
		try {
			BufferedImage image = ImageIO.read(new File("./res/textures/" + fileName));
			int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
			
			ByteBuffer buffer = Util.createByteBuffer(image.getWidth() * image.getWidth() * 4);
			
			boolean hasAlpha = image.getColorModel().hasAlpha();
			
			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {
					int pixel = pixels[y * image.getWidth() + x];
					
					buffer.put((byte) ((pixel >> 16) & 0xFF));
					buffer.put((byte) ((pixel >> 8) & 0xFF));
					buffer.put((byte) ((pixel) & 0xFF));
					if (hasAlpha)
						buffer.put((byte) ((pixel >> 24) & 0xFF));
					else
						buffer.put((byte) 0xFF);
				}
			}

			buffer.flip();
			
			return new TextureResource(GL_TEXTURE_2D, image.getWidth(), image.getHeight(), 1, buffer, GL_LINEAR_MIPMAP_LINEAR, GL_NONE);
			
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
}
