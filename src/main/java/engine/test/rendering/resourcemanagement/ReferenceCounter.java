package engine.test.rendering.resourcemanagement;

public class ReferenceCounter {

	private int refCount;
	
	public ReferenceCounter() {
		this.refCount = 1;
	}
	
	public boolean removeReference() {
		refCount--;
		return refCount == 0;
	}
	
	public void addReference() {
		refCount++;
	}
}
