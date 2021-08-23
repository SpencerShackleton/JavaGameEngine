package engine.test.core;

import engine.test.core.math.matrices.Matrix4f;
import engine.test.core.math.quads.Quad;
import engine.test.core.math.vectors.Vector3f;

public class Transform {

	private Transform parent;
	private Matrix4f parentMatrix;

	private Vector3f pos;
	private Quad rot;
	private Vector3f scale;

	private Vector3f oldPos;
	private Quad oldRot;
	private Vector3f oldScale;

	public Transform()
	{
		pos = new Vector3f(0,0,0);
		rot = new Quad(0,0,0,1);
		scale = new Vector3f(1,1,1);

		parentMatrix = new Matrix4f().initIdentity();
	}

	public void update()
	{
		if(oldPos != null)
		{
			oldPos.set(pos);
			oldRot.set(rot);
			oldScale.set(scale);
		}
		else
		{
			oldPos = new Vector3f(0,0,0).set(pos).add(1.0f);
			oldRot = new Quad(0,0,0,0).set(rot).mult(0.5f);
			oldScale = new Vector3f(0,0,0).set(scale).add(1.0f);
		}
	}

	public void rotate(Vector3f axis, float angle)
	{
		rot = new Quad(axis, angle).mult(rot).normalized();
	}
	
	public void rotate(Quad r) {
		rot = r.mult(rot).normalized();
	}

	public boolean hasChanged()
	{
		if(parent != null && parent.hasChanged())
			return true;

		if(!pos.equals(oldPos))
			return true;

		if(!rot.equals(oldRot))
			return true;

		if(!scale.equals(oldScale))
			return true;

		return false;
	}

	public Matrix4f getTransformation()
	{
		Matrix4f translationMatrix = new Matrix4f().initTranslation(pos.getX(), pos.getY(), pos.getZ());
		Matrix4f rotationMatrix = rot.toRotationMatrix();
		Matrix4f scaleMatrix = new Matrix4f().initScale(scale.getX(), scale.getY(), scale.getZ());

		return getParentMatrix().mult(translationMatrix.mult(rotationMatrix.mult(scaleMatrix)));
	}

	private Matrix4f getParentMatrix()
	{
		if(parent != null && parent.hasChanged())
			parentMatrix = parent.getTransformation();

		return parentMatrix;
	}

	public void setParent(Transform parent)
	{
		this.parent = parent;
	}

	public Vector3f getTransformedPos()
	{
		return getParentMatrix().transform(pos);
	}

	public Quad getTransformedRot()
	{
		Quad parentRotation = new Quad(0,0,0,1);

		if(parent != null)
			parentRotation = parent.getTransformedRot();

		return parentRotation.mult(rot);
	}
	
	public Vector3f getPos() {
		return pos;
	}

	public void setPos(Vector3f translation) {
		this.pos = translation;
	}

	public Quad getRot() {
		return rot;
	}

	public void setRot(Quad rotation) {
		this.rot = rotation;
	}
	
	public void setRot(Vector3f axis, float angle) {
		this.rot = new Quad(axis, angle);
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}
	
	public void setScale(float scale) {
		this.scale = new Vector3f(scale, scale, scale);
	}
}
