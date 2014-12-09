package connect;


public class Dialler {
	CellularRadio cr = new CellularRadio();
	
	public void connect(){
		cr.connect();
	}
	public void transfer(){
		cr.transfer();
	}
	public void end(){
		cr.end();
	}
	
	public static void main(String [] args) {
		
	}
}
