package com.tcmxx.ballgame.GameObjects;

import java.util.ArrayList;

import com.tcmxx.ballgame.CollisionModel;
import com.tcmxx.ballgame.ComponentGroup;
import com.tcmxx.ballgame.VectorAttr;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class Ejection extends GameObject {
	
	static final private float HALF_ANGLE = 5;	//ejection angle on one side of the center
	static final private long MAX_TIME = 2000;
	
	public float mass;
	public float damping;
	public PointF origin = null;
	private int halfWidth;	//number of points on one side of the center
	private long startTime;
	private ArrayList<PointF> previousPoints = null; 	//points after which the ejection will be deleted
	private ArrayList<PointF> currentPoints = null;
	private ArrayList<VectorAttr> velocity = null;
	private ArrayList<Boolean> collisions = null;
	//velocity: pix/second
	public Ejection(PointF origin_, VectorAttr velocity_, int halfWidth_){
		origin = origin_;
		halfWidth = halfWidth_;
		velocity = new ArrayList<VectorAttr>(halfWidth*2+1);
		previousPoints = new ArrayList<PointF>(halfWidth*2+1);
		currentPoints = new ArrayList<PointF>(halfWidth*2+1);
		collisions = new ArrayList<Boolean>(halfWidth*2+1);
		for(int i = 0; i<halfWidth*2+1; i++ ){
			previousPoints.add(new PointF(origin.x,origin.y));
			velocity.add(new VectorAttr());
			velocity.get(i).setValue(velocity_.getValue(),
					velocity_.getAngle()+((float)(i-halfWidth)/((float)halfWidth+1))*HALF_ANGLE);
			currentPoints.add(new PointF(origin.x,origin.y));
			collisions.add(false);
		}
		damping = 0.005f;
		mass = 0.05f;
	}
	
	//start timing this ejection and give head a velocity feedback
	public void start(ComponentGroup mGroup, HeadObject head){
		startTime = System.currentTimeMillis();
		for(int i = 0; i<halfWidth*2+1; i++ ){
			velocity.get(i).add(head.getMotion());
		}
		head.getMotion().add(VectorAttr.mulVector(velocity.get(halfWidth),(-halfWidth*2+1)*mass/head.mass));
	}
	
	//update the state of this ejection
	//return 0: normal; return -1:need to be deleted;
	public int update(ComponentGroup mGroup, BallObject ball){
		long timePassed = System.currentTimeMillis()-startTime;
		ArrayList<PointF> colPoints = null;
		for(int j=0; j<halfWidth*2+1; j++){
			//PointF nextPoint = new PointF(origin.x+velocity.get(j).getX()*timePassed/1000, 
			//		origin.y+velocity.get(j).getY()*timePassed/1000);
			//speed with damping
			PointF nextPoint = null;
			if(damping == 0){
				nextPoint = new PointF(origin.x+velocity.get(j).getX()*timePassed/1000, 
						origin.y+velocity.get(j).getY()*timePassed/1000);
			}
			else{
				nextPoint = new PointF(origin.x+(float)(velocity.get(j).getX()*(1/damping-1/(damping*Math.exp(damping*timePassed)))/1000), 
						origin.y+(float)(velocity.get(j).getY()*(1/damping-1/(damping*Math.exp(damping*timePassed)))/1000));
			}
			
			CollisionModel line = new CollisionModel();
			line.addLine(previousPoints.get(j), nextPoint, "");
			if(collisions.get(j)==false){
				
				//component collision
				for(int i=0; i<mGroup.getObjectNum(); i++){
					colPoints = line.detectCollision(mGroup.getGameObjectByIndex(i).getCollisionModel());
					if(colPoints.size()!=0){
	
						collisions.set(j, true);
						break;
					}
				}
				
				//ball collision
				PointF point = new PointF(0,0);
				colPoints = line.detectCollision(ball.getCollisionModel());
				if(colPoints.size()!=0){
					for(int i = 0;i<colPoints.size();i++){
						point.x+=colPoints.get(i).x;
						point.y+=colPoints.get(i).y;
					}
					point.x = point.x/colPoints.size();
					point.y = point.y/colPoints.size();
					VectorAttr ballMotion = ball.getMotion();
					VectorAttr motionAttr = VectorAttr.mulVector(velocity.get(j),(float)Math.exp(-(double)(System.currentTimeMillis()-startTime)*damping));
					float tmpVX = ballMotion.getX()-motionAttr.getX();
					float tmpVY = ballMotion.getY()-motionAttr.getY();
					VectorAttr vRelated = new VectorAttr(tmpVX,tmpVY);
					VectorAttr normal = new VectorAttr((ball.getX()-point.x), (ball.getY()-point.y));
					VectorAttr vnRelated = VectorAttr.projectVector(vRelated, normal);
					VectorAttr vtRelated = new VectorAttr(vRelated.getX()-vnRelated.getX(),
							vRelated.getY()-vnRelated.getY());
					VectorAttr dvBall = VectorAttr.mulVector(vnRelated, (-mass+ball.mass)/(mass+ball.mass));
					
					ballMotion.set(VectorAttr.addVector(vtRelated,VectorAttr.addVector(dvBall,motionAttr)));
					collisions.set(j, true);
				}
			}	

			previousPoints.get(j).set(currentPoints.get(j));
			currentPoints.get(j).set(nextPoint);
		}
		if(timePassed>MAX_TIME){
			return -1;
		}
		return 0;
	}
	
	public boolean draw(Canvas canvas){
		Paint tmpPaint = new Paint();
		tmpPaint.setColor(Color.BLUE);
		
		for(int i = 0; i<halfWidth*2+1; i++){
			if(collisions.get(i)==false){
				//if the speed is low, draw it lightly
				tmpPaint.setAlpha((int)(255*((Math.exp(-(double)(System.currentTimeMillis()-startTime)*damping)))));
				canvas.drawCircle(currentPoints.get(i).x, currentPoints.get(i).y,3, tmpPaint);
			}//canvas.drawCircle(33, 33,10, tmpPaint);
		}
		
		return true;
	}
	
}
