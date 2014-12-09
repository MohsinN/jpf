package login;
import java.lang.*;
public class LoginSystem {
	private static Object out;
	final int MIN = 100000;
	final int MAX = 999999;
	public boolean login(int userID, int pin){
		if (isAccountValid(userID,pin)){
			System.out.println("Login success");
			return true;
		}
		else{
			System.out.println("Login fail! Please try again!!!");
			return false;
		}
	}
	private boolean isAccountValid(int userID, int pin){
		if (userID > 100000 && userID < 999999)
			return true;
		return false;
	}
}
