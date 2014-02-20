package solvers;

/***Solves for energies and eigenstates using the finite difference method
 * 
 */
import utils.Function;
import utils.GreedyFunction;
import utils.WellParameters;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeLong;

public class FiniteDifferenceSolver extends SchrodingerSolver {

	public FiniteDifferenceSolver(WellParameters params, Function potential) {
		super(params, potential);
		eigenvalues = new double[params.getProblemDomain().getNumPoints()]; // bigger than needed
	}
	
	@Override
	public Function[] solveSystem() {
		// minimally define Hamiltonian matrix (it is symmetric and tridiagonal)
		double[] diag = potential.toArray();
		final int N = diag.length;
		double[] subDiag = new double[N]; // 1 extra length needed by called library code
		
		// initialize diag and subDiag
		for(int i = 0; i < N; ++i) {
			// diag elems == 2*K_E_C + potential at that point
			diag[i] +=  2*KIN_ENGY_COEFF;
			subDiag[i] = -KIN_ENGY_COEFF;
		}
		
		// setup call to lapack routine
		//TODO: do workspace query for num of evs, then real call
		NativeLong numEvsFound = new NativeLong();
		double[] eigfcns = new double[N*N]; // bigger than needed
		NativeLong[] isuppz = new NativeLong[2*N]; // bigger than needed
		NativeLong lWork = new NativeLong(18*N);
		double[] work = new double[lWork.intValue()];  // bigger than needed
		NativeLong liWork = new NativeLong(10*N);
		NativeLong[] iWork = new NativeLong[liWork.intValue()];  // bigger than needed
		NativeLong info = new NativeLong();
		
		// call lapack routine. for reference see http://www.netlib.org/lapack/explore-html/d9/d1e/dstemr_8f.html
		Native.setProtected(true);
		dstemr_((byte)'V',(byte)'V', new NativeLong(N), diag, subDiag,
				0.0, params.getBgPotential().evalAt(0), null, null, 
				numEvsFound, eigenvalues, 
				eigfcns, new NativeLong(N), new NativeLong(N), isuppz,
				false, //TODO: might have to switch back to nativelong
				work, lWork, iWork, liWork,
				info);
		
		// extract results into a form compatible with the rest of the code
		final int M = numEvsFound.intValue();
		Function[] ans = new GreedyFunction[M];
		double[] tmp = new double[N];
		for(int i = 0; i < M; ++i) {
			System.arraycopy(eigfcns, i*N, tmp, 0, N);
			ans[i] = new GreedyFunction(potential.getDomain(), tmp);
		}
		
		return ans;
	}
	
	private static native void dstemr_(byte jobz, byte range, NativeLong N, double[] d, double[] e, 
				double vl, double vu, NativeLong il, NativeLong iu, 
				NativeLong m, double[] w, 
				double[] z, NativeLong ldz, NativeLong nzc, NativeLong[] isuppz,
				boolean tryrac, 
				double[] work, NativeLong lwork, NativeLong[] iwork, NativeLong liwork,
				NativeLong info);
	
	static {
		NativeLibrary.addSearchPath("lapack", "bin/");
		Native.register("lapack");
	}

}
