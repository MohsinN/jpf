public class Drink {
  int price;
  int avail;
  int quantity;

  public Drink(int avail) {
    price = 0;
    this.avail = avail;
    quantity = 10;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public void setAvail(int avail) {
    this.avail = avail;
  }

  public int getPrice() {
    return price;
  }

  public int getAvail() {
    return avail;
  }

  public void release() {
  }

  public void updateDrink() {
    quantity--;
  }
}