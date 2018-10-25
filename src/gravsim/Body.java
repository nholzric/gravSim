/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gravsim;

import java.lang.Math;

/**
 *
 * @author Nathan
 */
public class Body implements BodyInterface{
    private String name;
    private Coordinate position;
    private Coordinate velocity;
    private Coordinate acceleration;
    
    Body(String name, Coordinate position, Coordinate velocity){
	this.name = name;
	this.position = position;
	this.velocity = velocity;
	this.acceleration = new Coordinate();
    }
    
    public double getGravityMass(){return 0.0;}
    public double getMythiumMass(){return 0.0;}
    public String getName(){return name;}
    public Coordinate getPosition(){return position;}
    public Coordinate getVelocity(){return velocity;}
    
    public Coordinate calcDistanceVec(BodyInterface b){
	Coordinate secondPosition = b.getPosition();
	return new Coordinate(position.x - secondPosition.x, position.y - secondPosition.y);
    }
    public double calcDistance(Coordinate secondPosition){
	return Math.hypot(position.x - secondPosition.x, position.y - secondPosition.y);
//	return Math.sqrt( Math.pow(position.x - secondPosition.x,2) +
//		Math.pow(position.y - secondPosition.y,2));
    }
    
    //calculation acceleration vector from this body to b, given gravitational constant G and r power p
    public Coordinate calcAcceleration(BodyInterface b){
	return new Coordinate(); //return 0 contribution to acceleration
    }
    public AccelerationInfo calcAccelerationDetailed(BodyInterface b){
	return new AccelerationInfo(this.calcAcceleration(b),this.calcDistanceVec(b));
    }
    
    public Coordinate calcRelativeVelocity(BodyInterface b){
	Coordinate bVelocityVec = b.getVelocity();
	return new Coordinate(this.velocity.x-bVelocityVec.x,this.velocity.y-bVelocityVec.y);
    }
    
    public void setAcceleration(Coordinate newA){acceleration = newA;}
    
    public void move(double timeStep){
	velocity.add(acceleration.multiply(timeStep));
	position.add(velocity.multiply(timeStep));
    }
    
    public String positionString(){return String.format("%f\t%f",position.x,position.y);}
    public String velocityString(){return String.format("%f\t%f",velocity.x,velocity.y);}
    public String accelerationString(){return String.format("%f\t%f",acceleration.x,acceleration.y);}
}
