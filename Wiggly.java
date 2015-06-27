/*
 * Wiggly.java
 * 
 * Copyright 2015 Kate Barr <plaguenursethorne@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 * 
 * 
 */

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.effect.*;
import javafx.stage.Stage;
import javafx.scene.input.*;
import javafx.scene.image.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.shape.*;
import javafx.scene.input.*;
import javafx.scene.transform.*;
import java.lang.Math;
import javafx.animation.*;

public class Wiggly extends Application
{
	@Override
	public void start(Stage primaryStage)
	{
		Group root = new Group();
		int width = 700;
		int height = 700;
		Canvas canvas = new Canvas(700,700);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		WigglyGuy james = new WigglyGuy(15,350,730);
		root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root,700,700,Color.web("#ffedc9")));
		
		new Action(james,gc).start();
		
		
        primaryStage.show();
        
	}

	
	public static void main (String args[]) 
	{
		launch(args);
	}
}
class Action extends AnimationTimer 
{
	GraphicsContext gc;
	//Time time;
	WigglyGuy james;
	Image face;
	final long startNanoTime = System.nanoTime();
	Action(WigglyGuy james, GraphicsContext gc)
	{
		this.gc = gc;
		this.james = james;
		face = new Image("face.png");
	}
	@Override
	public void handle(long currentNanoTime)
	{
			gc.clearRect(0,0,700,700);
			james.getNext().timeflow((currentNanoTime - startNanoTime) / 22000000.0);
			james.propagate(0,0);
			drawWiggle(james, gc,face);

	}
	
	public static void drawWiggle(WigglyGuy person, GraphicsContext gc, Image face)
	{
		gc.setStroke(Color.web("#17ff3d"));
		gc.setLineWidth(40);
		gc.setLineCap(StrokeLineCap.ROUND);
		gc.setLineJoin(StrokeLineJoin.BEVEL);
		WigglyGuy guy = person;
		gc.beginPath();
		gc.moveTo(guy.getX(), guy.getY());
		int i = 1;
		while(guy.getNext() != null && guy.getNext().getNext() != null)
		{
			gc.strokeLine(guy.getX(),guy.getY(),
				guy.getNext().getX(),guy.getNext().getY());
			guy = guy.getNext();
			if(guy.hasArms())
			{
				drawArm(guy.getArmLeft(),gc);
				drawArm(guy.getArmRight(),gc);
				gc.setLineWidth(40);
			}
			
		}
			gc.strokeLine(guy.getX(),guy.getY(),
				guy.getNext().getX(),guy.getNext().getY());
		gc.stroke();
		//face goes here  *u*
		gc.translate(guy.getNext().getX(),guy.getNext().getY());
		gc.rotate(-1.0*Math.atan((guy.getNext().getY() - guy.getY())/(guy.getNext().getX() - guy.getX())));
		gc.drawImage(face,-20,-40);
		gc.rotate(1.0*Math.atan((guy.getNext().getY() - guy.getY())/(guy.getNext().getX() - guy.getX())));
		gc.translate(-guy.getNext().getX(),-guy.getNext().getY());
		//end face :(

	}
	public static void drawArm(Arm arm, GraphicsContext gc)
	{
		gc.setLineWidth(20);
		gc.moveTo(arm.getX(), arm.getY());
		while(arm.getNext() != null)
		{
			gc.strokeLine(arm.getX(),arm.getY(),
				arm.getNext().getX(),arm.getNext().getY());
			arm = arm.getNext();			
		}
	}
}
class WigglyGuy
{
	private double x;
	private double y;
	private int num;
	private double force = 9.0;
	private WigglyGuy next = null;
	private Arm armLeft = null;
	private Arm armRight = null;
	WigglyGuy(int num, double x, double y)
	{
		this.x = x;
		this.y = y;
		this.num = num;
		if(num==4)
		{
			this.next = new WigglyGuy(num-1,x,y-30); 
			this.armLeft = new Arm(10,x,y,false);
			this.armRight = new Arm(10,x,y,true);
		}
		else if(num != 0)
			this.next = new WigglyGuy(num-1,x,y-30); 
		else
			this.next = null;
	}
	void timeflow(double time)
	{
		this.x+=12*Math.sin(((time)*Math.PI)/6);
	}
	void propagate(double shiftX, double shiftY)
	{
		if(this.next != null)
		{
			this.next.propagate(
				
				(this.x-this.next.getX()),
				
				(this.y-this.next.getY()-30)
				
				);
		}
		this.x+=shiftX;
		this.y+=(shiftY);
		if (this.hasArms())
		{
			this.armLeft.propagate(shiftX,(shiftY-shiftX));
			this.armRight.propagate(shiftX,(shiftY+shiftX));
		}
		
	}
	double getX()
	{
		return this.x;
	}
	double getForce()
	{
		return this.force;
	}
	void setX(double x)
	{
		this.x = x;
	}
	double getY()
	{
		return this.y;
	}
	void setY(double y)
	{
		this.y = y;
	}
	boolean hasArms()
	{
		return(this.armLeft != null);
	}
	Arm getArmLeft()
	{
		return this.armLeft;
	}
	Arm getArmRight()
	{
		return this.armRight;
	}
	WigglyGuy getNext()
	{
		return this.next;
	}
	
}
class Arm
{
	private double x;
	private double y;
	private int num;
	private double force = 9;
	private boolean direction;
	Arm next = null;
	Arm(int num, double x, double y, boolean direction)
	{
		this.num=num;
		this.x=x;
		this.y=y;
		this.direction= direction;
		if(num!=0)
		{
			next = new Arm(num-1,x + (direction?-15:15),y,direction);
		}
	}
	
	void propagate(double shiftX, double shiftY)
	{
		if(this.next != null)
		{
			this.next.propagate(
				
				(this.x-this.next.getX()+ (direction?15:-15)),
				
				(this.y-this.next.getY()+ (direction?1:-1)*(this.x-this.next.getX()))
				
				);
		}
		this.x+=shiftX;
		this.y+=shiftY;
	}
	double getX()
	{
		return this.x;
	}
	double getForce()
	{
		return this.force;
	}
	void setX(double x)
	{
		this.x = x;
	}
	double getY()
	{
		return this.y;
	}
	void setY(double y)
	{
		this.y = y;
	}
	Arm getNext()
	{
		return this.next;
	}
}

