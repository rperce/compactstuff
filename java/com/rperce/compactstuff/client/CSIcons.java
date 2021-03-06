package com.rperce.compactstuff.client;

import com.rperce.compactstuff.Metas;

public class CSIcons {
	public final static String
		PREFIX = "CompactStuff:",
		CARBFURNACE_FRONT_INACTIVE = "carbfurnace_front_inactive",
		CARBFURNACE_FRONT_ACTIVE = "carbfurnace_front_active",
		CARBFURNACE_SIDE = "carbfurnace_side",
		CARBFURNACE_TOP = "carbfurnace_top",
		COALBLOCK = "coalblock",
		COMCOBBLE = "comcobble",
		COMDIA = "comdia",
		COMDIRT = "comdirt",
		COMFURNACE_FRONT_INACTIVE = "comfurnace_front_inactive",
		COMFURNACE_FRONT_ACTIVE = "comfurnace_front_active",
		COMFURNACE_SIDE = "comfurnace_side",
		COMFURNACE_TOP = "comfurnace_top",
		COMGRAVEL  = "comgravel",
		COMIRON = "comiron",
		COMRACK = "comrack",
		COMSAND = "comsand",
		COMSTEEL = "comsteel",
		DIORITE = "diorite",
		STEELBLOCK = "steelblock",
		COMGOLD	= "comgold",
		BLAZEFURNACE_FRONT_INACTIVE = "blazefurnace_front_inactive",
		BLAZEFURNACE_FRONT_ACTIVE = "blazefurnace_front_active",
		BLAZEFURNACE_SIDE = "blazefurnace_side",
		BLAZEFURNACE_TOP = "blazefurnace_top",
		
		COMPACTOR_TOP = "compactor_top",
		COMPACTOR_SIDE1 = "compactor_side",
		COMPACTOR_SIDE2 = "compactor_side2",
		COMPACTOR_BOTTOM = "compactor_bottom",
		PAXEL = "compactPaxel";
	
	public static String comString(int meta) {
		switch(meta) {
			case Metas.COMCOBBLE : return COMCOBBLE;
			case Metas.COMDIAMOND : return COMDIA;
			case Metas.COMDIRT : return COMDIRT;
			case Metas.COMGRAVEL : return COMGRAVEL;
			case Metas.COMIRON : return COMIRON;
			case Metas.COMRACK : return COMRACK;
			case Metas.COMSAND : return COMSAND;
			case Metas.COMSTEEL : return COMSTEEL;
			case Metas.DIORITE : return DIORITE;
			case Metas.STEELBLOCK : return STEELBLOCK;
			case Metas.COMGOLD: return COMGOLD;
			default: return "Error: unknown meta: "+meta;
		}
	}
}
