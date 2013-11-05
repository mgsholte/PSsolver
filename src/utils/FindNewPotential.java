package utils;

import function.*;
import java.io.FileNotFoundException;

public class FindNewPotential {
	
	public static final double ELECTRON_CHARGE = 1.60217657E-19;
	public static final double EPSILON = 8.85418782E-12 ;
	
	public static void main(String[] args) throws FileNotFoundException {
		Function dOfZ = new Function(readData("SampleConstantdOfZ.txt"));//read density function test file
		Function V0 = new Function(readData("SamplePotentialWell.txt"));//read potential well test file
		int isDone = 0;
		while (isDone == 0){
			Function Psi = new Function(calculatePsi(dOfZ, V0), dOfZ.getDomain());
			Function sigmaOfZ = new Function(calculateSigma(Psi, dOfZ), dOfZ.getDomain());
			Function electricField = new Function(calculateEField(sigmaOfZ), dOfZ.getDomain());
			Function V1 = new Function(calculatePotential(electricField));
			//Then check V1 - V0 and change isDone if they're close enough
			if (V1.compare(V0) < Domain.getVanishPrec())
				isDone = 1;
			V0 = V1;
		}
	}

	private static double[] calculatePotential(Function electricField) {
		//Straight integration of the electric field yields the potential
		//need to check if this formula is right
		double[] newPotential = new double[electricField.getSize()];
		for (int z = 0; z < electricField.getSize(); z++)
			newPotential[z] = electricField.integrate(0, z);
		return newPotential;
	}

	private static double[] calculateEField(Function sigmaOfZ) {
		// Integration of sigma, according to a modified Gauss's law
		double[] eField = new double[sigmaOfZ.getSize()];
		for (int z = 0; z < sigmaOfZ.getSize(); z++){
			for (int zPrime = 0; zPrime < sigmaOfZ.getSize(); z++)
				eField[z] += sigmaOfZ.getVals()[zPrime]*sign(z, zPrime)/(2.0*EPSILON);
		}
		return eField;
	}
	
	private static double sign(int z, int zPrime){
		if (z >= zPrime)
			return 1.0;
		else
			return -1.0;
	}

	private static double[] calculateSigma(Function Psi, Function dOfZ) {
		//Take dofz and Psi and do a stepwise process to fill in sigma
		double nTotal = dOfZ.integrate();
		double[] sigmaVals = new double[dOfZ.getSize()];
		for (int z = 0; z < dOfZ.getSize(); z++)
			sigmaVals[z] = ELECTRON_CHARGE*(nTotal*Psi.getVals()[z]*Psi.getVals()[z] - dOfZ.getVals()[z])*dOfZ.getH();
		return sigmaVals;
	}
	
    public static double[] calculatePsi(Function dOfZ, Function V0){
        int size = dOfZ.getSize();
        double H2 = dOfZ.getH()*dOfZ.getH();
        double delE = Domain.getVanishPrec()*100;
    	double[] testPsi1 = new double[size];
        double[] testPsi2 = new double[size];
        double[] testPsi3 = new double[size];
        testPsi1[0] = 0;/**THESE INITIAL CONDITIONS NEED TO BE ALTERED*/
        testPsi1[1] = .01;
        testPsi2[0] = 0;
        testPsi2[1] = .01;
        testPsi3[0] = 0;
        testPsi3[1] = .01;
        double E1 = 0;
        double E2 = E1 + delE;
        double E3 = 0;
        
        while (Math.abs(E1 - E2) > Domain.getVanishPrec()){//test if energy is guessed within a specified error bound
            for (int i = 1; i < size - 1; i++){//calculate psi for two energy guesses
                testPsi1[i + 1] =  ((2.0 - 10*H2*(-(E1 + V0.getVals()[i]))/12)*testPsi1[i] - (1 + H2*(-(E1 + V0.getVals()[i - 1]))/12)*testPsi1[i - 1])/(1 + H2*(-(E1 + V0.getVals()[i + 1]))/12);
                testPsi2[i + 1] =  ((2.0 - 10*H2*(-(E2 + V0.getVals()[i]))/12)*testPsi2[i] - (1 + H2*(-(E2 + V0.getVals()[i - 1]))/12)*testPsi2[i - 1])/(1 + H2*(-(E2 + V0.getVals()[i + 1]))/12);
            }
            if (testPsi1[size - 1]*testPsi2[size - 1] > 0)
                E2 += delE;//increment energy guess by 1 if psi hasn't crossed the x axis at the boundary(will be updated to be inf)
            else{
                E3 = (E1 + E2)/2;//get a more accurate energy eigenvalue
                for (int i = 1; i < size - 1; i++){
                    testPsi3[i + 1] =  ((2.0 - 10*H2*(-(E3 + V0.getVals()[i]))/12)*testPsi3[i] - (1 + H2*(-(E3 + V0.getVals()[i - 1]))/12)*testPsi3[i - 1])/(1 + H2*(-(E3 + V0.getVals()[i + 1]))/12);
                }
                if (testPsi3[size - 1]*testPsi2[size - 1] < 0)//picks new energy guesses based on end behavior
                    E1 = E3;
                else
                    E2 = E3;
            }
        }
        return testPsi1;
    }

	// This is going to read a data file giving some function.
	private static double[] readData(String filename) throws FileNotFoundException{
		double[] data = null;
		//HAVING SOME ISSUES HERE
		return data;
	}

}
