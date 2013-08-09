public class Dvm {
  final int MAXMONEY = 90;
  Drink drink;
  CoinBox coinBox;
  int status;

  public Dvm(int status) {
    this.status = status;
    coinBox = new CoinBox();
  }

  public void setDrink(Drink drink) {
    this.drink = drink;
  }

  public void insertCoin(int coin1, int coin2, int coin3) {
    /*
     * add money to coinBox, notice that coinBox can return money on user
     * request
     */
    if (status > 0) {
      if (coin1 > 0) {
        coinBox.addCoin(coin1);
        if (coin2 > 0) {
          coinBox.addCoin(coin2);
          if (coin3 > 0) {
            if (coinBox.getMoneyAmount() < MAXMONEY)
              coinBox.addCoin(coin3);
            else
              coinBox.rejectMoney();
          } else if (coin3 == -1)
            coinBox.returnAllCoin();
        } else if (coin2 == -1)
          coinBox.returnAllCoin();
      } else if (coin1 == -1)
        coinBox.returnAllCoin();
    }
  }

  public void returnButton() {
    int money = coinBox.getMoneyAmount();

    if (drink.getAvail() > 0) {
      int drinkPrice = drink.getPrice();
      if (money >= drinkPrice) {
        drink.release();
        if (money != drinkPrice) {
          coinBox.giveChange(money - drinkPrice);
          drink.updateDrink();
        }
      } else
        coinBox.returnAllCoin();
    }
  }
}