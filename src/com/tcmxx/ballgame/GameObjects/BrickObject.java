package com.tcmxx.ballgame.GameObjects;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import com.tcmxx.ballgame.VectorAttr;

public class BrickObject extends Component {

	protected int color = Color.BLACK;
	protected RectF rectF;
	public int lives;				//how many time the brick can be hit
	public float stiffCoef=1;		//energy loss of ball when the ball hit it 
	
	public BrickObject(){
		super();
		rectF = new RectF();
	}
	public BrickObject(float w, float h){
		super();
		rectF = new RectF();
		width = w;
		height = h;
		collisionRadius = (float)Math.sqrt(width*width+height*height)/2;
		rectF.set(xPos-width/2, yPos-height/2, xPos+width/2, yPos+height/2);
		paint.setColor(color);
		collisionModel.addLine(new PointF(-width/2, -height/2), new PointF(-width/2, +height/2), "");
		collisionModel.addLine(new PointF(-width/2, +height/2), new PointF(+width/2, +height/2), "");
		collisionModel.addLine(new PointF(+width/2, +height/2), new PointF(+width/2, -height/2), "");
		collisionModel.addLine(new PointF(+width/2, -height/2), new PointF(-width/2, -height/2), "");
		collisionModel.setCollisionRadius((float)Math.sqrt(width*width+height*height)/2);
		collisionModel.setNodePosition(xPos, yPos);

	}
	public BrickObject(float l, float t,float r, float b){
		this(r-l,b-t);
		setPosition((r+l)/2,(b+t)/2);
		
	}
	
	////////////////////////////////////////////////////////
	//////////////////change the geometry of the rect
	public void setGeometry(float w, float h){
		width = w;
		height = h;
		rectF.set(xPos-width/2, yPos-height/2, xPos+width/2, yPos+height/2);
		collisionRadius = (float)Math.sqrt(width*width+height*height)/2;
		collisionModel.setLineRelatedStartByIndex(0, -width/2, -height/2);
		collisionModel.setLineRelatedEndByIndex(0, -width/2, height/2);
		collisionModel.setLineRelatedStartByIndex(1, -width/2, height/2);
		collisionModel.setLineRelatedEndByIndex(1, width/2, height/2);		
		collisionModel.setLineRelatedStartByIndex(2, width/2, height/2);
		collisionModel.setLineRelatedEndByIndex(2, width/2, -height/2);		
		collisionModel.setLineRelatedStartByIndex(3, width/2, -height/2);
		collisionModel.setLineRelatedEndByIndex(3, -width/2, -height/2);
		collisionModel.setCollisionRadius((float)Math.sqrt(width*width+height*height)/2);
	
	}
	
	
	@Override
	public boolean setPosition(float x, float y) {
		// TODO Auto-generated method stub
		xPos = x;
		yPos = y;
		collisionModel.setNodePosition(x, y);
		rectF.set(x-width/2, y-height/2, x+width/2, y+height/2);
		return true;
	}
	@Override
	public void rotate(float degree) {
		// TODO Auto-generated method stub
		super.rotate(degree);
		Matrix r = new Matrix();
		r.setRotate(degree,xPos,yPos);
		r.mapRect(rectF);
	}
	public void setColor(int c){
		color = c;
		paint.setColor(color);
	}
	public boolean draw(Canvas canvas) {
		// TODO Auto-generated method stub
		
		canvas.drawRect(rectF, paint);
		return true;
	}
	public float getWidth(){
		return width;
	}
	public float getHeight(){
		return height;
	}
	
	//called every frame for each brick
	public int effectBall(BallObject ball){
		ArrayList<PointF> collisionPoints=ball.getCollisionModel().detectCollision(collisionModel);
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
			VectorAttr v = ball.getMotion();
			VectorAttr normal = new VectorAttr((point.x-ball.getX()), (point.y-ball.getY()));
			v.set(VectorAttr.reflectVector(v, normal));
			v.setValue(v.getValue()*stiffCoef, v.getAngle());
			return -1;		//hit the ball
		}
		
	}
	public int effectHead(HeadObject head){return 1;}
}
