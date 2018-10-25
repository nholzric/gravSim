/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gravsim;

import java.util.ArrayList;

/**
 *
 * @author Nathan
 */
public interface BodyFactory {
    public class BodySpecs{
	public String name = "UNNAMED";
	public Coordinate position = new Coordinate();
	public Coordinate velocity = new Coordinate();
	public double gravityMass = 0.0;
	public double mythiumMass = 0.0;
    }
    
    public BodyInterface CreateNewBody(BodyFactory.BodySpecs newSpecs);
    
    default void WriteBodySpec(java.io.BufferedWriter thisWriter, BodyInterface thisBody){
	try{
	    thisWriter.write(String.format("NAME\t%s\n",thisBody.getName()));
	    Coordinate thisPosition = thisBody.getPosition();
	    thisWriter.write(String.format("POSITION\t%.9f\t%.9f\n",thisPosition.x,thisPosition.y));
	    Coordinate thisVelocity = thisBody.getVelocity();
	    thisWriter.write(String.format("VELOCITY\t%.9f\t%.9f\n",thisVelocity.x,thisVelocity.y));
	    if (thisBody.getGravityMass() !=0)
		thisWriter.write(String.format("GRAVITYMASS\t%.9f\n",thisBody.getGravityMass()));
	    if (thisBody.getMythiumMass() !=0)
		thisWriter.write(String.format("MYTHIUMMASS\t%.9f\n",thisBody.getMythiumMass()));
	}
	catch(java.io.IOException e){
	    System.out.println("Error writing file.");
	}
    }
    
    default ArrayList<BodyInterface> LoadBodies(java.io.BufferedReader inFile){
	ArrayList<BodyInterface> bodyList = new ArrayList<BodyInterface>();
	
	String line = null;
	BodySpecs thisBody = null;
	try{
	    do{
		line = inFile.readLine();
		if(line == null || line.isEmpty())
		    continue;
		String[] parsedLine = line.split("\t");
		
		if(parsedLine[0].equals("NAME"))
		{
		    if(thisBody != null){
			bodyList.add(CreateNewBody(thisBody));
		    }
		    if(parsedLine.length<2)
			throw new java.io.IOException("Incomplete NAME line");
		    thisBody = new BodySpecs();
		    thisBody.name = parsedLine[1];
		}
		else{
		    if(thisBody == null)
			throw new java.io.IOException("UnNAMEd body spec");
		    switch(parsedLine[0]){
			case "POSITION":
			    if(parsedLine.length<3)
				throw new java.io.IOException("Incomplete POSITION line");
			    thisBody.position = new Coordinate(parsedLine);
			case "VELOCITY":
			    if(parsedLine.length<3)
				throw new java.io.IOException("Incomplete VELOCITY line");
			    thisBody.velocity = new Coordinate(parsedLine);
			case "GRAVITYMASS":
			    if(parsedLine.length<2)
				throw new java.io.IOException("Incomplete GRAVITYMASS line");
			    thisBody.gravityMass = Double.parseDouble(parsedLine[1]);
			case "MYTHIUMMASS":
			    if(parsedLine.length<2)
				throw new java.io.IOException("Incomplete MYTHIUMMASS line");
			    thisBody.mythiumMass = Double.parseDouble(parsedLine[1]);
			default:
		    }
		}
	    }while(line != null);
	    //add last body being built
	    if(thisBody != null){
		bodyList.add(CreateNewBody(thisBody));
	    }
	}
	catch(java.io.IOException e){
	    System.out.println("Error reading file.");
	}

	
	return bodyList;
    }
}
