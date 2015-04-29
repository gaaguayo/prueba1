/**
This file is part of a jTEM project.
All jTEM projects are licensed under the FreeBSD license 
or 2-clause BSD license (see http://www.opensource.org/licenses/bsd-license.php). 

Copyright (c) 2002-2009, Technische Universität Berlin, jTEM
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

-	Redistributions of source code must retain the above copyright notice, 
	this list of conditions and the following disclaimer.

-	Redistributions in binary form must reproduce the above copyright notice, 
	this list of conditions and the following disclaimer in the documentation 
	and/or other materials provided with the distribution.
 
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
OF SUCH DAMAGE.
**/

package es.uam.eps.ir.optimization.jtem;

/**
 * This class represents the routine to find a minimum of multidimensional 
 * function f with the simplex method (aka polytope method, or Nelder-Mead
 * algorithm) in mutidimensional. The main references used for this
 * implementation are: <BR>
 * <UL>
 * <LI>P.E. Gill, W. Murray and M.H. Wright, Practical Optimization, Academic
 * Press</LI>
 * <LI>J.C. Lagarias, J.A. Reeds, M.H. Wright and P.E. Wright, Convergence
 * Properites of the Nelder-Mead Simplex Method in Low Dimension, SIAM
 * Journal of Optimization, Vol 9, Number 1, pp 112-147, 1998</LI>
 * </UL>
 * @author Pierre-Yves Mignotte tks to a framework built by 
 * Markus Schmies, Vitali Lieder
 * @version 1.0
 */
