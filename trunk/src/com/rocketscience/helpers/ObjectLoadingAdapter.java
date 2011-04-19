package com.rocketscience.helpers;

import java.io.DataInputStream;
import java.io.IOException;

import com.rocketscience.objects.BaseObject;

public abstract class ObjectLoadingAdapter 
{
	public final short key;
	
	public abstract BaseObject load(DataInputStream inp) throws IOException;
	
	public ObjectLoadingAdapter(final short k)
	{
		key = k;
	}
}
