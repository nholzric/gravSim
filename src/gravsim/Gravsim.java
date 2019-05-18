/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gravsim;

import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 *
 * @author Nathan
 */
public class Gravsim {
    
    double timeStep;
    double maxAllowedAngularRate = 10.0*Math.PI/180.0; //5 deg/sec
    double minTimeStep = 10; //seconds
    double currentTime;
    ArrayList<BodyInterface> bodyList;
    BodyFactory myBodyFactory;
    BodyInterface centralBody;
    Random myRandom;
    
    java.io.BufferedWriter myLogWriter;
    
    public Gravsim(BodyFactory myBodyFactory,double centralBodyMass,long randomSeed){
	this.timeStep = 0.0;
	this.currentTime = 0;
	this.bodyList = new ArrayList<BodyInterface>();
	this.myBodyFactory = myBodyFactory;
	Body concreteBody = new Body("Central",new Coordinate(),new Coordinate());
	this.centralBody = new GravityBody(concreteBody,centralBodyMass);
	this.myRandom = new Random(randomSeed);
	
	try{
	    myLogWriter = new java.io.BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(new java.io.File(".\\timestepLog.txt"))));
	}catch(java.io.IOException e){
	    System.out.println("Error writing file.");
	}
    }
    
    public void closeFiles(){
	try{
	    myLogWriter.close();
	}
	catch(IOException e){
	    
	}
    }
    
    public double doTimestep(double maxTimeStep){
	Coordinate centralBodyDistance;
	ArrayList<Coordinate> bodyDistances = new ArrayList<Coordinate>();
	bodyDistances.ensureCapacity(bodyList.size());
	
	//calculate acceleration of every object except central body
	double maxAngularRate = Double.NEGATIVE_INFINITY;
	String maxARNameA="", maxARNameB="";
	Coordinate maxARDistance = new Coordinate();
	for(int i = 0,numObjects = bodyList.size(); i<numObjects; i++){
	    BodyInterface.AccelerationInfo totalAcceleration = bodyList.get(i).calcAccelerationDetailed(centralBody);
	    centralBodyDistance = totalAcceleration.getDistance();
	    for(int j = 0;j < numObjects; j++){
		if(i!=j){ //don't calculate acceleration of self-interaction
		    BodyInterface.AccelerationInfo thisAcceleration = bodyList.get(i).calcAccelerationDetailed(bodyList.get(j));
		    bodyDistances.add(j,thisAcceleration.getDistance());
		    totalAcceleration.addA(thisAcceleration.getA());
		}
		else
		    bodyDistances.add(j,new Coordinate());
	    }
	    
	    bodyList.get(i).setAcceleration(totalAcceleration.getA());
	    
	    //calculate max angular rate
	    double thisAngularRate = computeAngularRate(bodyList.get(i),centralBody,centralBodyDistance);
	    if(thisAngularRate > maxAngularRate){
		maxAngularRate = thisAngularRate;
		maxARNameA = bodyList.get(i).getName();
		maxARNameB = centralBody.getName();
		maxARDistance = centralBodyDistance;
	    }
	    for(int j = 0; j < numObjects; j++){
		if(i!=j){
		    thisAngularRate = computeAngularRate(bodyList.get(i),bodyList.get(j),bodyDistances.get(j));
		    if(thisAngularRate > maxAngularRate){
			maxAngularRate = thisAngularRate;
			maxARNameA = bodyList.get(i).getName();
			maxARNameB = bodyList.get(j).getName();
			maxARDistance = bodyDistances.get(j);
		    }
		}
	    }
	    bodyDistances.clear(); //reset list for next timesetp
	}
	
	//scale calculated time step to produce maxAngularRate
	double calculatedTimeStep = this.maxAllowedAngularRate / maxAngularRate;
	double thisTimeStep = maxTimeStep;
	//choose smallest of time steps
	try{
	    myLogWriter.write(String.format("%f\t%s\t%s\t%f\t%f\n",maxAngularRate,
		maxARNameA,maxARNameB,
		maxARDistance.x,maxARDistance.y));
	}catch(java.io.IOException e){
	    System.out.println("Error writing line.");
	}
//	if(calculatedTimeStep < maxTimeStep)
//	    thisTimeStep = calculatedTimeStep;
//	else
//	    thisTimeStep = maxTimeStep;
//	if(thisTimeStep < minTimeStep)
//	    thisTimeStep = minTimeStep;
	
	//let each body move to it's new velocity and position based on calculated acceleration
	for(int i = 0,numObjects = bodyList.size(); i<numObjects; i++){
	    bodyList.get(i).move(thisTimeStep);
	}
//	bodyList.stream().forEach((b) -> {
//	    b.move(thisTimeStep);
//	});
	
	return thisTimeStep;
    }
    
    private double computeAngularRate(BodyInterface A, BodyInterface B, Coordinate distanceAB){
	    Coordinate relativeVelocity = A.calcRelativeVelocity(B);
	    relativeVelocity.add(distanceAB); //this is now the distance 1 second later
	    double thisAngularRate = distanceAB.getAngleBetween(relativeVelocity);
	    
	    return thisAngularRate;
    }
    
    public ArrayList<String> getPositionStrings(){
	ArrayList<String> positionList = new ArrayList<String>();
	bodyList.stream().forEach((b) ->{
	    positionList.add(String.format("%s\t%s\t%s\t%s",b.getName(),b.positionString(),b.velocityString(),b.accelerationString()));
	});
	
	return positionList;
    }
    
    public ArrayList<String> getDistanceStrings(){
	ArrayList<String> distanceList = new ArrayList<String>();
	for(int i = 0, numBodies = bodyList.size(); i < numBodies; i++){
	    for(int j = 0; j < numBodies; j++){
		if(i == j)
		    continue;
		distanceList.add(String.format("%s\t%s\t%s",
			bodyList.get(i).getName(),
			bodyList.get(j).getName(),
			bodyList.get(i).calcDistance(bodyList.get(j).getPosition())));
	    }
	}
	return distanceList;
    }
    
    public ArrayList<State> getSimState(){
        ArrayList<State> simState = new ArrayList<State>();
        bodyList.stream().forEach((b) -> {
            simState.add(new State(b.getName(),b.getPosition(),b.getVelocity())); 
        });
        
        return simState;
    }
    
    //min-max angle in radians
    public void addNewRandomBody(double minMass, double maxMass,
	    double minMythium, double maxMythium,
	    double minAltitude, double maxAltitude,
	    double minAngle, double maxAngle){
	
	String name = String.format("%d",bodyList.size());
	double gravityMass = minMass + (maxMass - minMass) * myRandom.nextDouble();
	double mythiumMass = minMass + (maxMass - minMass) * myRandom.nextDouble();
	double altitude = minAltitude + (maxAltitude - minAltitude) * myRandom.nextDouble();
	double angle = minAngle + (maxAngle - minAngle) * myRandom.nextDouble();
	
	addNewBody(name,Coordinate.RadialCoordinate(altitude,angle),gravityMass,mythiumMass);
    }
    
    public void addNewBody(String name,Coordinate startingPosition,double gravityMass,double mythiumMass){
	Coordinate startingVelocity = getCircularVelocity(startingPosition);
	addNewBody(name,startingPosition,startingVelocity,gravityMass,mythiumMass);
    }
    public void addNewBody(String name,Coordinate startingPosition,Coordinate startingVelocity,double gravityMass,double mythiumMass){
	BodyFactory.BodySpecs theseSpecs = new BodyFactory.BodySpecs();
	theseSpecs.name = name;
	theseSpecs.position = startingPosition;
	theseSpecs.velocity = startingVelocity;
	theseSpecs.gravityMass = gravityMass;
	theseSpecs.mythiumMass = mythiumMass;
	BodyInterface newBody = myBodyFactory.CreateNewBody(theseSpecs);
	boolean add = bodyList.add(newBody);
    }
    
    public ArrayList<BodyInterface> getBodyList(){
        return bodyList;
    }
    
    private Coordinate getCircularVelocity(Coordinate position){
	double velocityMagnitude = Math.sqrt(Constants.getG() * centralBody.getGravityMass()/centralBody.calcDistance(position));
	
	//assume counter-clockwise rotation (+PI/2)
	return Coordinate.RadialCoordinate(velocityMagnitude, position.getAngle()+Math.PI/2);
    }
    
    public void exportPopulation(String filePath){
	try{
	    java.io.BufferedWriter scenarioWriter = new java.io.BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(new java.io.File(filePath))));

	    for(int i = 0, numObjects = bodyList.size(); i < numObjects; i++){
		myBodyFactory.WriteBodySpec(scenarioWriter, bodyList.get(i));
	    }
	    scenarioWriter.close();
	}
	catch(java.io.IOException e){
	    System.out.println("Error writing population file");
	}
	
    }
    public void importPopulation(String filePath){
	try
	{
	    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(filePath));
	    bodyList = myBodyFactory.LoadBodies(reader);
	}
	    catch (java.io.FileNotFoundException e){
	    System.out.println("Error reading population file");
	}
    }
    
    public static class simParameters{
	public double centralMass = 1.898*Math.pow(10,27); //Jupiter
	public double startTime = 0;
	public double timeStep = 10*3.551*24*60*60/10000; //10 revs of Europa in 1000 steps
	public double endTime = 10*3.551*24*60*60; //10 revs of Europa's orbit in seconds
	public boolean useMythium = false;
	public randomPopulationParameters myRandPopulationSpecs;
    }
    public static class randomPopulationParameters{
	public double baselineRadius = 670900.0; //km
	public double rangeRadius = 0.01*baselineRadius;
	//https://en.wikipedia.org/wiki/List_of_U.S._states_and_territories_by_area
	//https://nssdc.gsfc.nasa.gov/planetary/factsheet/asteroidfact.html
	public double baselineMass = 1500 * Math.pow(10,15);//mass of the asteroid Siwa which has a surface area of roughly 33E3 km^2 or roughly the surface area of South Carolina
	public double rangeMass = 0.1*baselineMass;
	public double baselineMythium = 0.0;
	public double rangeMythium = 0;
	public double baselineAngle = 0.005*Math.PI;
	double rangeAngle = 0.005*Math.PI;
	public int numObjects = 50;
	public long randomSeed = 0;
    }
    
    public static simParameters loadSimFile(String simFilePath){
	simParameters theseParameters = new simParameters();
	theseParameters.myRandPopulationSpecs = new randomPopulationParameters();
	
	try{
	    java.io.BufferedReader inFile = new java.io.BufferedReader(new java.io.FileReader(simFilePath));
	    String line;
	    System.out.printf("Loading Simulation File %s\n",simFilePath);
	    do{
		line = inFile.readLine();
		if(line == null || line.isEmpty())
		    continue;
		String[] parsedLine = line.split("\t");
		switch(parsedLine[0]){
		    case "CENTRALMASS":
			if(parsedLine.length < 2)
			    throw new java.io.IOException("Incomplete BASELINERADIUS line");
			theseParameters.centralMass = Double.parseDouble(parsedLine[1]);
			System.out.printf("\tCENTRALMASS = %e\n",theseParameters.centralMass);
			break;
		    case "STARTTIME":
			if(parsedLine.length < 2)
			    throw new java.io.IOException("Incomplete STARTTIME line");
			theseParameters.startTime = Double.parseDouble(parsedLine[1]);
			System.out.printf("\tSTARTTIME = %f\n",theseParameters.startTime);
			break;
		    case "TIMESTEP":
			if(parsedLine.length < 2)
			    throw new java.io.IOException("Incomplete TIMESTEP line");
			theseParameters.timeStep = Double.parseDouble(parsedLine[1]);
			System.out.printf("\tTIMESTEP = %f\n",theseParameters.timeStep);
			break;
		    case "ENDTIME":
			if(parsedLine.length < 2)
			    throw new java.io.IOException("Incomplete ENDTIME line");
			theseParameters.endTime = Double.parseDouble(parsedLine[1]);
			System.out.printf("\tENDTIME = %f\n",theseParameters.endTime);
			break;
		    case "USEMYTHIUM":
			if(parsedLine.length < 2)
			    throw new java.io.IOException("Incomplete USEMYTHIUM line");
			if(Integer.parseInt(parsedLine[1]) == 1){
			    theseParameters.useMythium = true;
			    System.out.printf("\tUsing Mythium\n");
			}
			break;
		    case "BASELINERADIUS":
			if(parsedLine.length < 2)
			    throw new java.io.IOException("Incomplete BASELINERADIUS line");
			theseParameters.myRandPopulationSpecs.baselineRadius = Double.parseDouble(parsedLine[1]);
			System.out.printf("\tBASELINERADIUS = %e\n",theseParameters.myRandPopulationSpecs.baselineRadius);
			break;
		    case "RANGERADIUS":
			if(parsedLine.length < 2)
			    throw new java.io.IOException("Incomplete RANGERADIUS line");
			theseParameters.myRandPopulationSpecs.rangeRadius = Double.parseDouble(parsedLine[1]);
			System.out.printf("\tRANGERADIUS = %e\n",theseParameters.myRandPopulationSpecs.rangeRadius);
			break;
		    case "BASELINEMASS":
			if(parsedLine.length < 2)
			    throw new java.io.IOException("Incomplete BASELINEMASS line");
			theseParameters.myRandPopulationSpecs.baselineMass = Double.parseDouble(parsedLine[1]);
			System.out.printf("\tBASELINEMASS = %e\n",theseParameters.myRandPopulationSpecs.baselineMass);
			break;
		    case "RANGEMASS":
			if(parsedLine.length < 2)
			    throw new java.io.IOException("Incomplete RANGEMASS line");
			theseParameters.myRandPopulationSpecs.rangeMass = Double.parseDouble(parsedLine[1]);
			System.out.printf("\tRANGEMASS = %e\n",theseParameters.myRandPopulationSpecs.rangeMass);
			break;
		    case "BASELINEMYTHIUM":
			if(parsedLine.length < 2)
			    throw new java.io.IOException("Incomplete BASELINEMYTHIUM line");
			theseParameters.myRandPopulationSpecs.baselineMythium = Double.parseDouble(parsedLine[1]);
			System.out.printf("\tBASELINEMYTHIUM = %e\n",theseParameters.myRandPopulationSpecs.baselineMythium);
			break;
		    case "RANGEMYTHIUM":
			if(parsedLine.length < 2)
			    throw new java.io.IOException("Incomplete RANGEMYTHIUM line");
			theseParameters.myRandPopulationSpecs.rangeMythium = Double.parseDouble(parsedLine[1]);
			System.out.printf("\tRANGEMYTHIUM = %e\n",theseParameters.myRandPopulationSpecs.rangeMythium);
			break;
		    case "BASELINEANGLE":
			if(parsedLine.length < 2)
			    throw new java.io.IOException("Incomplete BASELINEANGLE line");
			theseParameters.myRandPopulationSpecs.baselineAngle = Double.parseDouble(parsedLine[1]);
			System.out.printf("\tBASELINEANGLE = %f\n",theseParameters.myRandPopulationSpecs.baselineAngle);
			break;
		    case "RANGEANGLE":
			if(parsedLine.length < 2)
			    throw new java.io.IOException("Incomplete RANGEANGLE line");
			theseParameters.myRandPopulationSpecs.rangeAngle = Double.parseDouble(parsedLine[1]);
			System.out.printf("\tRANGEANGLE = %f\n",theseParameters.myRandPopulationSpecs.rangeAngle);
			break;
		    case "NUMOBJECTS":
			if(parsedLine.length < 2)
			    throw new java.io.IOException("Incomplete NUMOBJECTS line");
			theseParameters.myRandPopulationSpecs.numObjects = Integer.parseInt(parsedLine[1]);
			System.out.printf("\tNUMOBJECTS = %d\n",theseParameters.myRandPopulationSpecs.numObjects);
			break;
		    case "RANDOMSEED":
			if(parsedLine.length < 2)
			    throw new java.io.IOException("Incomplete RANDOMSEED line");
			theseParameters.myRandPopulationSpecs.randomSeed = Long.parseLong(parsedLine[1]);
			System.out.printf("\tRANDOMSEED = %d\n",theseParameters.myRandPopulationSpecs.randomSeed);
			break;
		    default:
			System.out.printf("Unknown Simfile Parameter: %\n", parsedLine[0]);
		}
	    }while(line != null);
	}
	catch(java.io.IOException e){
	    System.out.println("Error reading file.");
	}
	
	return theseParameters;
    }
    public static void exportSimFile(String simFilePath,simParameters theseParameters){
        try{
            java.io.BufferedWriter simulationWriter = new java.io.BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(new java.io.File(simFilePath))));
            
            simulationWriter.write(String.format("CENTRALMASS\t%G\tkg\n",theseParameters.centralMass));
            simulationWriter.write(String.format("STARTTIME\t%f\tseconds\n",theseParameters.startTime));
            simulationWriter.write(String.format("TIMESTEP\t%f\tseconds\n",theseParameters.timeStep));
            simulationWriter.write(String.format("ENDTIME\t%f\tseconds\n",theseParameters.endTime));
            simulationWriter.write(String.format("USEMYTHIUM\t%d\t1/0\n\n",theseParameters.useMythium ? 1:0));
            simulationWriter.write(String.format("BASELINERADIUS\t%.1f\tkm\n",theseParameters.myRandPopulationSpecs.baselineRadius));
            simulationWriter.write(String.format("RANGERADIUS\t%.1f\tkm\n",theseParameters.myRandPopulationSpecs.rangeRadius));
            simulationWriter.write(String.format("BASELINEMASS\t%G\tkg\n",theseParameters.myRandPopulationSpecs.baselineMass));
            simulationWriter.write(String.format("RANGEMASS\t%G\tkg\n",theseParameters.myRandPopulationSpecs.rangeMass));
            simulationWriter.write(String.format("BASELINEMYTHIUM\t%G\n",theseParameters.myRandPopulationSpecs.baselineMythium));
            simulationWriter.write(String.format("RANGEMYTHIUM\t%G\n",theseParameters.myRandPopulationSpecs.rangeMythium));
