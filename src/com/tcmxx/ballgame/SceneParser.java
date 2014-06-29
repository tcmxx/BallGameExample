

package com.tcmxx.ballgame;
import java.io.InputStream;  
   
import org.xmlpull.v1.XmlPullParser;  

import com.tcmxx.ballgame.GameObjects.*;
  
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.util.Xml;  

//parse the InputStream XML, then store it in the ComponentGroup Object passed by the argument.
public class SceneParser {
	public void parse (InputStream is, MovementView movementView, Resources res)
			throws Exception {    
          
//      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();  
//      XmlPullParser parser = factory.newPullParser();  
          
        XmlPullParser parser = Xml.newPullParser(); //由android.util.Xml创建一个XmlPullParser实例  
        parser.setInput(is, "UTF-8");               //设置输入流 并指明编码方式  
  
        int eventType = parser.getEventType();  
        while (eventType != XmlPullParser.END_DOCUMENT) {  
            switch (eventType) {  
            case XmlPullParser.START_DOCUMENT:   
                break;  
            case XmlPullParser.START_TAG:  
                if (parser.getName().equals("brick")) { 
                	//parse the brick tag, store it in the component group
                	parseBrick(parser, movementView.brickGroup, res);  
                } else if (parser.getName().equals("indoor")
                		||parser.getName().equals("outdoor")) { 
                	//parse the brick tag, store it in the component group
                	parseDoor(parser, movementView.brickGroup, res);  
                }
                break;  
            case XmlPullParser.END_TAG:  

                break;  
            }  
            eventType = parser.next();  
        }  
         
    }  
	
	//parse door tag of xml
	private void parseDoor(XmlPullParser parser, ComponentGroup mGroup, Resources res)
			throws Exception{
		int eventType = parser.next();  
		float x=0, y=0;
		int id = 0;
		while (true) {
			switch (eventType){
			case XmlPullParser.START_TAG:
				if (parser.getName().equals("x")) { 
					eventType = parser.next();  
	                x = Float.parseFloat(parser.getText());
	            } else if (parser.getName().equals("y")) {  
					eventType = parser.next();  
	                y = Float.parseFloat(parser.getText()); 
	            } else if (parser.getName().equals("id")) {  
	                eventType = parser.next();  
	                id = Integer.parseInt(parser.getText());   
	            } 
				break;
			case XmlPullParser.END_TAG:  
				if(parser.getName().equals("indoor")){
                	Door mDoor = new Door(true);
                	mDoor.setPosition(x, y);
                	mDoor.loadBitmap(BitmapFactory.decodeResource(res, R.drawable.indoor));
                	mGroup.addObject(mDoor, Integer.toString(id), true);
                	return;
                }else if(parser.getName().equals("outdoor")){
                	Door mDoor = new Door(false);
                	mDoor.setPosition(x, y);
                	mDoor.loadBitmap(BitmapFactory.decodeResource(res, R.drawable.outdoor));
                	mGroup.addObject(mDoor, Integer.toString(id), true);
                	return;
                }
				break;
			}
			eventType = parser.next();  
		}
	}
	//parse brick tag of xml
	//x,y: position; w: rotation;
	//width, height: width and height;
	//visibility: if visible
	//texture: 0~wood, 1~stone;
	private void parseBrick(XmlPullParser parser, ComponentGroup mGroup, Resources res) 
			throws Exception{
		int eventType = parser.next();  

		float x=0, y=0, w=0;
		float width=0, height=0;
		int id = 0;
		boolean visibility = true;
		int texture = 0, lives = 1;
		while (true) {  
			switch (eventType) { 
			case XmlPullParser.START_TAG:
				if (parser.getName().equals("x")) { 
					eventType = parser.next();  
	                x = Float.parseFloat(parser.getText());
	            } else if (parser.getName().equals("y")) {  
					eventType = parser.next();  
	                y = Float.parseFloat(parser.getText()); 
	            } else if (parser.getName().equals("w")) {  
					eventType = parser.next();  
	                w = Float.parseFloat(parser.getText()); 
	            } else if (parser.getName().equals("width")) {  
	                eventType = parser.next();  
	                width = Float.parseFloat(parser.getText());  
	            } else if (parser.getName().equals("height")) {  
	                eventType = parser.next();  
	                height = Float.parseFloat(parser.getText());   
	            } else if (parser.getName().equals("id")) {  
	                eventType = parser.next();  
	                id = Integer.parseInt(parser.getText());   
	            } else if (parser.getName().equals("visibility")) {  
	                eventType = parser.next();  
	                visibility = Boolean.parseBoolean(parser.getText());   
	            } else if (parser.getName().equals("texture")) {  
	                eventType = parser.next();  
	                texture = Integer.parseInt(parser.getText());   
	            } else if (parser.getName().equals("lives")) {  
	                eventType = parser.next();  
	                lives = Integer.parseInt(parser.getText());   
	            }
	            break;  
			case XmlPullParser.END_TAG:  
                if (parser.getName().equals("brick")) {  
            		BrickObject  mBrick = new BrickObject(width, height);  
            		mBrick.setPosition(x, y);
            		mBrick.rotate(w);
            		mBrick.lives = lives;
            		if(texture == 0){
            			mBrick.loadBitmap(BitmapFactory.decodeResource(res, R.drawable.wood_brick));
            		}else if(texture == 1){
            			mBrick.loadBitmap(BitmapFactory.decodeResource(res, R.drawable.stone_brick));
            		}
					mGroup.addObject(mBrick, Integer.toString(id), visibility);
					return;
                }
				break;
			}
			eventType = parser.next();  
		}
	}
}
