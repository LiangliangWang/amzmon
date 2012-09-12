package amzmon;

import java.io.PrintWriter;

public class Item {
	
	private String ItemId;
	private String ItemName;
	private Category ItemCategory;
	private boolean OutOfStock;
	private int Quantity;
	public enum Unit {Day, Week, NA};
	private Unit WaitingUnit;
	private int WaitingTimeEarliest;
	private int WaitingTimeLatest;
	private float Price;
	private float MinNewPrice;
	private float MinUsedPrice;
	
	public Item(String id, String name, Category category){
		this.ItemId = id;
		this.ItemName = name;
		this.ItemCategory = category;
		this.OutOfStock = false;
		this.Quantity = -1;
		this.WaitingUnit = Unit.NA;
		this.WaitingTimeEarliest = -1;
		this.WaitingTimeLatest = -1;
		this.Price = -1;
		this.MinNewPrice = -1;
		this.MinUsedPrice = -1;
	}
	
	public String getItemId() {
		return ItemId;
	}
	public void setItemId(String itemId) {
		ItemId = itemId;
	}
	public String getItemName() {
		return ItemName;
	}
	public void setItemName(String itemName) {
		ItemName = itemName;
	}
	public Category getItemCategory() {
		return ItemCategory;
	}
	public void setItemCategory(Category itemCategory) {
		ItemCategory = itemCategory;
	}
	public boolean isOutOfStock() {
		return OutOfStock;
	}
	public void setOutOfStock(boolean outOfStock) {
		OutOfStock = outOfStock;
	}
	public int getQuantity() {
		return Quantity;
	}
	public void setQuantity(int quantity) {
		Quantity = quantity;
	}
	public int getWaitingTimeEarliest() {
		return WaitingTimeEarliest;
	}
	public void setWaitingTimeEarliest(int waitingTimeEarliest) {
		WaitingTimeEarliest = waitingTimeEarliest;
	}
	public int getWaitingTimeLatest() {
		return WaitingTimeLatest;
	}
	public void setWaitingTimeLatest(int waitingTimeLatest) {
		WaitingTimeLatest = waitingTimeLatest;
	}
	public Unit getWaitingUnit() {
		return WaitingUnit;
	}
	public void setWaitingUnit(Unit waitingUnit) {
		WaitingUnit = waitingUnit;
	}
	public float getMinNewPrice() {
		return MinNewPrice;
	}

	public void setMinNewPrice(float minNewPrice) {
		MinNewPrice = minNewPrice;
	}
	public float getMinUsedPrice() {
		return MinUsedPrice;
	}

	public void setMinUsedPrice(float minUsedPrice) {
		MinUsedPrice = minUsedPrice;
	}
	public float getPrice() {
		return Price;
	}

	public void setPrice(float price) {
		Price = price;
	}
	public void printItem(){
		System.out.println(this.ItemId + "\t" 
				       + this.ItemName + "\t"
				       + this.ItemCategory.getCategoryName() + "\t"
				       + (this.Price>0?this.Price:"---") + "\t"
				       + (this.MinNewPrice>0?this.MinNewPrice:"---") + "\t"
				       + (this.MinUsedPrice>0?this.MinUsedPrice:"---") + "\t"
				       + (this.OutOfStock?"Out of Stock":"In Stock" +"\t")
				       + (this.Quantity>0?this.Quantity+"\t":"")
				       + (this.WaitingTimeEarliest>0?this.WaitingTimeEarliest+" to ":"") 
				       + (this.WaitingTimeLatest>0?this.WaitingTimeLatest:"") + " " + (this.WaitingUnit != Unit.NA? this.WaitingUnit + "s":""));
	}
	public void writeItem(PrintWriter pw) {
		pw.println(this.ItemId + "\t" 
			       + this.ItemName + "\t"
			       + this.ItemCategory.getCategoryName() + "\t"
			       + (this.Price>0?this.Price:"---") + "\t"
			       + (this.MinNewPrice>0?this.MinNewPrice:"---") + "\t"
			       + (this.MinUsedPrice>0?this.MinUsedPrice:"---") + "\t"
			       + (this.OutOfStock?"Out of Stock":"In Stock" +"\t")
			       + (this.Quantity>0?this.Quantity+"\t":"")
			       + (this.WaitingTimeEarliest>0?this.WaitingTimeEarliest+" to ":"")
			       + (this.WaitingTimeLatest>0?this.WaitingTimeLatest:"") + " " + (this.WaitingUnit != Unit.NA? this.WaitingUnit + "s":""));		
	}
}
