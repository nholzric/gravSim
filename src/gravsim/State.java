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
public class State {
    private Coordinate p,v,a;
    private String name = null;
    
    State(Coordinate p){
        this.p = p;
        
        this.v = null;
        this.a = null;
    }
    State(Coordinate p, Coordinate v){
        this.p = p;
        this.v = v;
        
        this.a = null;
    }
    State(Coordinate p, Coordinate v, Coordinate a){
        this.p = p;
        this.v = v;
        this.a = a;
    }
    State(String name, Coordinate p){
        this.name = name;
        
        this.p = p;
        
        this.v = null;
        this.a = null;
    }
    State(String name, Coordinate p, Coordinate v){
        this.name = name;
        this.p = p;
        this.v = v;
        
        this.a = null;
    }
    State(String name, Coordinate p, Coordinate v, Coordinate a){
        this.name = name;
        this.p = p;
        this.v = v;
        this.a = a;
    }
    
    public Coordinate getP(){
        return this.p;
    }
    public Coordinate getV(){
        return this.v;
    }
    public Coordinate getA(){
        return this.a;
    }
    public String getName(){
        return this.name;
    }
    
    public State copy(){
        return new State(name,p.copy(),v.copy(),a.copy());
    }
}
