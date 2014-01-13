package utils;

import java.util.Properties;

public abstract class Parameters {

	protected Properties params;
	
	public boolean containsParameter(String paramName) {
		return params.containsKey(paramName);
	}
	
	public String getParameter(String paramName) {
		return params.getProperty(paramName);
	}

	public double getDouble(String paramName) throws NumberFormatException {
		return Double.parseDouble(params.getProperty(paramName));
	}
	
	
}