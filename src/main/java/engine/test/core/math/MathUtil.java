package engine.test.core.math;

public class MathUtil {

	public static double roundPlaces(double input, int places) {
		int rounding = (int) Math.pow(10, places);
		return (double)((int)(input * rounding))/rounding;
	}
	
	public static long roundPlaces(long input, int places) {
		int rounding = (int) Math.pow(10, places);
		return (long)((int)(input * rounding))/rounding;
	}
}
