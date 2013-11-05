package utils;

import java.awt.Graphics;

/**(EDIT 11/3/13:  This class doesn't do anything, this was just the first run at using the shooting method.
 * Now calculatePsi is a method called within the FindNewPotential class)
 * 
 * 
 * This class will (eventually) take an input of a potential function and some situational parameters and calculate 
 * energies along with their associated wavefunctions
 * 
 * @author Matthew Butcher 
 * @version Version 1.0, Started 10-2-13
 */
public class CalculatePsi
{   //all of the constants named here are arbitrary, and can be changed later to match the actual problem
    public static final double H = .01;
    public static final double H2 = H*H;
    public static final double DEL_E = .01;
    public static final double V_MIN = -50;
    public static final double WIDTH = 1;
    public static final int INDEX = (int) (WIDTH/H);
    public static final double EPSILON = .001;
    /**Use the shooting method to guess the energy, and numerov method to integrate psi.
     * guess a minimum E (E1) and increment it by DEL_E to get second guess E2.  Integrate 
     * psi with E1 and E2 and check if psie1[end]*psie2[end] < 0. If not, increment
     * E2 again and repeat until the two guesses give opposite signed boundaries (we want psi = 0
     * at the boundaries).  Then find E3 = (E1 + E2)/2 and repeat until Eavg gives boundary within
     * a good error bound.  This will give a psi with associated eigenvalue.
     */
    
    public static void main(){
        double[] testPsi1 = new double[INDEX];
        double[] testPsi2 = new double[INDEX];
        double[] testPsi3 = new double[INDEX];
        testPsi1[0] = 0;
        testPsi1[1] = .01;
        testPsi2[0] = 0;
        testPsi2[1] = .01;
        testPsi3[0] = 0;
        testPsi3[1] = .01;
        double E1 = 0;
        double E2 = E1 + DEL_E;
        double E3 = 0;
        
        while (Math.abs(E1 - E2) > EPSILON){//test if energy is guessed within a specified error bound
            for (int i = 1; i < INDEX - 1; i++){//calculate psi for two energy guesses
                testPsi1[i + 1] =  ((2.0 - 10*H2*(-(E1 + V_MIN))/12)*testPsi1[i] - (1 + H2*(-(E1 + V_MIN))/12)*testPsi1[i - 1])/(1 + H2*(-(E1 + V_MIN))/12);
                testPsi2[i + 1] =  ((2.0 - 10*H2*(-(E2 + V_MIN))/12)*testPsi2[i] - (1 + H2*(-(E2 + V_MIN))/12)*testPsi2[i - 1])/(1 + H2*(-(E2 + V_MIN))/12);
            }
            if (testPsi1[INDEX - 1]*testPsi2[INDEX - 1] > 0)
                E2 += DEL_E;//increment energy guess by 1 if psi hasn't crossed the x axis at the boundary(will be updated to be inf)
            else{
                E3 = (E1 + E2)/2;//get a more accurate energy eigenvalue
                for (int i = 1; i < INDEX - 1; i++){
                    testPsi3[i + 1] =  ((2.0 - 10*H2*(-(E3 + V_MIN))/12)*testPsi3[i] - (1 + H2*(-(E3 + V_MIN))/12)*testPsi3[i - 1])/(1 + H2*(-(E3 + V_MIN))/12);
                }
                if (testPsi3[INDEX - 1]*testPsi2[INDEX - 1] < 0)//picks new energy guesses based on end behavior
                    E1 = E3;
                else
                    E2 = E3;
            }
        }
        
        
        //these are just some outputs I was using to check if the behavior was what I expected
        System.out.println(E1 + " " + E2 + " " + E3);
        for (int i = 0; i < INDEX; i++){
            System.out.print(testPsi1[i] + " ");
        }
        
        DrawingPanel testGraph = new DrawingPanel((int)(200*WIDTH), 200);
        Graphics graphPsi = testGraph.getGraphics();
        graphPsi.drawLine(0,100,(int)(200*WIDTH),100);
        graphPsi.drawLine((int)(100*WIDTH),0,(int)(100*WIDTH),200);
        for (int i = 0; i < INDEX; i++){
            graphPsi.fillOval((int) (i*200*WIDTH/INDEX), (int) (100 - testPsi1[i]*100), 2, 2);
        }
    }

    
    
    
}