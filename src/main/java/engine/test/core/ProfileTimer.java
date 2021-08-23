package engine.test.core;

import org.lwjgl.glfw.GLFW;

import engine.test.core.math.MathUtil;

public class ProfileTimer {

	private int numInvocations;
	private double totalTime;
	
	public ProfileTimer() {
		numInvocations = 0;
		totalTime = 0;
	}
	
	public void startInvocation() {
		GLFW.glfwSetTime(0.0);
	}
	
	public void stopInvocation() {
		numInvocations++;
		totalTime += GLFW.glfwGetTime();
	}
	
	private double getTimeAndReset(double dividend) {
		dividend = dividend == 0 ? numInvocations : dividend;
		double res = totalTime == 0 && dividend == 0 ? 0 : (1000.0d * totalTime) / (dividend);
		totalTime = 0;
		numInvocations = 0;
		return res;
	}
	
	public double displayAndReset(String msg, double dividend, int lineLength) {
		String whitespace = "";
		for (int i = msg.length(); i < lineLength; i++)
			whitespace += " ";
		double time = getTimeAndReset(dividend);
		System.out.println(msg + whitespace + MathUtil.roundPlaces(time, 3) + " ms");
		return time;
	}
	
	public double displayAndReset(String msg, double dividend) {
		return displayAndReset(msg, dividend, 20);
	}
	
	public double displayAndReset(String msg) { return this.displayAndReset(msg, 0); }
}
