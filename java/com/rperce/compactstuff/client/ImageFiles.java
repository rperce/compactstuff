package com.rperce.compactstuff.client;

import net.minecraft.util.ResourceLocation;

public enum ImageFiles {
	WOVEN_1("armor", "woven_1"),
	WOVEN_2("armor", "woven_2"),
	COBBLE_1("armor", "cobble_1"),
	COBBLE_2("armor", "cobble_2"),
	CARBON_1("armor", "carbon_1"),
	CARBON_2("armor", "carbon_2"),
	ADVANCED_1("armor", "pure_1"),
	ADVANCED_2("armor", "pure_2"),
	TMOG_GUI("tmog_gui"),
	FURNACE_GUI("furnace_gui"),
	COMPACTOR_GUI("compactor_gui"),
	BLAZEFURNACE_GUI("blazefurnace_gui"),
	HOLDINGBAG_GUI("holding_gui");
	
		
	public String path;
	public ResourceLocation loc;
	ImageFiles(String p) { 
		this.path = "textures/"+p+".png"; 
		this.loc = new ResourceLocation("compactstuff", this.path);	
	}
	ImageFiles(String type, String p) {
		this.path = "compactstuff:textures/"+type+"/"+p+".png";
		this.loc = new ResourceLocation("compactstuff", this.path);
	}
}
