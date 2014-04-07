package withdraw;
import annotations.UmlSpec;

public class ATM {
	User user;
	
	public ATM(){
		user =  new User();
	}
	public static void main(String args[]){
		ATM atm = new ATM();
		atm.withdrawMoney(100);
	}

	@UmlSpec(sequenceDiagram = { ".\\xmi\\withdraw.xmi" })	//annotation
	public void withdrawMoney(int amount){	//method under test
		if (amount >= 0){
			String log = user.withdrawMoney(amount);
			System.out.println(log);
		}
		String log2 = user.withdrawMoney(amount);
	}
}

