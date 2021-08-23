package engine.test.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL40;

import engine.test.components.BaseLight;
import engine.test.components.BaseLight.ShadowCameraTransform;
import engine.test.components.Camera;
import engine.test.components.ShadowInfo;
import engine.test.core.GameObject;
import engine.test.core.ProfileTimer;
import engine.test.core.Transform;
import engine.test.core.Util;
import engine.test.core.math.matrices.Matrix4f;
import engine.test.core.math.quads.Quad;
import engine.test.core.math.vectors.Vector2f;
import engine.test.core.math.vectors.Vector3f;
import engine.test.rendering.resourcemanagement.MappedValues;

public class RenderingEngine extends MappedValues {
	
	private static final Matrix4f biasMatrix = new Matrix4f().initScale(0.5f, 0.5f, 0.5f).mult(new Matrix4f().initTranslation(1.0f, 1.0f, 1.0f));
	private static final int numShadowMaps = 10;
	
	private HashMap<String, Integer> samplerMap;
	private ArrayList<BaseLight> lights;
	private BaseLight activeLight;

	private Shader forwardAmbient;	
	private Shader shadowMapShader;	
	private Shader nullFilter;
	private Shader gausBlurFilter;
	private Shader fxaaFilter;
	
	private Matrix4f lightMatrix;
	
	private ArrayList<Texture> shadowMaps = new ArrayList<>(numShadowMaps);
	private ArrayList<Texture> shadowMapTempTargets = new ArrayList<>(numShadowMaps);
	
	private ProfileTimer profileTimer;
	private ProfileTimer windowSyncprofileTimer;
	
	private Camera mainCamera;
	
	Texture tempTarget;
	Mesh plane;
	Transform planeTransform = new Transform();
	Material planeMaterial;
	Camera altCamera;
	GameObject altCameraObject;
	
	public RenderingEngine() {
		super();
		this.lights = new ArrayList<>();
		this.samplerMap = new HashMap<>();
		samplerMap.put("diffuse", 0);
		samplerMap.put("normalMap", 1);
		samplerMap.put("dispMap", 2);
		samplerMap.put("shadowMap", 3);
		
		setVector3f("ambient", new Vector3f(0.1f, 0.1f, 0.1f));
		
		setTexture("displayTexture", new Texture(Window.getWidth(), Window.getHeight(), null, GL_TEXTURE_2D, GL_LINEAR, GL_COLOR_ATTACHMENT0, GL_RGBA, GL_RGBA, false));
		setFloat("fxaaSpanMax", 8.0f);
		setFloat("fxaaReduceMin", 1.0f/128.0f);
		setFloat("fxaaReduceMul", 1.0f/8.0f);
		
		forwardAmbient = new Shader("forward-ambient");
		shadowMapShader = new Shader("shadowMapGenerator");
		nullFilter = new Shader("filter-null");
		gausBlurFilter = new Shader("filter-gausBlur7x1");
		fxaaFilter = new Shader("filter-fxaa");
		
		profileTimer = new ProfileTimer();
		windowSyncprofileTimer = new ProfileTimer();
		
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		glFrontFace(GL_CW);
		glCullFace(GL_BACK);
		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_DEPTH_CLAMP);
		//glEnable(GL_MULTISAMPLE);

		//glEnable(GL_TEXTURE_2D);
		
	  	int width = Window.getWidth() / 2;
		int height = Window.getHeight() / 2;
		
		tempTarget = new Texture(width, height, null, GL_TEXTURE_2D, GL_NEAREST, GL_COLOR_ATTACHMENT0, GL_RGBA, GL_RGBA, false);
		
		planeMaterial = new Material(tempTarget, 1, 8);
		planeTransform.rotate(new Quad(new Vector3f(1,0,0), (float)Math.toRadians(90f)));
		planeTransform.rotate(new Quad(new Vector3f(0,0,1), (float)Math.toRadians(180f)));
		plane = new Mesh("plane.obj");
		
		altCamera = new Camera(new Matrix4f().initIdentity());
		altCameraObject = new GameObject().addComponent(altCamera);
		altCamera.getTransform().rotate(new Vector3f(0,1,0),(float)Math.toRadians(180.0f));
		
		for (int i = 0; i < numShadowMaps; i++) {
			int shadowMapSize = 1 << (i+1);
			shadowMaps.add(i, new Texture(shadowMapSize, shadowMapSize, null, GL_TEXTURE_2D, GL_LINEAR, GL_COLOR_ATTACHMENT0, GL_RG32F, GL_RGBA, true));
			shadowMapTempTargets.add(i, new Texture(shadowMapSize, shadowMapSize, null, GL_TEXTURE_2D, GL_LINEAR, GL_COLOR_ATTACHMENT0, GL_RG32F, GL_RGBA, true));
		}
		
