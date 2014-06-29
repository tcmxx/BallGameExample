package com.tcmxx.ballgame.GameObjects;

import java.util.ArrayList;

import com.tcmxx.ballgame.MovePath;
import com.tcmxx.ballgame.VectorAttr;

import android.graphics.Canvas;
import android.graphics.PointF;


public class HeadObject extends GameObject {
		//inner physical features
		private float radius;
		public float mass=10;

		//outer physical features
		public float friction;
		public float gAcc;
		public float acc;
		private VectorAttr motionAttr;
		private PointF destination;
		
		//control parameters
		protected float gainP=40;
		protected float gainD=5;
		private float headPosError = 1;
		float followRate=0.5f; 	//the speed the new destination will follow the path
								//For example, when it is 0.5, the destination will poll
								//from the path every 2 frame
		public long followDelay = 1000;	//The delay before following a new path
		public long delay=0;	//current delay

		//the status of the current head
		//it will be examed every frame
		public enum HeadStatus {NORMAL, PAUSE, NEXTLEVEL}
		public HeadStatus status;
		
		//recorded path
		MovePath mPath;
		
		//methods
		public HeadObject(float x, float y, float r){
			super();
			status = HeadStatus.NORMAL;
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
			mPath = new MovePath(100);
		}
		public HeadObject(){
			this(0.0f,0.0f,0.0f);
		}
		public HeadObject(HeadObject head){
			
		}
		
		@Override
		public Object clone() throws CloneNotSupportedException {
			return super.clone();
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
		public MovePath getPath(){
			return mPath;
		}
		public VectorAttr getMotion(){
			return motionAttr;
		}
		
		public int effectBall(BallObject ball, int FPS){
			ArrayList<PointF> collisionPoints = collisionModel.detectCollision(ball.getCollisionModel());
			if(collisionPoints.size()==0){
				return 0;
			}
			else{
				//head & ball go back to the position before collision(record position)
				float newHPosX = this.getX()-this.getMotion().getX()/(float)FPS; 
				float newHPosY = this.getY()-this.getMotion().getY()/(float)FPS;
				//float newBPosX = ball.getX()-ball.getMotion().getX()/(float)FPS; 
				//float newBPosY = ball.getY()-ball.getMotion().getY()/(float)FPS;
				
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
				VectorAttr vRelated = new VectorAttr(tmpVX,tmpVY);
				VectorAttr normal = new VectorAttr((ball.getX()-point.x), (ball.getY()-point.y));
				VectorAttr vnRelated = VectorAttr.projectVector(vRelated, normal);
				VectorAttr vtRelated = new VectorAttr(vRelated.getX()-vnRelated.getX(),
						vRelated.getY()-vnRelated.getY());
				VectorAttr dvHead = VectorAttr.mulVector(vnRelated, 2*ball.mass/(mass+ball.mass));
				VectorAttr dvBall = VectorAttr.mulVector(vnRelated, (-mass+ball.mass)/(mass+ball.mass));
				
				ballMotion.set(VectorAttr.addVector(vtRelated,VectorAttr.addVector(dvBall,motionAttr)));
				motionAttr.add(dvHead);
				
				this.setPosition(newHPosX, newHPosY);
				//ball.setPosition(newBPosX, newBPosY);
				return -1;
			}
		}
		public boolean draw(Canvas canvas){
			mPath.draw(canvas);
			super.draw(canvas);
			
			return true;
		}
		
		public void updateFrame(int FPS){
			
			motionControl(FPS);		//use controller to update the velocity
			setPosition(xPos+motionAttr.getX()/FPS, yPos+motionAttr.getY()/FPS);
		}
		//////------------motion controls!------------------
		/////
		protected void motionControl(int FPS){
			delay=delay+(long)(1f/(float)FPS*1000);
			if(delay>=followDelay){
				//set destination as the new points on the path
				PointF tmpDes=null;
				if(mPath.getTimeFromLastPoll()>=mPath.getNextInterval()/followRate){
					tmpDes= mPath.pollPoint();
				}
				if(tmpDes!=null){
					destination = tmpDes;
				}
			}
			//if there is a destination, use control strategy
			if(destination!=null){
				float dist = (float)Math.sqrt(((xPos-destination.x)*(xPos-destination.x)
		    			+(yPos-destination.y)*(yPos-destination.y)));
		    	if(dist<=headPosError&&mPath.length==0){
		    		motionAttr.setVector(0,0);
		    		destination = null;
		    		delay=0;
		    	}
		    	else{
		    		PDControl(FPS);
		    	}
			}
		}
		//pd control for destination
		protected void PDControl(int FPS){
			float ax=(destination.x-xPos)*gainP+(-motionAttr.getX())*gainD;
			float ay=(destination.y-yPos)*gainP+(-motionAttr.getY())*gainD;
			motionAttr.setVector(motionAttr.getX()+ax/FPS, motionAttr.getY()+ay/FPS);
		}
}
