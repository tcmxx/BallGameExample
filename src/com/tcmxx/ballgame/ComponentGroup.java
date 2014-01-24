package com.tcmxx.ballgame;

import java.util.ArrayList;

import com.tcmxx.ballgame.GameObjects.Component;

import android.graphics.Canvas;


public class ComponentGroup {
	protected int objectNum = 0;
	ArrayList<Component> objectArray;
	ArrayList<Boolean> ifVisibleArray;
	ArrayList<String> nameArray;
	////////////////////////////////////////////////////////////////
	public ComponentGroup(){
		objectArray = new ArrayList<Component>();
		ifVisibleArray = new ArrayList<Boolean>();
		nameArray = new ArrayList<String>();
	}
	/////////////////////////////////////////////////////////////////
	public ComponentGroup(int n){
		objectArray = new ArrayList<Component>(n);
		ifVisibleArray = new ArrayList<Boolean>(n);
		nameArray = new ArrayList<String>(n);
	}
	////////////////////////////////////////////////////////////////
	public void drawAll(Canvas c){
		for(int i = 0; i<objectNum;i++){
			objectArray.get(i).draw(c);
		}
		
	}
	///////////////////////////////////////////////////////////////////
	public void addObject(Component gameObject, String name, boolean visible){
		gameObject.rotate(90);
		objectArray.add(gameObject);
		nameArray.add(name);
		ifVisibleArray.add(visible);
		objectNum++;
	}
	//////////////////////////////////////////////////////////////////
	public boolean removeObjectByIndex(int i){
		if(i>=objectNum){
			return false;
		}
		else{
			objectArray.remove(i);
			nameArray.remove(i);
			ifVisibleArray.remove(i);
			objectNum--;
			return true;
		}
	}
	/////////////////////////////////////////////////////////
	public boolean setVisibleByIndex(int i, boolean visible){
		if(i>=objectNum){
			return false;
		}
		else{
			ifVisibleArray.set(i, visible);
			return true;
		}
	}
	//////////////////////////////////////////////
	public int getIndexByName(String name){
		return nameArray.indexOf(name);
	}
	//////////////////////////////////////////////
	public Component getGameObjectByIndex(int i){
		if(i>=objectNum){
			return null;
		}
		else{
			return objectArray.get(i);
		}
	}
	////////////////////////////////////////
	public boolean getVisibleByIndex(int i){
		if(i>=objectNum){
			return false;
		}
		else{
			return ifVisibleArray.get(i);
		}
	}
	////////////////////////////////////////////////////////
	public int getObjectNum(){
		return objectNum;
	}
}
