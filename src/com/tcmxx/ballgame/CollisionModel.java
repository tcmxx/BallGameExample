package com.tcmxx.ballgame;

import java.util.ArrayList;

import android.graphics.PointF;


public class CollisionModel {

	public static final int COLLISION = 1;
	public static final int UNCOLLISION = 0;
	public static final float NAMDA = 0.001f;
	//following contains data of lines
	private ArrayList<LineModel> lineList= new ArrayList<LineModel>();;
	private ArrayList<PointF> lineEndRelatedPositions= new ArrayList<PointF>();; 
	private ArrayList<PointF> lineStartRelatedPositions= new ArrayList<PointF>();; 
	private ArrayList<Integer> lineState= new ArrayList<Integer>();;
	private ArrayList<String> lineName= new ArrayList<String>();;
	//following contains data of circles
	private ArrayList<CircleModel> circleList = new ArrayList<CircleModel>();
	private ArrayList<PointF> circleCenterRelatedPositions = new ArrayList<PointF>();
	private ArrayList<Integer> circleState = new ArrayList<Integer>();
	private ArrayList<String> circleName = new ArrayList<String>();
	//-------following contains data of points ----------------------///
	//this is not accurate, only for test
	public static final float RAYINF = 9999;
	private ArrayList<LineModel> rayList= new ArrayList<LineModel>();
	private ArrayList<PointF> pointRelatedPositions = new ArrayList<PointF>();
	private ArrayList<Integer> pointState = new ArrayList<Integer>();
	private ArrayList<String> pointName = new ArrayList<String>();
	public PointF getRayS(int i){return rayList.get(i).start;}
	public PointF getRayE(int i){return rayList.get(i).end;}
	//-------------////////////////////////////////
	protected float collisionRadius = 0;
	protected int pointNum;
	protected int circleNum;
	protected int lineNum;
	protected float xNode, yNode;
	
	public CollisionModel(float x, float y){
		xNode = x;
		yNode = y;
	}
	
	public CollisionModel(){

	}
	//add a basic circle to the model, c is the related center, 
	//r is the radius and name is the string name.
	//test with points are not accurate
	public void addPoint(PointF point, String name){
		LineModel temp= new LineModel(new PointF(point.x+xNode, point.y+yNode),
				new PointF(RAYINF, RAYINF));
		rayList.add(temp);
		pointRelatedPositions.add(point);
		pointState.add(1);
		pointName.add(name);
		pointNum++;
		
	}
	
	
	//add a basic circle to the model, c is the related center, 
	//r is the radius and name is the string name.
	public void addCircle(PointF c, float r, String name){
		CircleModel temp= new CircleModel(c.x+xNode, c.y+yNode, r);
		circleList.add(temp);
		circleCenterRelatedPositions.add(c);
		circleState.add(1);
		circleName.add(name);
		circleNum++;
		
	}
	//add a basic line to the model, c is the related center, 
	//r is the radius and name is the string name.
	public void addLine(PointF s, PointF e, String name){
		
		
		LineModel temp= new LineModel(new PointF(s.x+xNode, s.y+yNode),
				new PointF(e.x+xNode, e.y+yNode));
		lineList.add(temp);
		lineStartRelatedPositions.add(s);
		lineEndRelatedPositions.add(e);
		lineState.add(1);
		lineName.add(name);
		lineNum++;
		
		
	}
	public boolean removeCircleByIndex(int i){
		if(i>=circleNum){
			return false;
		}
		circleList.remove(i);
		circleCenterRelatedPositions.remove(i);
		circleState.remove(i);
		circleName.remove(i);
		circleNum--;
		return true;
	}
	public boolean removeLineByIndex(int i){
		if(i>=lineNum){
			return false;
		}
		lineList.remove(i);
		lineStartRelatedPositions.remove(i);
		lineEndRelatedPositions.remove(i);
		lineState.remove(i);
		lineName.remove(i);
		lineNum--;
		return true;
	}

