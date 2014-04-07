package withdraw;

public class Account {
	private int balance;
	
	public Account(){
		balance = 1000;
	}
	public Account(int balance){
		this.balance = balance;
	}
	public void setBalance(int balance){
		this.balance = balance;
	}
	public int getBalance(){
		return balance;
	}
	public void withdrawMoney(int amount){
		balance -= amount;
	}
}
