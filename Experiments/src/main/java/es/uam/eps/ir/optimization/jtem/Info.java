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

import java.io.OutputStream;

/**
 * The class can be used to save the debug and iteration information in some classes of this package.
 * @author Markus Schmies, Vitali Lieder
 * @version 1.0
 */
public final class Info
    implements java.io.Serializable {


  private boolean debug;
  private String message = "";
  private int currentIter = 0, maxIter = 0;

  /**
   * Constructs Info object for minimizing process.
   * @param debug indicates printing debug output.
   */
  public Info( final boolean debug ) {
    this.debug= debug;
  }

  /**
   * Constructs Info object for minimizing process.
   */
  public Info() {
    this(false);
  }

  /**
   * Sets debug for indicating printing debug ouput.
   * @param debug indicates printing debug output.
   */
  public void setDebug( final boolean debug ){
      this.debug= debug;
  }

  /**
   * Gets the current status of printing debug ouput.
   * @return debug info.
   */
  public boolean getDebug(){
    return debug;
  }


  /**
   * Returns the message.
   * @return message as string.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Returns the reached iteration.
   * @return current iteration number
   */
  public int getCurrentIter() {
    return currentIter;
  }

  /**
   * Returns max number of iteration.
   * @return max. allowed iteration
   */
  public int getMaxIter() {
    return maxIter;
  }

  /**
   * Returns true if max. number of interration was reached otherwise false.
   * @return boolean whether max number of interation was reached.
   */
  public boolean isMaxIterationReached() {
    return currentIter>=maxIter;
  }

  /**
   * Set message BY REFERENCE.
   * @param str message
   */
  void setMessage(final String str) {
    message = str;
  }

  /**
   * Adds message to the end of existing in new line.
   * @param str message
   */
  void addMessage(final String str) {
    message = message +"\n"+  str;
  }

  /**
   * Set iteration that was reached.
   * @param currentIter surrent iteration
   */
  void setCurrentIter(final int currentIter) {
    this.currentIter = currentIter;
  }

  /**
   * Set max number of interation.
   * @param maxIter max interation allowed
   */
  void setMaxIter(final int maxIter) {
    this.maxIter = maxIter;
  }

  /**
   * Prints information storing in this object to output.
   */
  public void printDebug(){
    if( debug )
      System.out.println( toString() );
  }

  /**
   * Prints information storing in this object to out stream.
   * @param out stream where the info will be printed.
   * @throws java.io.IOException #see OutputStream.write( bytes[] ).
   */
  public void printDebug( OutputStream out ) throws java.io.IOException{
    if ( debug )
      out.write( toString().getBytes() );
  }

  /**
   * Returns a string representation of the object.
   * @return a string representation of the object.
   */
    @Override
  public String toString() {
    return "Max Iteration in method: " + maxIter
        + ", reached iteration: " + currentIter + "\nMessage: " + message;
  }
}