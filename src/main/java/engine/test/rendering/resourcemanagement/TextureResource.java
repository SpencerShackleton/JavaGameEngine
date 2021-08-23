package engine.test.rendering.resourcemanagement;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL30;

import engine.test.core.Util;
import engine.test.rendering.RenderingEngine;

public class TextureResource extends ReferenceCounter {

	private IntBuffer id;
	private int textureTarget;
	private int frameBuffer;
	private int renderBuffer;
	private int numTextures;
	private int width;
	private int height;
	
	public TextureResource(int textureTarget, int width, int height, int numTextures, 
			   			   ByteBuffer data, int filters, int attachments) {
		this(textureTarget, width, height, numTextures, 
				data, filters, attachments, 
				GL_RGBA, GL_RGBA, false);
	}
	
	public void cleanUp() {
		glDeleteTextures(id);
		if (frameBuffer != 0) glDeleteFramebuffers(frameBuffer);
		if (renderBuffer != 0) glDeleteRenderbuffers(renderBuffer);
	}
	
	public TextureResource(int textureTarget, int width, int height, int numTextures, 
						   ByteBuffer data, int filters, int attachments, 
						   int internalFormat, int format, boolean clamp) {
		this.textureTarget = textureTarget;
		this.numTextures = numTextures;
		this.width = width;
		this.height = height;
		this.frameBuffer = 0;
		this.renderBuffer = 0;
		
		initTextures(data, filters, internalFormat, format, clamp);
		initRenderTargets(attachments);
		
	}
	
	private void initRenderTargets(int attachments) {
		if (attachments == 0)
			return;
		
		boolean hasDepth = false;
		int[] drawBuffers = new int[numTextures];
		
		for (int i = 0; i < numTextures; i++) {
			if (attachments == GL_DEPTH_ATTACHMENT) {
				drawBuffers[i] = GL_NONE;
				hasDepth = true;
			}
			else drawBuffers[i] = attachments;
			if (attachments == GL_NONE)
				continue;
			
			if (frameBuffer == 0) {
				frameBuffer = glGenFramebuffers();
				glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);

				glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, attachments, textureTarget, id.get(i), 0);
			}
		}
		
		if (frameBuffer == 0)
			return;
		
		if (!hasDepth) {
			renderBuffer = glGenRenderbuffers();
			glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer);
			glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height);
			glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderBuffer);
		}
		
		glDrawBuffers(drawBuffers);
		
		if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
		{
			throw new RuntimeException("Shadow framebuffer creation has failed");
		}
	}
	
	private void initTextures(ByteBuffer data, int filter, int internalFormat, int format, boolean clamp) {
		this.id = allocateTextures();

		for (int i = 0; i < numTextures; i++) {
			glBindTexture(textureTarget, id.get(i));

			glTexParameterf(textureTarget, GL_TEXTURE_MIN_FILTER, filter);
			glTexParameterf(textureTarget, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

			if (clamp) {
				glTexParameteri(textureTarget, GL_TEXTURE_WRAP_S, GL_CLAMP);
				glTexParameteri(textureTarget, GL_TEXTURE_WRAP_T, GL_CLAMP);
			}
			
			glTexImage2D(textureTarget, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, data);

			if (filter == GL_NEAREST_MIPMAP_NEAREST || 
				filter == GL_NEAREST_MIPMAP_LINEAR || 
				filter == GL_LINEAR_MIPMAP_NEAREST || 
				filter == GL_LINEAR_MIPMAP_LINEAR) {
				
				glGenerateMipmap(textureTarget);
				
				FloatBuffer maxAnisotropy = Util.createFloatBuffer(1);
				glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy);

				glTexParameterf(textureTarget, GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy.get(0));
			} else {
				glTexParameteri(textureTarget, GL_TEXTURE_BASE_LEVEL, 0);
				glTexParameteri(textureTarget, GL_TEXTURE_MAX_LEVEL, 0);
			}
		}
	}
	
	private IntBuffer allocateTextures() {
		IntBuffer buff = Util.createIntBuffer(numTextures);
		glGenTextures(buff);
		return buff;
	}
	
	public void bindAsRenderTarget() {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBuffer);
		glViewport(0, 0, width, height);
	}
	
	public void bind(int textureNum) {
		glBindTexture(textureTarget, id.get(textureNum));
	}

	public int getId() { return id.get(0); }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
}
