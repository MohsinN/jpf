package connect;
import annotations.UmlSpec;


/**
*@author Phuc Nguyen Dinh
*/

public class TestCallMonitor {
	
	public static void main(String [] args) {
		TestCallMonitor t = new TestCallMonitor();
		t.test1(-1,-2,-3);
	}
	@UmlSpec(
			sequenceDiagram = {".\\xmi\\connect.xmi"}
	)
	public void test1(int x, int y, int z){
		Dialler d = new Dialler();
		int k = x + y + z;
		if (k == 0){
			d.connect();
			d.transfer();
			d.end();
			System.out.println("k == 0");
		}
		else if (k > 0){
			d.connect();
			//d.transfer();
			d.end();
			System.out.println("k > 0");
		}
		else{
			d.connect();
			d.transfer();
			//d.end();
			System.out.println("k  < 0");
		}
	}
}