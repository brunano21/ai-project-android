package it.polito.ai.project.main;

public class ItemListFragment {
	
	private String item_name;
	private String item_quantity;
	
	
	public ItemListFragment(String item_name, String item_quantity) {
		this.item_name = item_name;
		this.item_quantity = item_quantity;
	}
	
	
	public String getItem_name() {
		return item_name;
	}
	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}
	public String getItem_quantity() {
		return item_quantity;
	}
	public void setItem_quantity(String item_quantity) {
		this.item_quantity = item_quantity;
	}
	
}
