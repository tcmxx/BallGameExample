package com.tcmxx.ballgame.GameObjects;

public abstract class Component extends GameObject {
	
	
	
	public Component(){
		super();
	}
	
	public abstract int effectBall(BallObject ball);	//return -1: hit   0 nothing  
	public abstract int effectHead(HeadObject head);
}
