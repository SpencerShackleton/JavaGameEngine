package engine.test.core.math.matrices;

import engine.test.core.math.vectors.Vector3f;

public class Matrix4f {

	private float[][] m;
	
	public Matrix4f() {
		setM(new float[4][4]);
	}
	
	public Matrix4f initIdentity() {
		for(int i = 0; i < 4; i++)
			m[i][i] = 1;
		return this;
	}
	
	public Matrix4f initTranslation(float x, float y, float z) {
		initIdentity();
		
		m[0][3] = x;	m[1][3] = y;	m[2][3] = z;
			
		return this;
	}
	
	public Matrix4f initScale(float x, float y, float z) {
		
		m[0][0] = x;	m[1][1] = y;	m[2][2] = z; m[3][3] = 1;
			
		return this;
	}
	
	public Matrix4f initRotation(float x, float y, float z) {
		Matrix4f rx = new Matrix4f().initIdentity();
		Matrix4f ry = new Matrix4f().initIdentity();
		Matrix4f rz = new Matrix4f().initIdentity();
		
		x = (float) Math.toRadians(x);
		y = (float) Math.toRadians(y);
		z = (float) Math.toRadians(z);
		
		rx.m[1][1] = (float) Math.cos(x);	rx.m[1][2] = -(float)Math.sin(x);
		rx.m[2][1] = (float) Math.sin(x);	rx.m[2][2] = (float)Math.cos(x);
		
		ry.m[0][0] = (float) Math.cos(y);	ry.m[0][2] = -(float)Math.sin(y);
		ry.m[2][0] = (float) Math.sin(y);	ry.m[2][2] = (float)Math.cos(y);
		
		rz.m[0][0] = (float) Math.cos(z);	rz.m[0][1] = -(float)Math.sin(z);
		rz.m[1][0] = (float) Math.sin(z);	rz.m[1][1] = (float)Math.cos(z);
		
		m = rz.mult(ry.mult(rx)).getM();
		
		return this;
	}
	
	public Matrix4f initPerspective(float fov, float ar, float zNear, float zFar) {

		float tfov = (float)Math.tan(fov / 2);
		float zRange = zNear - zFar;
		
		m[0][0] = 1.0f / (tfov * ar);	m[0][1] = 0;			m[0][2] = 0;					m[0][3] = 0;
		m[1][0] = 0;					m[1][1] = 1.0f / tfov;	m[1][2] = 0;					m[1][3] = 0;
		m[2][0] = 0;					m[2][1] = 0;			m[2][2] = (-zNear -zFar)/zRange;m[2][3] = 2 * zFar * zNear / zRange;
		m[3][0] = 0;					m[3][1] = 0;			m[3][2] = 1;					m[3][3] = 0;
		
		
		return this;
	}
	
	public Matrix4f initOrthographic(float left, float right, float bottom, float top, float near, float far) {

		float width = right - left;
		float height = top - bottom;
		float depth = far - near;
		
		m[0][0] = 2/width;	m[0][1] = 0;		m[0][2] = 0;		m[0][3] = -(right + left)/width;
		m[1][0] = 0;		m[1][1] = 2/height;	m[1][2] = 0;		m[1][3] = -(top + bottom)/height;
		m[2][0] = 0;		m[2][1] = 0;		m[2][2] = -2/depth;	m[2][3] = -(far + near)/depth;
		m[3][0] = 0;		m[3][1] = 0;		m[3][2] = 0;		m[3][3] = 1;
		
		return this;
	}
	
	public Matrix4f initRotation(Vector3f forward, Vector3f up) {

		Vector3f f = new Vector3f(forward.getX(), forward.getY(), forward.getZ()).normalized();
		Vector3f r = new Vector3f(up.getX(), up.getY(), up.getZ()).normalized();
		r = r.cross(f);
		
		Vector3f u = f.cross(r);
		
		m[0][0] = r.getX();		m[0][1] = r.getY();	m[0][2] = r.getZ();		m[0][3] = 0;
		m[1][0] = u.getX();		m[1][1] = u.getY();	m[1][2] = u.getZ();		m[1][3] = 0;
		m[2][0] = f.getX();		m[2][1] = f.getY();	m[2][2] = f.getZ();		m[2][3] = 0;
		m[3][0] = 0;			m[3][1] = 0;		m[3][2] = 0;			m[3][3] = 1;
		
		return this;
	}
	
	public Matrix4f initRotation(Vector3f forward, Vector3f up, Vector3f right)
	{
		Vector3f f = forward;
		Vector3f r = right;
		Vector3f u = up;

		m[0][0] = r.getX();	m[0][1] = r.getY();	m[0][2] = r.getZ();	m[0][3] = 0;
		m[1][0] = u.getX();	m[1][1] = u.getY();	m[1][2] = u.getZ();	m[1][3] = 0;
		m[2][0] = f.getX();	m[2][1] = f.getY();	m[2][2] = f.getZ();	m[2][3] = 0;
		m[3][0] = 0;		m[3][1] = 0;		m[3][2] = 0;		m[3][3] = 1;

		return this;
	}
	
	public Vector3f transform(Vector3f r)
	{
		return new Vector3f(m[0][0] * r.getX() + m[0][1] * r.getY() + m[0][2] * r.getZ() + m[0][3],
		                    m[1][0] * r.getX() + m[1][1] * r.getY() + m[1][2] * r.getZ() + m[1][3],
		                    m[2][0] * r.getX() + m[2][1] * r.getY() + m[2][2] * r.getZ() + m[2][3]);
	}
	
	public Matrix4f mult(Matrix4f r) {
		Matrix4f res = new Matrix4f();
		
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				res.set(i, j, m[i][0] * r.get(0, j) +
							m[i][1] * r.get(1, j) +
							m[i][2] * r.get(2, j) +
							m[i][3] * r.get(3, j));
			}
		}
		
		return res;
	}

	public float[][] getM() {
		return m;
	}
	
	public float get(int x, int y) {
		return m[x][y];
	}

	public void setM(float[][] m) {
		this.m = m;
	}
	
	public void set(int x, int y, float value) {
		m[x][y] = value;
	}
}
