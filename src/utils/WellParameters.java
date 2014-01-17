package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class WellParameters {
	
	public static final String defaultParamsFileName = "./well_params_default.kvp";

	private Properties params;

	public int numLayers;
	
	private double
		dx, errTol;
	
	private double[]
		widths, bandGaps, dielecs, effMasses;
	
	private Domain domain;
	
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
	
	public double getErrTolerance() {
		return errTol;
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
		while (x > rightEdge) {
			if (layer >= numLayers)
				throw new IllegalArgumentException("x is not in the mass fcn domain");
			rightEdge += widths[layer++];
		}
		return layer;
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
			@Override
			public double evalAt(double x) {
				return dielecs[getLayer(x, domain)];
			}
		};
	}

	public Function getBgPotential() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
