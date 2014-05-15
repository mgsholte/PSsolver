package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import main.Main;

public class WellParameters {
	
	public static final String defaultParamsFileName = "./well_params_default.kvp";

	private Properties params;

	public int numLayers;
	
	private double
		dx, errTol, Lx, Ly, Lz, dOfZ;//dOfZ in A^-3 ~ 2e-6
	
	private double[]
		widths, bandGaps, dielecs, effMasses;//effMasses need to be relative to rest mass
	
	private Domain domain;
	
	public static final WellParameters genDummyParams(Domain d) {
		return new WellParameters(d);
	}
	
	/**
	 * For creating fake param objects for easier testing
	 */
	private WellParameters(Domain d) {
		//TODO: for testing only
		double totWidth = d.getUB() - d.getLB();
		this.domain = d;
		this.numLayers = 3;
		double[] dumWidths = { totWidth/5*2, totWidth/5, totWidth/5*2};//1 layer, width is the whole domain
		double[] dumDielecs = {12.0, 10.0, 12.0};
		this.widths = dumWidths;
		this.dielecs = dumDielecs;
		this.effMasses = dumDielecs.clone();
		errTol = Main.DEFAULT_TOLERANCE;
		Lz = totWidth;
		Lx = 1e8;
		Ly = 1e8;
		dOfZ = 2e-6;
	}
	
	public WellParameters(String paramsFileName) throws ParameterReadException {
		this(paramsFileName, defaultParamsFileName);
	}
	
	//TODO: write default parameters file
	//TODO: initialize values from property list
	public WellParameters(String paramsFileName, String defaultsFileName) throws ParameterReadException {
		// load default well parameters
		Properties defaultParams = new Properties();
		FileInputStream in;
		// read in default params
		try {
			in = new FileInputStream(defaultsFileName);
			defaultParams.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			throw new ParameterReadException("Default well parameters file not found. This shouldn't happen. ", e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ParameterReadException("Execption occurred while reading the default well parameters file", e);
		}
		// read input params with defaults for unspecified params
		params = new Properties(defaultParams);
		try {
			in = new FileInputStream(paramsFileName);
			params.load(in);
		} catch (FileNotFoundException e) {
			System.err.println("Error: input well parameters file not found.");
		} catch (IOException e) {
			e.printStackTrace();
			throw new ParameterReadException("Execption occurred while reading the input well parameters file", e);
		}
		// initialize values from parameter file
		errTol = Double.parseDouble( params.getProperty("tolerance") );
	}
	
	public Function getDofZ(){
		return new LazyFunction(getProblemDomain()) {
			@Override
			public double evalAt(double x) {
				return getLayer(x, this.domain) == 1 ?
						dOfZ :
						0;
			}
		};
	}
	
	public double getLx(){
		return Lx;
	}
	
	public double getLy(){
		return Ly;
	}
	
	public double getLz(){
		return Lz;
	}
	
	public double getErrTolerance() {
		return errTol;
	}
	
	public double[] getWidths() {
		return widths;
	}

	public Domain getProblemDomain() {
		if (domain == null) {
			double range = 0;
			for(double width : widths) {
				range += width;
			}
			// make the range symmetric about the origin
			range /= 2;
			domain = new Domain(-range, range, dx);
		}
		return domain;
	}
	
	/**
	 * Find the layer in which {@code x} belongs.
	 * @param x
	 * @param domain - the domain in which {@code x} resides
	 * @return the layer number indexed from 0
	 */
	private int getLayer(double x, Domain domain) {
		// get left-most point in the domain
		double rightEdge = domain.iterator().next();
		if (x < rightEdge)
			throw new IllegalArgumentException("x is not in the mass fcn domain");
		// find which layer x is in.
		// as long as x is to the right, move to the next layer edge
		int layer = 0;
		while (x > rightEdge || rightEdge == domain.getLB()) {
			if (layer >= numLayers)
				throw new IllegalArgumentException("x is not in the mass fcn domain");
			rightEdge += widths[layer++];
		}
		return layer - 1;
	}
	
	public Function getMass() {
		return new LazyFunction(getProblemDomain()) {
			@Override
			public double evalAt(double x) {
				return effMasses[getLayer(x, domain)];
			}
		};
	}
	
	public Function getDielectric() {
		return new LazyFunction(getProblemDomain()) {
			private static final double eps0 = 0.00552635;
			@Override
			public double evalAt(double x) {
				return dielecs[getLayer(x, domain)]*eps0;
			}
		};
	}

	public Function getBgPotential() {
		final double[] bGPVals = bandGaps.clone();
		LazyFunction bGPTemp = new LazyFunction(domain){
			@Override
			public double evalAt(double x){
				return bGPVals[getLayer(x, domain)];
			};
		};
		return bGPTemp.offset();
	}
	
}
