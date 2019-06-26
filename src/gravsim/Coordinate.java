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
public class Coordinate {
    double x,y;
    
    Coordinate(){
	this.x = 0.0;
	this.y = 0.0;
    }
    public Coordinate(double x, double y){
	this.x = x;
	this.y = y;
    }
    //allows a String array like "POSITION, 0.1, 0.2, blah" to be parsed to x=0.1, y=0.2
    Coordinate(String[] inputString){
	double thisValue;
	int valueIndex = 0;
	x = 0;
	y = 0;
	for(int i = 0,length = inputString.length; i < length; i++){
	   
	    try{
		thisValue = Double.parseDouble(inputString[i]);
		
		if(valueIndex == 0){
		    x = thisValue;
		    valueIndex++;
		}
		else if(valueIndex == 1 ){
		    y = thisValue;
		    valueIndex++;
		}
		else
		    break;
	    }catch(NumberFormatException e){
		//do nothing - skip text in the input array
	    }
	}
    }
    
    static Coordinate RadialCoordinate(double radius,double theta){
	Coordinate newCoordinate = new Coordinate(radius * Math.cos(theta), radius * Math.sin(theta));
	return newCoordinate;
//	return new Coordinate(radius * Math.sin(theta), radius * Math.cos(theta));
    }
    
    public void addToX(double dx){x = x+dx;}
    public void addToY(double dy){y = y+dy;}
    public void add(Coordinate dv){
	x = x + dv.x;
	y = y + dv.y;
    }
    
    public Coordinate multiply(double scale){
	return new Coordinate(x*scale,y*scale);
    }
    
    public double dot(Coordinate b){
	return this.x*b.x + this.y*b.y;
    }
    public double getAngleBetween(Coordinate b){
	double dotProduct = this.dot(b);
	double denominator = this.getMagnitude()*b.getMagnitude();
	
	if(denominator == 0)
	    return 0.0;
	    
	return Math.acos(dotProduct / denominator);
    }
    
    public double getAngle(){
	return Math.atan2(y, x);
    }
    
    public double getAngleTo(Coordinate b){
	return Math.atan2(b.y-y,b.x-x);
    }
    
    public double getMagnitude(){return Math.hypot(x,y);}
    
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    
    public Coordinate copy(){
//        System.out.printf("Coordinate.copy()\n");
        return new Coordinate(x,y);
    }
}
