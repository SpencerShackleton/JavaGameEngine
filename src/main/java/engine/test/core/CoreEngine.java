package engine.test.core;

import static org.lwjgl.opengl.GL11.glClearColor;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;

import engine.test.core.math.MathUtil;
import engine.test.rendering.RenderingEngine;
import engine.test.rendering.Window;

public class CoreEngine {
	
	private boolean isRunning;
	private Game game;
	private RenderingEngine renderingEngine;
	private ProfileTimer sleepTimer;
	private ProfileTimer windowTimer;

	private int width, height;
	private double frametime;
	
	public CoreEngine(int width, int height, double maxFps, Game game) {
		
		this.isRunning = false;
		this.game = game;
		this.width = width;
		this.height = height;
		this.frametime = 1/maxFps;
		this.sleepTimer = new ProfileTimer();
		this.windowTimer = new ProfileTimer();
		game.setEngine(this);
	}
	
	public void createWindow(String title) {
		Window.createWindow(width, height, title);
		this.renderingEngine = new RenderingEngine();
	}
	
	public void start() {
		if (isRunning)
			return;

		run();
	}
	
	public void stop() {
		if (!isRunning)
			return;
		isRunning = false;
	}
	
	private void run() {
		
		isRunning = true;
		
		int frames = 0;
		double frameCounter = 0;
		
		game.init();

		double lastTime = Time.getTime();
		double unprocessedTime = 0;

		while(isRunning) {
			boolean render = false;
			
			double startTime = Time.getTime();
			double passedTime = startTime - lastTime;
			lastTime = startTime; 
			
			unprocessedTime += passedTime;
			frameCounter += passedTime;
			
			if (frameCounter >= 1.0) {
				double totalTime = (1000.0 * frameCounter) / (double) frames;
				double totalRecordedTime = 0;
				totalRecordedTime += game.displayInputTime(frames);
				totalRecordedTime += game.displayUpdateTime(frames);
				totalRecordedTime += renderingEngine.displayRenderTime(frames);
				totalRecordedTime += renderingEngine.displayWindowSyncTime(frames);
				totalRecordedTime += windowTimer.displayAndReset("Window Update Time: ", frames);
				totalRecordedTime += sleepTimer.displayAndReset("Sleep Time: ", frames);
				System.out.println("Non-Profiled Time:  " + MathUtil.roundPlaces(totalTime - totalRecordedTime, 3) + " ms");
				System.out.println("Total Time:         " + MathUtil.roundPlaces(totalTime, 3) + " ms");
				System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				frames = 0;
				frameCounter = 0;
			}
			
			while (unprocessedTime > frametime) {
				render = true;
				
				unprocessedTime -= frametime;
				
				if (Window.isCloseRequested())
					stop();

				game.input((float) frametime);

				Input.update();
				
				game.update((float) frametime);
			}
			
			if (render) {
				game.render(renderingEngine);
				
				windowTimer.startInvocation();
				Window.render();
				windowTimer.stopInvocation();
				
				int code = GL11.glGetError();
				if (code != GL11.GL_NO_ERROR) {
					throw new RuntimeException("GL error " + code);
				}
				frames++;
			}
			else {
				try {
					sleepTimer.startInvocation();
					Thread.sleep(0, 100);
					sleepTimer.stopInvocation();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		cleanUp();
	}
	
	private void cleanUp() {
		renderingEngine.cleanUp();
		Window.dispose();
	}
	
	public RenderingEngine getRenderingEngine() {
		return renderingEngine;
	}
}
