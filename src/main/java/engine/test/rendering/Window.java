package engine.test.rendering;

import static org.lwjgl.opengl.GL30.*;

import java.nio.IntBuffer;
import java.util.Arrays;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.opengl.WGLAMDGPUAssociation;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.Callbacks.*;

import engine.test.core.Util;
import engine.test.core.math.vectors.Vector2f;

public class Window {

	private static long window;
	
	public static void createWindow(int width, int height, String title) {
		GLFWErrorCallback.createPrint(System.err).set();
		glfwInit();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
//		glfwWindowHint(GLFW_STENCIL_BITS, 4); //MSAA
//		glfwWindowHint(GLFW_SAMPLES, 4);	  //
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
		
		window = glfwCreateWindow(width, height, title, 0, 0);
		
		if(window == 0) {
		    throw new RuntimeException("Failed to create window");
		}
		
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		GLUtil.setupDebugMessageCallback(System.err);
		
		glfwShowWindow(window);
	}
	
	public static void render() {
		glfwPollEvents();
		glfwSwapBuffers(window);
	}
	
	public static void dispose() {
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		GLUtil.setupDebugMessageCallback().free();
		GL.setCapabilities(null);
	}
	
	public static void bindAsRenderTarget() {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
		glViewport(0, 0, getWidth(), getHeight());
	}
	
	public static boolean isCloseRequested() {
		return glfwWindowShouldClose(window);
	}
	
	public static int getWidth() {
		IntBuffer w = Util.createIntBuffer(1);
		glfwGetWindowSize(window, w, null);
		return w.get(0);
	}
	
	public static int getHeight() {
		IntBuffer h = Util.createIntBuffer(1);
		glfwGetWindowSize(window, null, h);
		return h.get(0);
	}
	
	public static long getWindow() { return window; }
	
	public Vector2f getCenterPos() {
		return new Vector2f(getWidth()/2, getHeight()/2);
	}
}
