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
public interface BodyInterface {
    
    public class AccelerationInfo{
	private Coordinate acceleration;
	private Coordinate distance;
	private double maxAngularRate;

	AccelerationInfo(Coordinate acceleration, Coordinate distance){
	    this.acceleration = acceleration;
	    this.distance = distance;
	    maxAngularRate = 0.0;
	}
	AccelerationInfo(){
	    acceleration = new Coordinate();
	    distance = new Coordinate();
	}

	public void updateMaxAngularRate(BodyInterface A, BodyInterface B){
	    Coordinate relativeVelocity = A.calcRelativeVelocity(B);
	    relativeVelocity.add(distance); //this is now the distance 1 second later
	    double thisAngularRate = this.distance.getAngleBetween(relativeVelocity);
	    
	    if(thisAngularRate > maxAngularRate)
		    this.maxAngularRate = thisAngularRate;
	}
	
	public void addA(Coordinate b){this.acceleration.add(b);}
	
	public Coordinate getA(){return acceleration;}
	public Coordinate getDistance(){return distance;}

    }
    
    public double getGravityMass();
    public double getMythiumMass();
    public Coordinate getPosition();
    public Coordinate getVelocity();
    public Coordinate getAcceleration();
    public String getName();
    public Coordinate calcDistanceVec(BodyInterface b);
    public double calcDistance(Coordinate secondPosition);
    public Coordinate calcAcceleration(BodyInterface b);
    public AccelerationInfo calcAccelerationDetailed(BodyInterface b);
    public Coordinate calcRelativeVelocity(BodyInterface b);
    public void setAcceleration(Coordinate newA);
    public void move(double timeStep);
    public String positionString();
    public String velocityString();
    public String accelerationString();
}