	public boolean setCircleRelatedCenterByIndex(int i, PointF c){
		if(i<circleNum){
			circleList.get(i).setPosition(c.x+xNode, c.y+yNode);
			circleCenterRelatedPositions.get(i).x = c.x;
			circleCenterRelatedPositions.get(i).y = c.x;
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean setCircleRadiusByIndex(int i, float r){
		if(i<circleNum){
			circleList.get(i).setRadius(r);
			return true;
		}
		else{
			return false;
		}
	}
	public boolean setLineRelatedStartByIndex(int i, float x, float y){
		if(i<lineNum){
			
			lineStartRelatedPositions.get(i).x = x;
			lineStartRelatedPositions.get(i).y = y;
			lineList.get(i).getStart().x = x+xNode;
			lineList.get(i).getStart().y = y+yNode;
			return true;
		}
		else{
			return false;
		}
	}
	public boolean setLineRelatedEndByIndex(int i, float x, float y){
		if(i<lineNum){
			
			lineEndRelatedPositions.get(i).x = x;
			lineEndRelatedPositions.get(i).y = y;
			lineList.get(i).getStart().x = x+xNode;
			lineList.get(i).getStart().y = y+yNode;
			return true;
		}
		else{
			return false;
		}
	}
	public void setCollisionRadius(float r){
		collisionRadius = r;
	}
	
	///////////////////////////////////////////////////////////
	//simple collision detection, if this returns true, more/// 
	//accurate detection will be done(only effective when collision 
	//radius not equals to 0)						///	
	public boolean isCollisionPossible(CollisionModel o2){
		if(collisionRadius!= 0 && o2.collisionRadius != 0){
			if(Math.sqrt((xNode-o2.xNode)*(xNode-o2.xNode)+(yNode-o2.yNode)*(yNode-o2.yNode))>collisionRadius+o2.collisionRadius){
				return false;
			}
			return true;
		}

		else{
			return true;
		}
	}
	//if the points are in closure of another model(input).
	//this is not accurate!!!!
	//return the points index that is in closure
	public ArrayList<Integer> inClosure(CollisionModel o){
		ArrayList<Integer> results = new ArrayList<Integer>();
		int numOfCol=0;
		for(int i = 0; i<pointNum; i++){
			numOfCol=0;
			for(int k = 0; k<o.lineNum; k++){
				if(o.lineState.get(k)==1){
					if(lineLineCollision(rayList.get(i).getStart(), rayList.get(i).getEnd(),
							o.lineList.get(k).getStart(), o.lineList.get(k).getEnd())!=null){
						numOfCol+=1;
					}
				}
			}
			for(int k = 0; k<o.circleNum; k++){
				if(o.circleState.get(k)==1){
					PointF[] tmp = lineCircleCollision(o.circleList.get(k).getPosition(), o.circleList.get(k).getRadius(),
							rayList.get(i).getStart(), rayList.get(i).getEnd());
					if(tmp!=null)
						numOfCol+=(tmp.length);
				}
			}
			if(numOfCol%2!=0){
				results.add(i);
			}
		}
		return results;
	}
	//detect collisions, return the points
	public ArrayList<PointF> detectCollision(CollisionModel o){

		ArrayList<PointF> results = new ArrayList<PointF>();
		if(isCollisionPossible(o)==false){
			return results;
		}
		PointF[] tmpP = new PointF[2];
		for(int i = 0; i<lineNum; i++){
			for(int k = 0; k<o.lineNum; k++){
				if(lineState.get(i)==1&&o.lineState.get(k)==1){
					tmpP[0] = lineLineCollision(lineList.get(i).getStart(), lineList.get(i).getEnd(),
							o.lineList.get(k).getStart(), o.lineList.get(k).getEnd());
					if(tmpP[0]!=null){
						results.add(tmpP[0]);
					}
				}
			}
		}
		for(int i = 0; i<circleNum; i++){
			for(int k = 0; k<o.circleNum; k++){
				if(circleState.get(i)==1&&o.circleState.get(k)==1){
					tmpP[0] = circleCircleCollision(circleList.get(i).getPosition(), circleList.get(i).getRadius(),
							o.circleList.get(k).getPosition(), o.circleList.get(k).getRadius());
					if(tmpP[0]!=null){
						results.add(tmpP[0]);
					}
				}
			}
		}
		for(int i = 0; i<lineNum; i++){
			for(int k = 0; k<o.circleNum; k++){
				if(lineState.get(i)==1&&o.circleState.get(k)==1){
					tmpP = lineCircleCollision(o.circleList.get(i).getPosition(), o.circleList.get(i).getRadius(),
							lineList.get(k).getStart(), lineList.get(k).getEnd());
					if(tmpP == null){
						continue;
					}
					for(int b = 0; b<tmpP.length;b++){
						if(tmpP[b]!=null){
							results.add(tmpP[b]);
						}
					}

				}
			}
		}
		for(int i = 0; i<circleNum; i++){
			for(int k = 0; k<o.lineNum; k++){
				if(circleState.get(i)==1&&o.lineState.get(k)==1){

					tmpP = lineCircleCollision(circleList.get(i).getPosition(), circleList.get(i).getRadius(),
							o.lineList.get(k).getStart(), o.lineList.get(k).getEnd());

					if(tmpP == null){
						continue;
					}
					for(int b = 0; b<tmpP.length;b++){
						if(tmpP[b]!=null){
							results.add(tmpP[b]);
						}
					}
				}
			}
		}
		return results;
		
	}
	//set the position of the collision model
	public void setNodePosition(float xx, float yy){
		for(int i = 0; i<circleNum;i++){
			CircleModel tmpC;
			PointF tmpP;
			tmpC = circleList.get(i);
			tmpP = circleCenterRelatedPositions.get(i);
			tmpC.setPosition(xx+tmpP.x, yy+tmpP.y);
		}
		for(int i = 0; i<lineNum;i++){
			LineModel tmpL;
			PointF tmpPS,tmpPE, tmpS,tmpE;
			tmpL = lineList.get(i);
			tmpPS = lineStartRelatedPositions.get(i);
			tmpPE = lineEndRelatedPositions.get(i);
			tmpS = tmpL.getStart();
			tmpE = tmpL.getEnd();
			tmpS.x = tmpPS.x+xx;
			tmpS.y = tmpPS.y+yy;
			tmpE.x = tmpPE.x+xx;
			tmpE.y = tmpPE.y+yy;
		}
		for(int i = 0; i<pointNum;i++){
			LineModel tmpRay;
			PointF tmpP, tmpS;
			tmpRay = rayList.get(i);
			tmpP = pointRelatedPositions.get(i);
			tmpS = tmpRay.getStart();
			tmpS.x = tmpP.x+xx;
			tmpS.y = tmpP.y+yy;
		}
		xNode = xx;
		yNode = yy;
	}
	//rotate the model from origin
	public void rotate(float degree){
		for(int i = 0; i<circleNum;i++){
			CircleModel tmpC;
			PointF tmpP;
			tmpC = circleList.get(i);
			tmpP = circleCenterRelatedPositions.get(i);
			BallGameUtils.rotatePointF(tmpP,tmpP,degree);
			tmpC.setPosition(xNode+tmpP.x, yNode+tmpP.y);
		}
		for(int i = 0; i<lineNum;i++){
			LineModel tmpL;
			PointF tmpPS,tmpPE, tmpS,tmpE;
			tmpL = lineList.get(i);
			tmpPS = lineStartRelatedPositions.get(i);
			tmpPE = lineEndRelatedPositions.get(i);
			BallGameUtils.rotatePointF(tmpPS,tmpPS,degree);
			BallGameUtils.rotatePointF(tmpPE,tmpPE,degree);
			tmpS = tmpL.getStart();
			tmpE = tmpL.getEnd();
			tmpS.x = tmpPS.x+xNode;
			tmpS.y = tmpPS.y+yNode;
			tmpE.x = tmpPE.x+xNode;
			tmpE.y = tmpPE.y+yNode;
		}
		for(int i = 0; i<pointNum;i++){
			LineModel tmpRay;
			PointF tmpP, tmpS;
			tmpRay = rayList.get(i);
			tmpP = pointRelatedPositions.get(i);
			BallGameUtils.rotatePointF(tmpP,tmpP,degree);
			tmpS = tmpRay.getStart();
			tmpS.x = tmpP.x+xNode;
			tmpS.y = tmpP.y+yNode;
		}
	}
	public float getNodeX(){
		return xNode;
	}
	public float getNodeY(){
		return yNode;
	}
	
	//some basic models(lines and circles)
  	private class CircleModel {
		protected float x;
		protected float y;
		protected float r;
		
		protected CircleModel(float xx, float yy, float rr){
			x = xx;
			y = yy;
			r = rr;
		}
		
		protected boolean setPosition(float xx, float yy){
			x = xx;
			y = yy;
			return true;
		}
		public PointF getPosition(){
			return new PointF(x,y);
		}

		public float getRadius(){
			return r;
		}
		public void setRadius(float rr){
			r = rr;
		}
		
	}
	
	private class LineModel{
		protected PointF start;
		protected PointF end;
		
		public LineModel(PointF s, PointF e){
			start = s;
			end = e;
		}
		
		public PointF getStart(){
			return start;
		}
		public PointF getEnd(){
			return end;
		}
	}
	
	///////////////////////////////////////////////////////////
	//Basic collision Detection                             ///
	// This one only return the middle point of line circle 
	//collision if two points are overloaded, if one point, 
	//return the point, if not collide, return null
	static public PointF[] lineCircleCollision(PointF center, float r, PointF start, PointF end){
		float y1 = start.y;
		float x1 = start.x;
		float y2 = end.y;
		float x2 = end.x;
		float y3 = center.y;
		float x3 = center.x;
		float midX, midY, int1X, int1Y, int2X, int2Y;
		float halfCut;
		int pointsNum = 0;
		PointF point1 = null, point2 = null;
		if(x1==x2&&y1==y2){
			return null;
		}

		if(x1==x2){
			midY = y3;
			midX = x1;
		}
		else{
			float k = (y2-y1)/(x2-x1);
			float b = y2-k*x2;
			midX = (2*x3-2*k*(b-y3))/(2*k*k+2);
			midY = (y1-y2)/(x1-x2)*midX+(y2*x1-y1*x2)/(x1-x2);
		}
		if((midY-y3)*(midY-y3)+(midX-x3)*(midX-x3)>r*r){
			return null;
		}
		halfCut = (float)Math.sqrt(r*r-(midY-y3)*(midY-y3)-(midX-x3)*(midX-x3));
		if(x1==x2){
			int1Y = midY+halfCut;
			int2Y = midY-halfCut;
			int1X = x1;
			int2X = x1;
		}
		else{
			float slideLine = (float)Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
			int1Y = (midY+halfCut*(y2-y1)/slideLine);
			int1X = (midX+halfCut*(x2-x1)/slideLine);
			int2Y = (midY+halfCut*(y1-y2)/slideLine);
			int2X = (midX+halfCut*(x1-x2)/slideLine);
			
		}
		
		if(int1X>=((x1<x2?x1:x2)-NAMDA)&&int1X<=((x1<x2?x2:x1)+NAMDA)&&int1Y>=((y1<y2?y1:y2)-NAMDA)&&int1Y<=((y1<y2?y2:y1)+NAMDA)){
			pointsNum++;
			point1 = new PointF(int1X, int1Y);
		}
		if(int2X>=((x1<x2?x1:x2)-NAMDA)&&int2X<=((x1<x2?x2:x1)+NAMDA)&&int2Y>=((y1<y2?y1:y2)-NAMDA)&&int2Y<=((y1<y2?y2:y1)+NAMDA)){
			pointsNum++;
			point2 = new PointF(int2X, int2Y);
		}
		PointF[] points = new PointF[pointsNum];
		switch(pointsNum){
		case 0:
			return null;
		case 1:
			if(point1 == null){
				points[0] = point2; 
			}
			else{
				points[0] = point1;
			}
			break;
		case 2:
			points[0] = point1; 
			points[1] = point2; 
			break;
		}	
		return points;
	}
	
	//this method is still not valid when the two lines are parallel
	static public PointF lineLineCollision(PointF start1, PointF end1,PointF start2, PointF end2){
		float y11 = start1.y;
		float x11 = start1.x;
		float y12 = end1.y;
		float x12 = end1.x;
		float y21 = start2.y;
		float x21 = start2.x;
		float y22 = end2.y;
		float x22 = end2.x;
		if((x11==x12&&y11==y12)||(x21==x22&&y21==y22)){
			return null;
		}
		else if(x11-x12==0&&x21-x22==0){
			if(x11==x22){
				if(y11>=(y21>y22?y22:y21)&&y11<=(y21<y22?y22:y21)){
					return new PointF(x11,(y11+(y21<y22?y22:y21))/2);
				}
				else if(y12>=(y21>y22?y22:y21)&&y12<=(y21<y22?y22:y21)){
					return new PointF(x11,(y12+(y21<y22?y22:y21))/2);
				}
				else{
					return null;
				}
				
			}
			else{
				return null;
			}
		}
		else if(y11-y12==0&&y21-y22==0){
			if(y11==y22){
				if(x11>=(x21>x22?x22:x21)&&x11<=(x21<x22?x22:x21)){
					return new PointF((x11+(x21<x22?x22:x21))/2,y11);
				}
				else if(x12>=(x21>x22?x22:x21)&&x12<=(x21<x22?x22:x21)){
					return new PointF((x12+(x21<x22?x22:x21))/2,y11);
				}
				else{
					return null;
				}
				
			}
			else{
				return null;
			}
		}
		else{
			float interX = (x21*(x11 - x12)*(y21 - y22) - x11*(x21 - x22)*(y11 - y12) + 
					(x11 - x12)*(x21 - x22)*(y11 - y21))/((x11 - x12)*(y21 - y22) - (x21 - x22)*(y11 - y12));
			float interY;
			if(x11==x12){
				interY = interX*(y21-y22)/(x21-x22)-(x22*y21-x21*y22)/(x21-x22);
			}
			else{
				interY = interX*(y11-y12)/(x11-x12)-(x12*y11-x11*y12)/(x11-x12);
			}
			//test if the intersection is within each line
			if(interX>=(((x11<x12)?x11:x12)-NAMDA)&&interX<=(((x11<x12)?x12:x11)+NAMDA)&&interY>=(((y11<y12)?y11:y12)-NAMDA)&&(interY<=((y11<y12)?y12:y11)+NAMDA)&&
					interX>=(((x21<x22)?x21:x22)-NAMDA)&&interX<=(((x21<x22)?x22:x21)+NAMDA)&&(interY>=((y21<y22)?y21:y22)-NAMDA)&&interY<=(((y21<y22)?y22:y21))+NAMDA){
				return new PointF(interX,interY);
			}
			else{
				return null;
			}
		}
		
		
	}	
	
	//////////////////////////////////////////////////////////////////////////////
	//only return the middle point
	static public PointF circleCircleCollision(PointF center1, float r1,PointF center2, float r2){
		float x1 = center1.x;
		float y1 = center1.y;
		float x2 = center2.x;
		float y2 = center2.y;
		float d = (x1-x2)*(x1-x2)+(y1-y2)*(y1-y2);
		
		if(d>(r1+r2)*(r1+r2)){
			return null;
		}
		else{
			double k = 0.5+(double)(r2*r2-r1*r1)/(double)(2*d*d);
			float interX = (float)(x2+(x1-x2)*k);
			float interY = (float)(y2+(y1-y2)*k);
			return new PointF(interX, interY);
		}
	}

	
}


