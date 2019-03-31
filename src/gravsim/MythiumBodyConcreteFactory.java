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
public class MythiumBodyConcreteFactory implements BodyFactory{
    
    public MythiumBodyConcreteFactory(){}
    
    public BodyInterface CreateNewBody(BodyFactory.BodySpecs newSpecs){
	BodyInterface newBody = new Body(newSpecs.name, newSpecs.position, newSpecs.velocity);
	BodyInterface newGravityBody = new GravityBody(newBody,newSpecs.gravityMass);
	BodyInterface newMythiumBody = new MythiumBody(newGravityBody,newSpecs.mythiumMass);
	return newMythiumBody;
    }
    
}
