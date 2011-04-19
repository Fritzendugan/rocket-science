package com.rocketscience.helpers;

public class CollisionFilter 
{
	public static final short CATEGORY_NORMAL    = 0x0002,
	                          CATEGORY_PLAYER    = 0x0004,
	                          CATEGORY_NONPLAYER = 0x0008,
	                          CATEGORY_NOCOLLISION = 0x0016;
	
	public static final short MASK_NORMAL = CATEGORY_NORMAL + CATEGORY_PLAYER + CATEGORY_NONPLAYER,
	                          MASK_PLAYER = CATEGORY_PLAYER + CATEGORY_NORMAL,
	                          MASK_NONPLAYER = CATEGORY_NONPLAYER + CATEGORY_NORMAL,
	                          MASK_NOCOLLISION = CATEGORY_NOCOLLISION;
}
