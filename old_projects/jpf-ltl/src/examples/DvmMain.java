import gov.nasa.jpf.ltl.LTLSpecFile;

/**
 * 
 * @author anh cuong
 * 
 */
@LTLSpecFile("src/examples/dvm.ltl")
public class DvmMain {
  public static void main(String[] args) {
    /*
     * insert money, 0 means no more insert, -1 means retake the money the money
     * can only be 10, 20 or 50
     */
    int coin1 = 1;
    int coin2 = 1;
    int coin3 = 1;

    /*
     * the amount money of a drink the prize must be divided by 10 and positive
     */
    int price = 3;

    /* the avaiability of a drink */
    int avail = 1;

    /* the status of dvm machine */
    int dvmStatus = 1;

    /* running dvm */
    temp(coin1, coin2, coin3, price, avail, dvmStatus);
  }

  public static void temp(int coin1, int coin2, int coin3, int price,
      int avail, int dvmStatus) {
    rundvm(coin1, coin2, coin3, price, avail, dvmStatus);
  }

  public static void rundvm(int coin1, int coin2, int coin3, int price,
      int avail, int dvmStatus) {
    Dvm dvm = new Dvm(dvmStatus);

    Drink drink = new Drink(avail);
    if (price > 0)
      drink.setPrice(price);
    else
      drink.setAvail(0);

    dvm.setDrink(drink);

    dvm.insertCoin(coin1, coin2, coin3);

    dvm.returnButton();
  }
}