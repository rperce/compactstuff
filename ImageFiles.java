package compactstuff;

public enum ImageFiles {
	ITEMS("items.png"),
	BLOCKS("blocks.png"),
	GLASS("glass.png"),
	COBBLE_1("cobble_1.png"),
	COBBLE_2("cobble_2.png"),
	CARBON_1("carbon_1.png"),
	CARBON_2("carbon_2.png"),
	WOVEN_1("woven_1.png"),
	WOVEN_2("woven_2.png"),
	ADVANCED_1("pure_1.png"),
	ADVANCED_2("pure_2.png"),
	FURNACE_GUI("furnace_gui.png"),
	HOLDINGBAG_GUI("holding_gui.png");
		
	public String path;
	ImageFiles(String p) { path = "/compactstuff/img/"+p; }
}