public final class NelderMead
    /*implements java.io.Serializable*/ {

    private static final long serialVersionUID = 1L;

    /**
     * maximum number of function evaluation in search method.
     */
    static int ITMAX = 200;
    
    /** Reflection parameter (Default = 1.0) */
    static double rho = 1.0D;

    /** Expansion parameter (Default = 2.0) */
    static double chi = 2.0D;

    /** Contraction parameter (Default = 0.5) */
    static double gamma = 0.5D;

    /** Shrink parameter (Default = 0.5) */
    static double sigma = 0.5D;
    
  /**
   * Get the value of ITMAX.
   * @return Value of ITMAX.
   */
  /** @deprecated */
  public static int getITMAX() {
    return ITMAX;
  }

  /**
   * Set the value of ITMAX.
   * @param v  Value to assign to ITMAX.
   */
  /** @deprecated*/
  public static void setITMAX(int v) {
    ITMAX = v;
  }


  /**
   * Return the standard basis of dimension dim.
   * @param dim dimension of basis.
   * @return standard basis.
   */
  public static double[][] getStandardBasis(int dim) {

    double[][] basis = new double[dim][dim];

    for (int i = 0; i < dim; i++)
      basis[i][i] = 1;

    return basis;
  }

  /**
   * Search the minimum of function f  with Nelder-Mead simplex method in standard basis
   *  and return the value of the minimum.
   * @param p starting piont
   * @param ftol precision tolerance.
   * @param f given function.
   * @return the minimum of function f.
   */
  public final static double search(double[] p, double ftol,
                             RealFunctionOfSeveralVariables f, double stepSize) {

    return search(p, getStandardBasis(p.length), ftol, f, ITMAX, null, stepSize);
  }

  /**
   * Search the minimum of function f  with Nelder-Mead simplex method in standard basis
   * and return the value of the minimum.
   * @param p starting piont
   * @param ftol precision tolerance.
   * @param itmax maximal number of iterations
   * @param f given function.
   * @return the minimum of function f.
   */
  public final static double search(double[] p, double ftol, int maxIteration,
                             RealFunctionOfSeveralVariables f, double stepSize) {

    return search(p, getStandardBasis(p.length), ftol, f, maxIteration, null, stepSize );
  }

  /**
   * Search the minimum of function f  with Nelder-Mead simplex method in standard basis
   * and return the value of the minimum.
   * @param p starting piont
   * @param ftol precision tolerance.
   * @param itmax maximal number of iterations
   * @param f given function.
   * @param info object holding process information.
   * @return the minimum of function f.
   * @see Info
   */
  public final static double search(double[] p, double ftol, int maxIteration,
                             RealFunctionOfSeveralVariables f, Info info, double stepSize) {

    return search(p, getStandardBasis(p.length), ftol, f, maxIteration, info, stepSize );
  }

  /**
   * Search the minimum of function f  with Nelder-Mead simplex method in initial basis xi
   * and return the value of the minimum.
   * <BR> xi is used to create the first vertices. The set of vertex is p0 and
   * the n vertices where one variable of p0 is modified. If p0[i] != 0, then
   * pi[i] = p0[i]*(1+0.05*xi[i][i]), ie +5% of itself. If p0[i] == 0 then we
   * prefer to add 0.00025*xi[i][i]. Sometimes, this value doesn't allow to 
   * reach the optimum if it's too far.
   * @param p starting point.
   * @param x initial basis or initial step length.
   * @param ftol precision tolerance.
   * @param f given function.
   * @param info some gebug information.
   * @return the minimum of function f.
   * @see Info
   */
  public static double search(double[] p,
                       double[][] xi,
                       double ftol,
                       RealFunctionOfSeveralVariables f,
                       int itMax, Info info, double stepSize) {

      // Store the underdone action (shrink, expand,...)
      String action = "";

      int numberOfVariables = f.getNumberOfVariables();

      if (p.length == numberOfVariables) { // check the compatibility init/func
	  int iteration = 0;
	  double[][] vertices = new double[numberOfVariables+1][numberOfVariables];
	  double[] evaluation = new double[numberOfVariables+1];

	  double[] meanVertex = new double[numberOfVariables];
	  double[] reflected = new double[numberOfVariables];
	  double[] expanded = new double[numberOfVariables];
	  double[] contracted = new double[numberOfVariables];
	  double evReflected = 0.0;
	  double evExpanded = 0.0;
	  double evContracted = 0.0;

	  // Initialisation of our vertex
	  for (int j=0;j<numberOfVariables;j++) {
		  vertices[0][j] = p[j];
	  }

	  // Use of the learning rate parameters to compute the first
	  // vertices... The version from Matlab seems more convenient
	  /*for (int i=0;i<numberOfVariables;i++) {
	      for (int j=0;j<numberOfVariables;j++) {
		  vertices[i+1][j] = p[j]+xi[j][j];
	      }
	      }*/

	  // Matlab version for vertices initialisation
	  // Original suggestion of L.Pfeffer at Stanford
	  for (int i=0;i<numberOfVariables;i++) {
	      for (int j=0;j<numberOfVariables;j++) {
		  vertices[i+1][j] = p[j];
	      }
	      if (vertices[i+1][i] == 0) 
		  vertices[i+1][i] += 0.00025*xi[i][i];
	      else
		  vertices[i+1][i] *= (1+stepSize*xi[i][i]);
	  }

	  // Evaluation for each node of the vertex
	  for (int i=0;i<numberOfVariables+1;i++) {
	      evaluation[i] = f.eval(vertices[i]);
	  }

	  // Generation of the information message
	  // Initialisation message

	  if (info!= null) {
	      String s = new String(" f(p) = " + evaluation[0] + " , p = ");
	      
	      for (int i = 0; i < vertices[0].length; i++)
		  s += vertices[0][i] + " ";
	      
	      info.setMessage(s);
	      info.setMaxIter(itMax);
	  }

	  // Order the nodes according to the evaluation
	  order(vertices, evaluation);

	  while ( (Math.abs(evaluation[numberOfVariables] - evaluation[0]) > ftol) 
		  && (iteration++<itMax) && 
		  (sizeMax(vertices) > ftol*ftol)
		  ) {
	      // Compute of the centroid of the best vertices
	      updateMeanVertex(meanVertex, vertices, numberOfVariables);
	  
	      for (int i=0;i<numberOfVariables;i++) {
		  reflected[i] = (1+rho)*meanVertex[i] - rho*vertices[numberOfVariables][i];
	      }

	      evReflected = f.eval(reflected);

	      if (evReflected < evaluation[0]) {
		  // First case: Expansion
		  for (int i=0;i<numberOfVariables;i++) 
		      expanded[i] = (1+rho*chi)*meanVertex[i] - rho*chi*vertices[numberOfVariables][i];
		  
		  evExpanded = f.eval(expanded);

		  if (evExpanded < evReflected) {
		      // Accept expanded
		      for (int i=0;i<numberOfVariables;i++) 
			  vertices[numberOfVariables][i] = expanded[i];
		      evaluation[numberOfVariables] = evExpanded;
		      action = "Expand";
		  } else {
		      // Accept reflected
		      for (int i=0;i<numberOfVariables;i++) 
			  vertices[numberOfVariables][i] = reflected[i];
		      evaluation[numberOfVariables] = evReflected;
		      action="Reflection";
		  }
		  
	      } else {
		  // Second case: No expansion
		  if (evReflected>=evaluation[numberOfVariables-1]) {
		      //Contraction
		      if (evReflected<evaluation[numberOfVariables]) {
			  // Contract outside
			  for (int i=0;i<numberOfVariables;i++) 
			      contracted[i] = (1+rho*gamma)*meanVertex[i] - rho*gamma*vertices[numberOfVariables][i];
			  evContracted = f.eval(contracted);
			  if (evContracted <= evReflected) {
			      // Accept contracted
			      for (int i=0;i<numberOfVariables;i++) 
				  vertices[numberOfVariables][i] = contracted[i];
			      evaluation[numberOfVariables] = evContracted;
			      action="Outside Contraction";
			  } else {
			      // Shrink
			      shrink(vertices,evaluation,f);
			      action="Shrink";
			  }
		      } else {
			  // Contract inside
			  for (int i=0;i<numberOfVariables;i++) 
			      contracted[i] = (1-gamma)*meanVertex[i] + gamma*vertices[numberOfVariables][i];
			  evContracted = f.eval(contracted);
			  if (evContracted < evReflected) {
			      // Accept contracted
			      for (int i=0;i<numberOfVariables;i++) 
				  vertices[numberOfVariables][i] = contracted[i];
			      evaluation[numberOfVariables] = evContracted;
			      action="Inside contraction";
			  } else {
			      // Shrink
			      shrink(vertices,evaluation,f);
			      action = "Shrink";
			  }
		      }
		  } else {
		      // Accept reflected [no reflected] 
		      for (int i=0;i<numberOfVariables;i++) 
			  vertices[numberOfVariables][i] = reflected[i];
		      evaluation[numberOfVariables] = evReflected;
		      action="Reflection";
		  }
	      }

	      if (info!=null) {
		  String s = new String("iter = " + iteration + ", action = " + action + ", fp = " + evaluation[0] + ", p = ");
		  
		  for (int i = 0; i < vertices[0].length; i++)
		      s += vertices[0][i] + " ";
		  
		  info.addMessage(s);
	      }

	      order(vertices,evaluation);
	    
	      /* End of iteration */ 
	  }

	  if (Math.abs(evaluation[numberOfVariables] - evaluation[0]) <= ftol) { 
	      if (info!=null){
		  info.setCurrentIter(iteration);
		  info.printDebug();
	      }
	  }
	  if (info!=null && iteration >= itMax) {
	      info.setCurrentIter(iteration);
	      info.setMessage("Too many iterations in routine Nelder-Mead simplex");
	      info.printDebug();
	  }

	  for (int i=0;i<p.length;i++)
	      p[i] = vertices[0][i];
	  return(evaluation[0]);

      } else {
	  System.out.println("Uncompatible number of parameters and initial point");
	  return Double.NaN;
      }

  }

    private static void order (double[][] x, double[] y) {
	double tmp = 0.0;

	for (int i=1;i<y.length;i++) {
	    int j = i;

	    while ((j>0)) {
		if ((y[j] < y[j-1])) {
		    tmp = y[j]; 
		    y[j] = y[j-1]; 
		    y[j-1] = tmp;
		    for (int k=0;k<x[0].length;k++) {
			tmp = x[j][k];
			x[j][k] = x[j-1][k];
			x[j-1][k] = tmp;
		    }
		    j--;
		} else {
		    j=0;
		}
	    }
	}
    }

    /**
     * Compute the centroid of the best n vertices
     * @param mean New centroid 
     * @param samples All the vertices
     * @param variables Number of variables (or size of samples -1)
     */

    private static void updateMeanVertex (double[] mean, double[][] samples, int variables) {
	for (int i=0;i<variables;i++) {
	    mean[i] = 0;
	    for (int j=0;j<variables;j++) {
		mean[i] += samples[j][i];
	    }
	    mean[i] /= variables;
	}
    }

    /**
     * Perform the shink step
     * @param samples Vertices
     * @param functions Value of the function at each vertices
     * @param f Function to optimise
     */

    private static void shrink (double[][] samples, double[] functions, RealFunctionOfSeveralVariables f) {
	order(samples,functions);
	for (int i=1;i<samples.length;i++) {
	    for (int j=0;j<samples[0].length;j++) {
		samples[i][j] = (1-sigma)*samples[0][j]+sigma*samples[i][j];
	    }
	    functions[i] = f.eval(samples[i]);
	}
	order(samples,functions);
    }
    
    /**
     * Measure the maximum size of the vertices as the square of the Euclidean
     * distance between the two extrema vertices
     * @param vertices The vertices
     */

    private static double sizeMax (double[][] vertices) {
	double size = 0;
	for (int i=0;i<vertices[0].length;i++)
	    size += (vertices[0][i]-vertices[vertices.length-1][i])*(vertices[0][i]-vertices[vertices.length-1][i]);
	return(size);
    }

    /**
     * Measure the norm-2 of a vector
     * @param vector Vector to be measured
     * @return The norm
     */

    private static double norm2 (double[] vector) {
	double norm = 0;
	for (int i=0;i<vector.length;i++)
	    norm += vector[i]*vector[i];
	return(norm);
    }

}