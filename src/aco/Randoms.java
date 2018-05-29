/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aco;

import static aco.Randoms.NTAB;
import static java.lang.Math.log;
import static java.lang.Math.random;
import static java.lang.Math.sqrt;
import java.lang.reflect.Array;

/**
 *
 * @author David
 */
public class Randoms {

private long xpto;

// Generator seed.

public Randoms (long x) {xpto = -x;}
	
	
	// Returns a random Gaussian number.
	public double Normal (double avg, double sigma)
	{
		return (avg+sigma*gaussdev(xpto)) ;
	}
	// Returns a uniform random number between 0 and 1.
	double Uniforme()	
	{
		return ran1(xpto);
	}
	// Returns a random number between -m and m.
	double sorte(int m)
	{ 
		return (1.0*random())/(1.0*32767)*2.0*m-m;
	}

/*
	Taken from Numerical Recipes in C, Chapter 7. 
*/

public static int IA = 16807;
public static long IM = 2147483647;
public static double AM = (1.0/IM);
public static long IQ = 127773;
public static long IR = 2836;
public static int NTAB = 32;
public static double NDIV = (1+(IM-1)/NTAB);
public static double EPS = 1.2e-7;
public static double RNMX = (1.0-EPS);

public double ran1 (long idum)
/* 
"Minimal" random number generator of Park and Miller with Bays-Durham shuffle and added
safeguards. Returns a uniform random deviate between 0.0 and 1.0 (exclusive of the endpoint values). Call with idum a negative integer to initialize; thereafter, do not alter idum between successive deviates in a sequence. RNMX should approximate the largest floating value that is less than 1.
*/
{
	 int j;
	 long k;
	 long iy = 0;
	 long[] iv = new long[NTAB];
	 float temp;
	 if (idum <= 0 || iy!=0) {           // Initialize.
		 if (-(idum) < 1) idum=1;     // Be sure to prevent idum = 0.
		 else idum = -(idum);
		 for (j=NTAB+7;j>=0;j--) {      // Load the shuffle table (after 8 warm-ups).
			  k=(idum)/IQ;
			  idum= IA*(idum-k*IQ)-IR*k;
			  if (idum < 0) idum += IM;
			  if (j < 32) iv[j] = idum;
		 }
		 iy=iv[0];
	 }
	 k=(idum)/IQ;                     // Start here when not initializing.
	 idum=IA*(idum-k*IQ)-IR*k;       // Compute idum=(IA*idum) % IM without over-
	 if (idum < 0) idum += IM;       // flows by Schrage's method.
	 j= (int) (iy/NDIV);                        //  Will be in the range 0..NTAB-1.
	 iy=iv[j];                         // Output previously stored value and refill the
	 iv[j] = idum; 
         temp =(float) (AM*iy);// shuffle table.
	 if ((temp) > RNMX) 
		return RNMX; 				   // Because users don't expect endpoint values.
	 else 
		return temp;
}

float gaussdev(long idum)
// Returns a normally distributed deviate with zero mean and unit variance, 
// using ran1(idum) as the source of uniform deviates.
{
//    float ran1(long *idum);

	int iset=0;
	float gset = 0;
	float fac,rsq,v1,v2;
	if (idum < 0) iset=0;      //     Reinitialize.
	if (iset == 0) {            //     We don't have an extra deviate handy, so
	  do {
			 v1=(float) (2.0*ran1(idum)-1.0);    // pick two uniform numbers in the square ex-
			 v2=(float) (2.0*ran1(idum)-1.0);    // tending from -1 to +1 in each direction,
			 rsq=v1*v1+v2*v2;          // see if they are in the unit circle,

	  } while (rsq >= 1.0 || rsq == 0.0);  // and if they are not, try again.
	  fac=(float) sqrt(-2.0*log(rsq)/rsq);
	  // Now make the Box-Muller transformation to get two normal deviates. 
	  // Return one and save the other for next time.
	  gset=v1*fac;
	  iset=1;                 //  Set flag.
	  return v2*fac;
  } else {                    //   We have an extra deviate handy,
	  iset=0;                 //   so unset the flag,
	  return gset;            //   and return it.
  }
}

};

