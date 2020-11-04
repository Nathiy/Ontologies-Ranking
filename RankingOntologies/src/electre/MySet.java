package electre;

public class MySet{
	
	// the column j and Vrj/Vsj element from the weighted normalized Matrix
	private int j;
	private double v;
	
	MySet(int j, double v){
		this.j = j;
		this.v = v;
	}
	
	public int getJ() {
		return j;
	}
	public void setJ(int j) {
		this.j = j;
	}
	public double getV() {
		return v;
	}
	public void setV(double v) {
		this.v = v;
	}
}
