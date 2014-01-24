package com.tcmxx.ballgame;

public class VectorAttr {
	private float value;
	private float angle;
	private float x;
	private float y;

	public VectorAttr(){
		this(0f,0f);
	}
	
	public VectorAttr(float xx, float yy){
		x = xx;
		y = yy;
		value=(float)Math.sqrt(x*x+y*y);
		angle=(float)Math.atan2(y, x);
	}
	public float getValue(){
		return value;
	}
	public float getAngle(){
		return angle;
	}
	public float getX(){
		return x;
	}
	public float getY(){
		return y;
	}
	public void set(VectorAttr c){
		setVector(c.x,c.y);
	}
	public void setValue(float v, float angle){
		this.value=v;
		this.angle=angle;
		x = v*(float)Math.cos(angle/360*Math.PI*2);
		y = v*(float)Math.sin(angle/360*Math.PI*2);
	}
	public void setVector(float x, float y){
		value=(float)(Math.sqrt(x*x+y*y));
		angle=(float)(Math.atan2(y, x)/Math.PI*180);
		this.x = x;
		this.y = y;
	}
	
	///////////////////////////////////////////////////////
	//some utilities
	public static VectorAttr reflectVector(VectorAttr v, VectorAttr n){
		float x1 = v.x;
		float x2 = n.x;
		float y1 = v.y;
		float y2 = n.y;
		float x = x1-2*(x1*x2+y1*y2)*x2/(x2*x2+y2*y2);
		float y = y1-2*(x1*x2+y1*y2)*y2/(x2*x2+y2*y2);
		return new VectorAttr(x,y);
	}
	///////////////////////////////////////////////////////////////////
	//return the vector project on the vector n
	public static VectorAttr projectVector(VectorAttr v, VectorAttr n){
		float vx = v.x;
		float vy = v.y;
		float nx = n.x;
		float ny = n.y;
		float mod = (float)Math.sqrt(nx*nx+ny*ny);
		float length = (vx*nx+vy*ny)/mod;
		return new VectorAttr(length*nx/mod, length*ny/mod);
		
	}
	
}
