package com.tcmxx.ballgame;

import android.graphics.Matrix;
import android.graphics.PointF;

public class BallGameUtils {
	//////////////////////////////////////////////////////////
	//utils for rotate pointF around origin for degree
	static public void rotatePointF(PointF scr, PointF des, float degree){
		Matrix R = new Matrix();
		R.setRotate(degree);
		float[] pt = new float[2];
		pt[0]=scr.x;pt[1]=scr.y;
		R.mapPoints(pt);
		des.x=pt[0];des.y=pt[1];
	}
	//////////////////////////////////////////////////////////////
	//return the reflection of v when n is the normal vector/////
	public static PointF reflectVector(PointF v, PointF n){
		float x1 = v.x;
		float x2 = n.x;
		float y1 = v.y;
		float y2 = n.y;
		float x = x1-2*(x1*x2+y1*y2)*x2/(x2*x2+y2*y2);
		float y = y1-2*(x1*x2+y1*y2)*y2/(x2*x2+y2*y2);
		return new PointF(x,y);
	}
	///////////////////////////////////////////////////////////////////
	//return the vector project on the vector n
	public static PointF projectVector(PointF v, PointF n){
		float vx = v.x;
		float vy = v.y;
		float nx = n.x;
		float ny = n.y;
		float mod = (float)Math.sqrt(nx*nx+ny*ny);
		float length = (vx*nx+vy*ny)/mod;
		return new PointF(length*nx/mod, length*ny/mod);
		
	}
}
