package withdraw;

public class User {
	private String log;
	Account acc;
	public User(){
		acc = new Account();
	}
	
	public String withdrawMoney(int amount){
		int balance = acc.getBalance();
		if ( amount < balance){
			acc.withdrawMoney(amount);
			log = "Transaction succeeded!\n\tYou've just withdrew " + amount + "$.\n\tYour current balance is " + (balance - amount);
			//log = "OK";
		}
		else{
			log = "Transaction failed!\n\tYour current balance is " + balance;
			//log = "Failed";
			
		}
		String message = log();
		return message;
	}
	private String log(){
		return log;
	}
}
