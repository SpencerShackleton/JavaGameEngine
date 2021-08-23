package engine.test.game;

import engine.test.core.CoreEngine;

public class Main {

	public static void main(String[] args) {
		
		CoreEngine engine = new CoreEngine(1600, 1200, 144, new TestGame());
		
		engine.createWindow("3D Game Engine");
		engine.start();
	}

}