//            simulationWriter.write(String.format("BASELINEANGLE\t%.9f\n",theseParameters.myRandPopulationSpecs.baselineAngle));
//            simulationWriter.write(String.format("RANGEANGLE\t%.9f\n",theseParameters.myRandPopulationSpecs.rangeAngle));
            simulationWriter.write(String.format("NUMOBJECTS\t%d\n\n",theseParameters.myRandPopulationSpecs.numObjects));
            simulationWriter.write(String.format("RANDOMSEED\t%d\n",theseParameters.myRandPopulationSpecs.randomSeed));
            
            simulationWriter.close();
        }
        catch(java.io.IOException e){
            System.out.println("Error writing simulation file");
        }
    }
    
    public static void main(String[] args) {
	if(args.length < 2 || 4 < args.length){
	    System.out.printf("Useaged:\n\tGravsim simfile.txt logfile.txt\n\n");
	    System.out.printf("\t-P populationfile.txt\texport population file\n");
	    System.out.printf("\t-p populationfile.txt\timport population file\n");
	    return;
	}
	String simFilePath = args[0];
	String logFilePath = args[1]; //	String logFilePath = ".\\simLog.txt";
	String populationExportFilePath = null; //String scenarioFilePath = ".\\gravScenario.txt";
	String populationImportFilePath = null;
	int argsIndex = 2;
	while(argsIndex < args.length){
	    switch(args[argsIndex]){
		case "-P":
		    if(argsIndex+1 >= args.length){
			System.out.printf("Missing population export file path\n");
			break;
		    }
		    populationExportFilePath = args[argsIndex+1];
		    argsIndex++;
		    break;
		case "-p":
		    if(argsIndex+1 >= args.length){
			System.out.printf("Missing population import file path\n");
			break;
		    }
		populationImportFilePath = args[argsIndex+1];
		    argsIndex++;
		    break;
	    }
	    argsIndex++;
	}
//	String distanceFilePath = ".\\distanceLog.txt";
	simParameters mySimParameters = loadSimFile(simFilePath);
	//double JupiterMass = 1.898*Math.pow(10,27);
	
	BodyFactory thisBodyFactory;
	if(mySimParameters.useMythium)
	    thisBodyFactory = new MythiumBodyConcreteFactory();
	else
	    thisBodyFactory = new GravityBodyConcreteFactory();
	
	Gravsim mySim = new Gravsim(thisBodyFactory,
		mySimParameters.centralMass,
		mySimParameters.myRandPopulationSpecs.randomSeed);
	
//	//add four Europa's at even intervals
//	{
//	    double europasRadius = 670900.0; //km
//	    double europasMass = 4.799844 * Math.pow(10,22); //kg
//	    double mythiumMass = 0.0;
////	    Coordinate newCoordinate = Coordinate.RadialCoordinate(europasRadius,0.0*Math.PI);
//	    mySim.addNewBody("A",Coordinate.RadialCoordinate(europasRadius,0.0*Math.PI), europasMass, mythiumMass);
//	    mySim.addNewBody("B",Coordinate.RadialCoordinate(europasRadius,0.5*Math.PI), europasMass, mythiumMass);
//	    mySim.addNewBody("C",Coordinate.RadialCoordinate(europasRadius,1.0*Math.PI), europasMass, mythiumMass);
//	    mySim.addNewBody("D",Coordinate.RadialCoordinate(europasRadius,1.5*Math.PI), europasMass, mythiumMass);
//	}
//	{
//	    double baselineRadius = 670900.0; //km
//	    double rangeRadius = 0.01*baselineRadius;
//	    //https://en.wikipedia.org/wiki/List_of_U.S._states_and_territories_by_area
//	    //https://nssdc.gsfc.nasa.gov/planetary/factsheet/asteroidfact.html
//	    double baselineMass = 1500 * Math.pow(10,15);//mass of the asteroid Siwa which has a surface area of roughly 33E3 km^2 or roughly the surface area of South Carolina
//	    double rangeMass = 0.1*baselineMass;
//	    double baselineMythium = 0.0;
//	    double rangeMythium = 0;
//	    
//	    int numObjects = 50;
//	    for(int i = 0; i < numObjects; i++){
//		mySim.addNewRandomBody(baselineMass-rangeMass,baselineMass+rangeMass,
//			baselineMythium-rangeMythium,baselineMythium+rangeMythium,
//			baselineRadius-rangeRadius,baselineRadius+rangeRadius,
//			0,0.01*Math.PI);
//	    }
//	}
	if(populationImportFilePath != null){
	    System.out.printf("Loading Population File %s\n", populationImportFilePath);
	    mySim.importPopulation(populationImportFilePath);
	    System.out.printf("\t%d Objects Loaded\n",mySim.bodyList.size());
	}
	else{
	    System.out.printf("Randomly Generating %d Objects\n",mySimParameters.myRandPopulationSpecs.numObjects);
	    //randomly generate population
	    for(int i = 0; i < mySimParameters.myRandPopulationSpecs.numObjects; i++){
		mySim.addNewRandomBody(
			mySimParameters.myRandPopulationSpecs.baselineMass-mySimParameters.myRandPopulationSpecs.rangeMass,mySimParameters.myRandPopulationSpecs.baselineMass+mySimParameters.myRandPopulationSpecs.rangeMass,
			mySimParameters.myRandPopulationSpecs.baselineMythium-mySimParameters.myRandPopulationSpecs.rangeMythium,mySimParameters.myRandPopulationSpecs.baselineMythium+mySimParameters.myRandPopulationSpecs.rangeMythium,
			mySimParameters.myRandPopulationSpecs.baselineRadius-mySimParameters.myRandPopulationSpecs.rangeRadius,mySimParameters.myRandPopulationSpecs.baselineRadius+mySimParameters.myRandPopulationSpecs.rangeRadius,
			mySimParameters.myRandPopulationSpecs.baselineAngle-mySimParameters.myRandPopulationSpecs.rangeAngle,mySimParameters.myRandPopulationSpecs.baselineAngle+mySimParameters.myRandPopulationSpecs.rangeAngle);
	    }
	}
	
	if(populationExportFilePath != null)
	    mySim.exportPopulation(populationExportFilePath);
	
	try{
	    java.io.BufferedWriter myLogWriter = new java.io.BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(new java.io.File(logFilePath))));
