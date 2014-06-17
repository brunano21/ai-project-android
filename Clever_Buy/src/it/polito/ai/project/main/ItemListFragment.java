package it.polito.ai.project.main;

public class ItemListFragment {

	private int item_id;
	private String item_name;
	private String item_quantity;
	private boolean hint_is_present;
	
	
	public ItemListFragment(String item_name, String item_quantity) {
		this.item_id=-1;
		this.item_name = item_name;
		this.item_quantity = item_quantity;
		this.hint_is_present = false;
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
	public int getItem_id() {
		return item_id;
	}
	public void setItem_id(int item_id) {
		this.item_id = item_id;
	}
	public boolean isHint_is_present() {
		return hint_is_present;
	}
	public void setHint_is_present(boolean hint_is_present) {
		this.hint_is_present = hint_is_present;
	}
	
}
