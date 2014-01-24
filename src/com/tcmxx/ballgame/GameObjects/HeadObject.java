package com.tcmxx.ballgame.GameObjects;

import java.util.ArrayList;

import com.tcmxx.ballgame.VectorAttr;

import android.graphics.PointF;


public class HeadObject extends GameObject {
		//inner physical features
		private float radius;
		public float mass;

		//outer physical features
		public float friction;
		public float gAcc;
		public float acc;
		private VectorAttr motionAttr;
		private PointF destination;
		
		//control parameters
		protected float gainP=20;
		protected float gainD=10;
		private float headPosError = 1;
		//methods
		public HeadObject(float x, float y, float r){
			super();
			radius = r;
			collisionRadius = r;
			super.setPosition(x, y);
			collisionModel.addCircle( new PointF(0,0), r, "Head");
			collisionModel.setCollisionRadius(r);
			collisionModel.addPoint(new PointF(0,0),"Head");
			width=2*r;
			height=2*r;
			motionAttr = new VectorAttr();
			destination = new PointF(x,y);
		}
		public HeadObject(){
			this(0.0f,0.0f,0.0f);
		}
		public void setRadius(float r){
			radius = r;
			collisionModel.setCircleRadiusByIndex(0,r);
			collisionRadius = r;
			collisionModel.setCollisionRadius(r);
			width=2*r;
			height=2*r;
		}
		public float getRadius(){
			return radius;
		}
		public PointF getDestionation(){
			return destination;
		}
		public VectorAttr getMotion(){
			return motionAttr;
		}
		
		public int effectBall(BallObject ball){
			ArrayList<PointF> collisionPoints = collisionModel.detectCollision(ball.getCollisionModel());
			if(collisionPoints.size()==0){
				return 0;
			}
			else{
				PointF point = new PointF(0,0);
				for(int i = 0;i<collisionPoints.size();i++){
					point.x+=collisionPoints.get(i).x;
					point.y+=collisionPoints.get(i).y;
					
				}
				point.x = point.x/collisionPoints.size();
				point.y = point.y/collisionPoints.size();
				VectorAttr ballMotion = ball.getMotion();
				float tmpVX = ballMotion.getX()-motionAttr.getX();
				float tmpVY = ballMotion.getY()-motionAttr.getY();
				VectorAttr v = new VectorAttr(tmpVX,tmpVY);
				VectorAttr normal = new VectorAttr((ball.getX()-point.x), (ball.getY()-point.y));
				v = VectorAttr.reflectVector(v, normal);
				ballMotion.setVector(v.getX()+motionAttr.getX(), v.getY()+motionAttr.getY());
				return -1;
			}
		}
		
		
		public void updateFrame(int FPS){
			float dist = (float)Math.sqrt(((xPos-destination.x)*(xPos-destination.x)
	    			+(yPos-destination.y)*(yPos-destination.y)));
	    	if(dist<=headPosError){
	    		motionAttr.setVector(0,0);
	    	}
	    	else{
	    		PDControl(FPS);
	    	}
			setPosition(xPos+motionAttr.getX()/FPS, yPos+motionAttr.getY()/FPS);
		}
		//////------------motion controls!------------------
		//pd control for destination
		protected void PDControl(int FPS){
			float ax=(destination.x-xPos)*gainP+(-motionAttr.getX())*gainD;
			float ay=(destination.y-yPos)*gainP+(-motionAttr.getY())*gainD;
			motionAttr.setVector(motionAttr.getX()+ax/FPS, motionAttr.getY()+ay/FPS);
		}
}