		lightMatrix = new Matrix4f().initScale(0, 0, 0);
	}
	
	public void cleanUp() {
		forwardAmbient.cleanUp();
		shadowMapShader.cleanUp();
		nullFilter.cleanUp();
		gausBlurFilter.cleanUp();
		fxaaFilter.cleanUp();
		GL40.glDeleteSamplers(Util.integerToIntArray(samplerMap.values().toArray(new Integer[] {})));
		for (BaseLight light : lights)
			light.getShader().cleanUp();
		plane.cleanUp();
		tempTarget.cleanUp();
		for (int i = 0; i < numShadowMaps; i++) {
			shadowMaps.get(i).cleanUp();
			shadowMapTempTargets.get(i).cleanUp();
		}
	}
	
	private void blurShadowMap(int shadowMapIndex, float blurAmt) {
		Texture shadowMap = shadowMaps.get(shadowMapIndex);
		Texture tempTarget = shadowMapTempTargets.get(shadowMapIndex);
		setVector3f("blurScale", new Vector3f(blurAmt/shadowMap.getWidth(), 0, 0));
		applyFilter(gausBlurFilter, shadowMap, tempTarget);
		
		setVector3f("blurScale", new Vector3f(0, blurAmt/shadowMap.getHeight(), 0));
		applyFilter(gausBlurFilter, tempTarget, shadowMap);
	}
	
	private void applyFilter(Shader filter, Texture source, Texture dest) {
		assert(source != dest);
		if (dest == null)
			Window.bindAsRenderTarget();
		else 
			dest.bindAsRenderTarget();
		
		setTexture("filterTexture", source);
		
		altCamera.setProjection(new Matrix4f().initIdentity());
		altCamera.getTransform().setPos(new Vector3f(0,0,0));
		altCamera.getTransform().setRot(new Vector3f(0,1,0),(float)Math.toRadians(180.0f));
		
		Camera temp = mainCamera;
		mainCamera = altCamera;
		
		glClear(GL_DEPTH_BUFFER_BIT);
		filter.bind();
		filter.updateUniforms(planeTransform, planeMaterial, this);
		plane.draw();
		
		mainCamera = temp;
		setTexture("filterTexture", null);
	}

	public void render(GameObject object) {
		profileTimer.startInvocation();
		getTexture("displayTexture").bindAsRenderTarget();
		
		glClearColor(0, 0, 0, 0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); //Clear Screen
		
		object.renderAll(forwardAmbient, this);

		for(BaseLight light : lights) {
			activeLight = light;
			ShadowInfo shadowInfo = light.getShadowInfo();
			
			int shadowMapIndex = 0;
			if (shadowInfo != null)
				shadowMapIndex = shadowInfo.getShadowMapSizeBaseTwo() - 1;
			setTexture("shadowMap", shadowMaps.get(shadowMapIndex));
			shadowMaps.get(shadowMapIndex).bindAsRenderTarget();
			glClearColor(1.0f, 1.0f, 0, 0);
			glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
			
			if (shadowInfo != null) {
				
				altCamera.setProjection(shadowInfo.getProjection());
				
				ShadowCameraTransform shadowCameraTransform = activeLight.calcShadowCameraTransform(mainCamera.getTransform().getTransformedPos(), mainCamera.getTransform().getTransformedRot());
				
				altCamera.getTransform().setPos(shadowCameraTransform.pos);
				altCamera.getTransform().setRot(shadowCameraTransform.rot);
				
				lightMatrix = biasMatrix.mult(altCamera.getViewProjection());
				
				setFloat("shadowVarianceMin", shadowInfo.getVarianceMin());
				setFloat("shadowLightBleedReduction", shadowInfo.getLightBleedReduction());
				boolean flipFaces = shadowInfo.getFlipFaces();
				
				Camera temp = mainCamera;
				mainCamera = altCamera;
				
				if (flipFaces) glCullFace(GL_FRONT);
				object.renderAll(shadowMapShader, this);
				if (flipFaces) glCullFace(GL_BACK);
				
				mainCamera = temp;
				
				float shadowSoftness = shadowInfo.getShadowSoftness();
				if (shadowSoftness != 0)
					blurShadowMap(shadowMapIndex, shadowSoftness);
				
			}
			else {
				lightMatrix = new Matrix4f().initScale(0,0,0);
				setFloat("shadowVarianceMin", 0.00002f);
				setFloat("shadowLightBleedReduction", 0.0f);
			}
			
			getTexture("displayTexture").bindAsRenderTarget();
			//Window.bindAsRenderTarget();
			glEnable(GL_BLEND);
			glBlendFunc(GL_ONE, GL_ONE);
			glDepthMask(false);
			glDepthFunc(GL_EQUAL);
			
			object.renderAll(light.getShader(), this);
			
			glDepthFunc(GL_LESS);
			glDepthMask(true);
			glDisable(GL_BLEND);
		}

		setVector3f("inverseFilterTextureSize", new Vector3f(1.0f/getTexture("displayTexture").getWidth(), 1.0f/getTexture("displayTexture").getHeight(), 0));
		profileTimer.stopInvocation();
		windowSyncprofileTimer.startInvocation();
		applyFilter(fxaaFilter, getTexture("displayTexture"), null);
		windowSyncprofileTimer.startInvocation();
	}
	
	public static void checkGlError(String msg) {
		int code = GL11.glGetError();
		if (code != GL11.GL_NO_ERROR) {
			throw new RuntimeException("GL error " + code + " " + msg);
		}
	}
	
	public double displayRenderTime(double dividend) { return profileTimer.displayAndReset("Render Time: ", dividend); }
	public double displayWindowSyncTime(double dividend) { return profileTimer.displayAndReset("Window Sync Time: ", dividend); }
	
	public static String getOpenGLVersion() {
		return glGetString(GL_VERSION);
	}

	public Camera getMainCamera() {
		return mainCamera;
	}
	
	public Matrix4f getLightMatrix() { return this.lightMatrix; }
	
	public int getSamplerSlot(String samplerName) {
		Integer slot = samplerMap.get(samplerName);
		return (slot == null) ? 0 : slot;
	}
	
	public void addLight(BaseLight light) {
		lights.add(light);
	}
	
	public void addCamera(Camera camera) {
		this.mainCamera = camera;
	}
	
	public BaseLight getActiveLight() {
		return activeLight;
	}

}
