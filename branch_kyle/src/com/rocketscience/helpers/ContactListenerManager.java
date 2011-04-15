package com.rocketscience.helpers;

import java.util.ArrayList;

import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;

/*
 * this class enables you to have multiple contactListeners kind of how andEngine handles
 * contact listeners
 */
public final class ContactListenerManager 
{
	private final static ArrayList<ContactListener> listeners = new ArrayList<ContactListener>();
	
	public static void addListener(final ContactListener l)
	{
		listeners.add(l);
	}
	
	public static void removeListener(final ContactListener l)
	{
		listeners.remove(l);
	}
	
	public static void clearAllListeners()
	{
		listeners.clear();
	}
	
	/* makes the given world the one that listens to all the contactlisteneres
	 * 
	 */
	public static void attachToWorld(PhysicsWorld world)
	{
		world.setContactListener(new ContactListener()
		{
			@Override
			public void beginContact(Contact contact) 
			{
				for (ContactListener l : listeners)
					l.beginContact(contact);
			}
			@Override
			public void endContact(Contact contact) 
			{
				for (ContactListener l : listeners)
					l.endContact(contact);
			}
		});
	}
}