//	    java.io.BufferedWriter myDistanceLogWriter = new java.io.BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(new java.io.File(distanceFilePath))));
	    
	    myLogWriter.write("Time\tMoon\tX\tY\tdX\tdY\tddX\tddY\n");
//	    myDistanceLogWriter.write("Time\tA\tB\tDistance\n");
	    
	    mySim.getPositionStrings().stream().forEach((s)-> {
		try{
		    myLogWriter.write(String.format("0.0\t%s\n",s));
		}catch(java.io.IOException e){
		    System.out.println("Error writing line.");
		}
	    });
//	    mySim.getDistanceStrings().stream().forEach((s)-> {
//		try{
//		    myDistanceLogWriter.write(String.format("0.0\t%s\n",s));
//		}catch(java.io.IOException e){
//		    System.out.println("Error writing line.");
//		}
//	    });


//	    int numSteps = 10000;
//	    int numRevs = 10;
//	    double europasPeriod = 3.551*24*60*60; //days to seconds
//	    double suggestedTimeStep = 10*europasPeriod/numSteps;
////	    for(int step = 0; step < numSteps; step++){
//	    double maxSimTime = numRevs*europasPeriod;
//	    double thisTime = 0.0;
	    double thisTime = mySimParameters.startTime;
	    double suggestedTimeStep = mySimParameters.timeStep;
	    double maxSimTime = mySimParameters.endTime;
	    int step = 0;
	    while(thisTime < maxSimTime){
		if(Math.floorMod(step,100) == 0)
		    System.out.printf("Step %d (%.2f%%)\n",step,100*thisTime/maxSimTime);
		
		double thisTimeStep = mySim.doTimestep(suggestedTimeStep);
		thisTime = thisTime + thisTimeStep;
		
		if(thisTimeStep != suggestedTimeStep && Math.floorMod(step,50)==0)
		    System.out.printf("\tUsing time step %.2f compared to %.2f\n",thisTimeStep,suggestedTimeStep);
		
		++step;
		
		Iterator<String> it = mySim.getPositionStrings().iterator();
		while(it.hasNext()){
		    myLogWriter.write(String.format("%f\t%s\n",thisTime,it.next()));
		}
//		Iterator<String> it2 = mySim.getDistanceStrings().iterator();
//		while(it2.hasNext()){
//		    myDistanceLogWriter.write(String.format("%f\t%s\n",step*timeStep,it2.next()));
//		}
		
//		mySim.getPositionStrings().stream().forEach((s)-> {
//		    try{
//			myWriter.write(String.format("%s\n",s));
//		    }catch(java.io.IOException e){
//			System.out.println("Error writing line.");
//		    }
//		});
	    }
	    
	    myLogWriter.close();
	}
	catch(java.io.IOException e){
	    System.out.println("Error writing file.");
	}
	
	mySim.closeFiles();
    }
    
}
