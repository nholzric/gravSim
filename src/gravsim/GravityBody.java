/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gravsim;

/**
 *
 * @author Nathan
 */
public class GravityBody implements BodyInterface{
    BodyInterface childBodyObject;
    double gravityMass;
    
    GravityBody(BodyInterface childBodyObject,double mass){
	this.childBodyObject = childBodyObject;
	this.gravityMass = mass;
    }
    
    public Coordinate calcAcceleration(BodyInterface b){
	double aMag = Constants.getG() * b.getGravityMass() / Math.pow(this.calcDistance(b.getPosition()), Constants.getGravityP());
	double aTheta = childBodyObject.getPosition().getAngleTo(b.getPosition());
	Coordinate totalAcceleration = Coordinate.RadialCoordinate(aMag, aTheta);
	totalAcceleration.add(childBodyObject.calcAcceleration(b));
	
	return totalAcceleration;
    }
    public Coordinate calcAcceleration(AccelerationInfo aInfo,BodyInterface b){
	double aMag = Constants.getG() * b.getGravityMass() / Math.pow(aInfo.getDistance().getMagnitude(), Constants.getGravityP());
	double aTheta = childBodyObject.getPosition().getAngleTo(b.getPosition());
	Coordinate totalAcceleration = Coordinate.RadialCoordinate(aMag, aTheta);
	
	return totalAcceleration;
    }
    public AccelerationInfo calcAccelerationDetailed(BodyInterface b){
	//get acceleration vector and compute distance in child body
	AccelerationInfo childAInfo = childBodyObject.calcAccelerationDetailed(b);

	//get acceleration from this decorator
	Coordinate thisAcceleration = calcAcceleration(childAInfo,b);
	
	//add acceleration vectors together
	childAInfo.addA(thisAcceleration);

	//return update acceleration info
	return childAInfo;
    }
    
    public double getGravityMass(){return gravityMass + childBodyObject.getGravityMass();}
    public double getMythiumMass(){return childBodyObject.getMythiumMass();}
    public Coordinate getPosition(){return childBodyObject.getPosition();}
    public Coordinate getVelocity(){return childBodyObject.getVelocity();}
    public String getName(){return childBodyObject.getName();}
    public Coordinate calcDistanceVec(BodyInterface b){return childBodyObject.calcDistanceVec(b);}
    public double calcDistance(Coordinate secondPosition){return childBodyObject.calcDistance(secondPosition);}
    public Coordinate calcRelativeVelocity(BodyInterface b){return childBodyObject.calcRelativeVelocity(b);}
    public void setAcceleration(Coordinate newA){childBodyObject.setAcceleration(newA);}
    public void move(double timeStep){childBodyObject.move(timeStep);}
    public String positionString(){return childBodyObject.positionString();}
    public String velocityString(){return childBodyObject.velocityString();}
    public String accelerationString(){return childBodyObject.accelerationString();}
}
