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
public class Constants {
    private static double G = 6.67408*java.lang.Math.pow(10,-11) * java.lang.Math.pow(1.0/1000.0,3); //km^3 kg^-1 s^-2
    private static double gravityExponent = 2.0;
    
    private static double M = -1*java.lang.Math.pow(10,-11); //m^? kg^-1 s^-2 (negative for repulseive force)
    private static double mythiumExponent = Math.PI;
    
    private Constants(){ }
    
    public static double getG(){return G;}
    public static double getM(){return M;}
    public static double getGravityP(){return gravityExponent;}
    public static double getMythiumP(){return mythiumExponent;}
}
