package com.rocketscience.actions;

public class ActionKeys 
{
	/*
	 * this class should hold all the constants for the various action types.
	 */
	// the following three actions aren't really needed anymore.
	static public final int JUMP = 0, // jump. up into the air. what comes up must come down. jump, jump, jump, jump around.
	                        TAKE_DAMAGE = 3, // subtract hp
	                        CHECK_DEATH = 4, // used with TAKE_DAMAGE usually
	                        DIE = 5, // if CHECK_DEATH is true, calls this, usually
							SPAWN_SELF = 1, // used when this thing is spawned
	                        SPAWN_OBJ = 2; // used when this thing is spawning sth else
}
