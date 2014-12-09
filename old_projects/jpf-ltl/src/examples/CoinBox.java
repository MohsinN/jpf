public class CoinBox {
  int totalMoney;

  public CoinBox() {
    totalMoney = 0;
  }

  public void addCoin(int coin) {
    totalMoney += coin;
  }

  public int getMoneyAmount() {
    return totalMoney;
  }

  public void returnAllCoin() {
    /* suppose to return some coint */
  }

  public void giveChange(int change) {

  }

  public void rejectMoney() {
  }
}