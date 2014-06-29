package com.tcmxx.ballgame.GameObjects;

import java.util.ArrayList;

import com.tcmxx.ballgame.GameObjects.HeadObject.HeadStatus;

import android.graphics.PointF;

public class Door extends Component {

	static final private float  RADIUS = 10;
	public boolean fromOrTo;
	public Door(boolean from) {
		super();
		fromOrTo = from;
		collisionModel.addCircle( new PointF(0,0), RADIUS, "");
		collisionModel.setCollisionRadius(RADIUS);
		width=5*RADIUS;
		height=5*RADIUS;
	}

	@Override
	public int effectBall(BallObject ball, int FPS) {

		return 0;
	}

	@Override
	public int effectHead(HeadObject head, int FPS) {
		ArrayList<PointF> collisionPoints=head.getCollisionModel().detectCollision(collisionModel);
		if(collisionPoints.size()==0){
			return 0;
		}
		else{
			if(!fromOrTo)
				head.status = HeadStatus.NEXTLEVEL;
		}
		return 0;
	}

}
