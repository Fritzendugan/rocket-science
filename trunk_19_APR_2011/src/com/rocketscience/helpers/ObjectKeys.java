package com.rocketscience.helpers;

public class ObjectKeys 
{
	public final static short WORLD_SHAPE = 0,
							  DEATH_AREA = 3,     // this instantly kills the player on touch. Has a flag to kill other mobs, too.
							  WAY_POINT = 2,     // when the player dies, they spawn at the last activated waypoint
							  BODY_WITH_ACTIONS = 4, // default body with actions type
							  NULL_OBJECT = -1,
							  PLAYER = 5, // the player
							  SPELL_BODY = 6,
							  LEVEL_ENDER = 7, // ends the level and begins the quiz
							  HINT_BOX = 8, // hint box
							  DOOR = 1;
	
}
