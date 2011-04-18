package com.rocketscience.mobs;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.util.Debug;

import com.badlogic.gdx.math.Vector2;

public class MovePathController implements IUpdateHandler
{
	private final MoveNode[] nodes;
	private int curNode; // the current node
	public Vector2 position = new Vector2(); // the current position
	
	public MovePathController(MoveNode[] n)
	{
		nodes = n;
		reset();
	}
	
	@Override
	public void reset()
	{
		curNode = 0;
		nodes[curNode].reset();
	}

	@Override
	public void onUpdate(float pSecondsElapsed) 
	{
		long elapsed = (int)(pSecondsElapsed * 1000);
		nodes[curNode].update(elapsed); // add elapsed time to this node
		elapsed = nodes[curNode].getExcessTime();
		while (elapsed > 0)
		{
			curNode++;
			curNode %= nodes.length;
			nodes[curNode].reset(); // reset the new node
			nodes[curNode].update(elapsed); // add elapsed time to this node
			elapsed = nodes[curNode].getExcessTime();
		}
		
		// now set position
		nodes[curNode].setPosition(position, nodes[(curNode + 1) % nodes.length]);
	}
	
	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder();
		buf.append("MovePathController -- numNodes: ").append(nodes.length).append(" \n");
		for (MoveNode n : nodes)
			buf.append(n.toString()).append("\n");
		
		return buf.toString();
	}
	
}
