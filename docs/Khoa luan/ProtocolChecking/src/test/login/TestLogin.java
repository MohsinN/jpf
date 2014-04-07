package login;
import annotations.UmlSpec;

public class TestLogin {

	public static void main(String arg[]){
		TestLogin test = new TestLogin();
		test.testLogin(100001, 123456);
	}
	@UmlSpec(
			sequenceDiagram = {".\\xmi\\login.xmi"}
	)
	public void testLogin(int userID, int pin){
		LoginSystem sys = new LoginSystem();
		boolean loginOK = false;
		//while (!loginOK){
			loginOK = sys.login(userID, pin);
		//}
	}
}
