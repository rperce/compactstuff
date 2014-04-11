package com.rperce.compactstuff.client;

import net.minecraft.util.ResourceLocation;

public enum ImageFiles {
	WOVEN_1("woven_1"),
	WOVEN_2("woven_2"),
	COBBLE_1("cobble_1"),
	COBBLE_2("cobble_2"),
	CARBON_1("carbon_1"),
	CARBON_2("carbon_2"),
	ADVANCED_1("pure_1"),
	ADVANCED_2("pure_2"),
	TMOG_GUI("tmog_gui"),
	FURNACE_GUI("furnace_gui"),
	COMPACTOR_GUI("compactor_gui"),
	BLAZEFURNACE_GUI("blazefurnace_gui"),
	HOLDINGBAG_GUI("holding_gui");
	
		
	public String path;
	public ResourceLocation loc;
	ImageFiles(String p) { 
		path = "textures/"+p+".png"; 
		loc = new ResourceLocation("compactstuff", path);	
	}
}
