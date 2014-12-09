package connect;


import java.util.ArrayList;

public class CellularRadio {
	Connection c;
	public void connect(){
		c = new Connection();
		c.connect();
	}
	public void transfer(){
		c.transfer();
	}
	public void end(){
		c.disconnect();
	}
}