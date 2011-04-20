package com.rocketscience.helpers;

import java.io.DataInputStream;
import java.io.IOException;

import com.rocketscience.objects.BaseObject;

public abstract class ObjectLoadingAdapter 
{
	public final short key;
	
	public abstract BaseObject load(final DataInputStream inp, final short myKey, final short relKey) throws IOException;
	
	public ObjectLoadingAdapter(final short k)
	{
		key = k;
	}
}
