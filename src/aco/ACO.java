/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aco;

/**
 *
 * @author David
 */
public class ACO {

    int NUMBEROFANTS, NUMBEROFCITIES, INITIALCITY;
	double ALPHA, BETA, Q, RO, TAUMAX;
	
	double BESTLENGTH;
	int[] BESTROUTE;

	int[][] GRAPH, ROUTES;
	double[][] CITIES, PHEROMONES, DELTAPHEROMONES, PROBS;

	Randoms randoms;
    
    public ACO (int nAnts, int nCities, 
		double alpha, double beta, double q, double ro, double taumax,
		int initCity) {
	NUMBEROFANTS 	= nAnts;
	NUMBEROFCITIES 	= nCities;
	ALPHA 			= alpha;
	BETA 			= beta;
	Q 				= q;
	RO 				= ro;
	TAUMAX 			= taumax;
	INITIALCITY		= initCity;

	randoms = new Randoms (21);	
}

public void init () {
	GRAPH 		= new int[NUMBEROFCITIES][NUMBEROFCITIES];
	CITIES 			= new double[NUMBEROFCITIES][2];
	PHEROMONES 		= new double[NUMBEROFCITIES][NUMBEROFCITIES];
	DELTAPHEROMONES = new double[NUMBEROFCITIES][NUMBEROFCITIES];
	PROBS 			= new double[NUMBEROFCITIES][2];
        //PROBS 			= new double[NUMBEROFCITIES - 1][2];
	for(int i=0; i<NUMBEROFCITIES; i++) {
		for (int j=0; j<2; j++) {
			CITIES[i][j] = -1.0;
			PROBS[i][j]  = -1.0;
		}
		for (int j=0; j<NUMBEROFCITIES; j++) {
			GRAPH[i][j] 			= 0;
			PHEROMONES[i][j] 		= 0.0;
			DELTAPHEROMONES[i][j] 	= 0.0;
		}
	}	

	ROUTES = new int[NUMBEROFANTS][NUMBEROFCITIES];
	for (int i=0; i<NUMBEROFANTS; i++) {
		for (int j=0; j<NUMBEROFCITIES; j++) {
			ROUTES[i][j] = -1;
		}
	}
	
	BESTLENGTH = (double) Integer.MAX_VALUE;
	BESTROUTE  = new int[NUMBEROFCITIES];
	for (int i=0; i<NUMBEROFCITIES; i++) {
		BESTROUTE[i] = -1;	
	}
}


public void connectCITIES (int cityi, int cityj) {
	GRAPH[cityi][cityj] = 1;
	PHEROMONES[cityi][cityj] = randoms.Uniforme() * TAUMAX;
	GRAPH[cityj][cityi] = 1;
	PHEROMONES[cityj][cityi] = PHEROMONES[cityi][cityj];
}
public void setCITYPOSITION (int city, double x, double y) {
	CITIES[city][0] = x;
	CITIES[city][1] = y;
}
public void printPHEROMONES () {	
	System.out.print(" PHEROMONES: ");
	System.out.print("  | ");
	for (int i=0; i<NUMBEROFCITIES; i++) {
		System.out.format("%5d   ", i);
	}
	System.out.print("\n - | ");
	for (int i=0; i<NUMBEROFCITIES; i++) {
		System.out.print("--------");
	}
	System.out.print("\n");
	for (int i=0; i<NUMBEROFCITIES; i++) {
		System.out.print( i + " | ");
		for (int j=0; j<NUMBEROFCITIES; j++) {
			if (i == j) {
				System.out.format("%5s   ", "x");
				continue;
			}
			if (exists(i, j)) {
				System.out.format("%7.3f ", PHEROMONES[i][j]);
			}
			else {
				if(PHEROMONES[i][j] == 0.0) {
					System.out.format("%5.0f   ", PHEROMONES[i][j]);
				}
				else {
					System.out.format("%7.3f ", PHEROMONES[i][j]);
				}
			}
		}
		System.out.print("\n");
	}
	System.out.print("\n");
}


public double distance (int cityi, int cityj) {
	return (double) 
		Math.sqrt (Math.pow (CITIES[cityi][0] - CITIES[cityj][0], 2) + 
 			  Math.pow (CITIES[cityi][1] - CITIES[cityj][1], 2));
}
public boolean exists (int cityi, int cityc) {
	return (GRAPH[cityi][cityc] == 1);
}
public boolean vizited (int antk, int c) {
	for (int l=0; l<NUMBEROFCITIES; l++) {
		if (ROUTES[antk][l] == -1) {
			break;
		}
		if (ROUTES[antk][l] == c) {
			return true;
		}
	}
	return false;
}
public double PHI (int cityi, int cityj, int antk) {
	double ETAij = (double) Math.pow (1 / distance (cityi, cityj), BETA);
	double TAUij = (double) Math.pow (PHEROMONES[cityi][cityj],   ALPHA);

	double sum = 0.0;
	for (int c=0; c<NUMBEROFCITIES; c++) {
		if (exists(cityi, c)) {
			if (!vizited(antk, c)) {
				double ETA = (double) Math.pow (1 / distance (cityi, c), BETA);
				double TAU = (double) Math.pow (PHEROMONES[cityi][c],   ALPHA);
				sum += ETA * TAU;
			}	
		}	
	}
	return (ETAij * TAUij) / sum;
}

public double length (int antk) {
	double sum = 0.0;
	for (int j=0; j<NUMBEROFCITIES-1; j++) {
		sum += distance (ROUTES[antk][j], ROUTES[antk][j+1]);	
	}
	return sum;
}

public int city () {
	double xi = randoms.Uniforme();
	int i = 0;
	double sum = PROBS[i][0];
	while (sum < xi) {
		i++;
		sum += PROBS[i][0];
	}
	return (int) PROBS[i][1];
}

public void route (int antk) {
	ROUTES[antk][0] = INITIALCITY;
	for (int i=0; i<NUMBEROFCITIES-1; i++) {		
		int cityi = ROUTES[antk][i];
		int count = 0;
		for (int c=0; c<NUMBEROFCITIES; c++) {
			if (cityi == c) {
				continue;	
			}
			if (exists (cityi, c)) {
				if (!vizited (antk, c)) {
					PROBS[count][0] = PHI (cityi, c, antk);
					PROBS[count][1] = (double) c;
					count++;
				}

			}
		}
		
		// deadlock
		if (0 == count) {
			return;
		}
		
		ROUTES[antk][i+1] = city();
	}
}
public int valid (int antk, int iteration) {
	for(int i=0; i<NUMBEROFCITIES-1; i++) {
		int cityi = ROUTES[antk][i];
		int cityj = ROUTES[antk][i+1];
		if (cityi < 0 || cityj < 0) {
			return -1;	
		}
		if (!exists(cityi, cityj)) {
			return -2;	
		}
		for (int j=0; j<i-1; j++) {
			if (ROUTES[antk][i] == ROUTES[antk][j]) {
				return -3;
			}	
		}
	}
	
	if (!exists (INITIALCITY, ROUTES[antk][NUMBEROFCITIES-1])) {
		return -4;
	}
	
	return 0;
}

public void printGRAPH () {
	System.out.print(" GRAPH: " + "\n");
	System.out.print( "  | ");
	for( int i=0; i<NUMBEROFCITIES; i++) {
		System.out.print( i +" ");
	}
	System.out.print("\n"+"- | ");
	for (int i=0; i<NUMBEROFCITIES; i++) {
		System.out.print("- ");
	}
	System.out.print("\n");
	int count = 0;
	for (int i=0; i<NUMBEROFCITIES; i++) {
		System.out.print(i + " | ");
		for (int j=0; j<NUMBEROFCITIES; j++) {
			if(i == j) {
				System.out.print("x ");	
			}
			else {
				System.out.print(GRAPH[i][j] +" ");	
			}
			if (GRAPH[i][j] == 1) {
				count++;	
			}
		}
		System.out.print("\n");
	}
	System.out.print("\n");
	System.out.print("Number of connections: " + count + "\n" + "\n");
}
public void printRESULTS () {
	BESTLENGTH += distance (BESTROUTE[NUMBEROFCITIES-1], INITIALCITY);
	System.out.print(" BEST ROUTE:" +"\n");
	for (int i=0; i<NUMBEROFCITIES; i++) {
		System.out.print(BESTROUTE[i] + " ");
	}
	System.out.print("\n"+"length: " + BESTLENGTH +"\n");
	
	System.out.print("\n"+" IDEAL ROUTE:" +"\n");
	System.out.println("0 7 6 2 4 5 1 3");
	System.out.println("length: 127.509");
}

public void updatePHEROMONES () {
	for (int k=0; k<NUMBEROFANTS; k++) {
		double rlength = length(k);
		for (int r=0; r<NUMBEROFCITIES-1; r++) {
			int cityi = ROUTES[k][r];
			int cityj = ROUTES[k][r+1];
			DELTAPHEROMONES[cityi][cityj] += Q / rlength;
			DELTAPHEROMONES[cityj][cityi] += Q / rlength;
		}
	}
	for (int i=0; i<NUMBEROFCITIES; i++) {
		for (int j=0; j<NUMBEROFCITIES; j++) {
			PHEROMONES[i][j] = (1 - RO) * PHEROMONES[i][j] + DELTAPHEROMONES[i][j];
			DELTAPHEROMONES[i][j] = 0.0;
		}	
	}
}


public void optimize (int ITERATIONS) {
	for (int iterations=1; iterations<=ITERATIONS; iterations++) {
		//System.out.print(flush);
		System.out.print("ITERATION " + iterations + " HAS STARTED!" +"\n"+"\n");

		for (int k=0; k<NUMBEROFANTS; k++) {
			System.out.print(" : ant " + k + " has been released!" + "\n");
			while (0 != valid(k, iterations)) {
				System.out.print("  :: releasing ant " + k + " again!" + "\n");
				for (int i=0; i<NUMBEROFCITIES; i++) {
					ROUTES[k][i] = -1;	
				}
				route(k);
			}
			
			for (int i=0; i<NUMBEROFCITIES; i++) {
				System.out.print(ROUTES[k][i] + " ");	
			}
			System.out.print("\n");
			
			System.out.print("  :: route done" + "\n");
			double rlength = length(k);

			if (rlength < BESTLENGTH) {
				BESTLENGTH = rlength;
				for (int i=0; i<NUMBEROFCITIES; i++) {
					BESTROUTE[i] = ROUTES[k][i];
				}
			}
			System.out.print(" : ant " + k + " has ended!" + "\n");				
		}		

		System.out.print("\n"+"updating PHEROMONES . . .");
		updatePHEROMONES ();
		System.out.print(" done!" + "\n" + "\n");
		printPHEROMONES ();
		
		for (int i=0; i<NUMBEROFANTS; i++) {
			for (int j=0; j<NUMBEROFCITIES; j++) {
				ROUTES[i][j] = -1;
			}
		}

		System.out.print("\n"+"ITERATION " + iterations + " HAS ENDED!" + "\n" + "\n");
	}
}


    public static void main(String[] args) {
    
        int ITERATIONS = 5;

        int NUMBEROFANTS = 4;
        int NUMBEROFCITIES = 8;

        // if (ALPHA == 0) { stochastic search & sub-optimal route }
        double ALPHA = 0.5;
        // if (BETA  == 0) { sub-optimal route }
        double BETA = 0.8;
        // Estimation of the suspected best route.
        double Q = 80;
        // Pheromones evaporation. 
        double RO = 0.2;
        // Maximum pheromone random number.
        int TAUMAX = 2;

        int INITIALCITY = 0;

        ACO ANTS = new ACO (NUMBEROFANTS, NUMBEROFCITIES, 
			 			ALPHA, BETA, Q, RO, TAUMAX,
			 			INITIALCITY);

	ANTS.init();

	ANTS.connectCITIES (0, 1);
	ANTS.connectCITIES (0, 2);
	ANTS.connectCITIES (0, 3);
	ANTS.connectCITIES (0, 7);
	ANTS.connectCITIES (1, 3);
	ANTS.connectCITIES (1, 5);
	ANTS.connectCITIES (1, 7);
	ANTS.connectCITIES (2, 4);
	ANTS.connectCITIES (2, 5);
	ANTS.connectCITIES (2, 6);
	ANTS.connectCITIES (4, 3);
	ANTS.connectCITIES (4, 5);
	ANTS.connectCITIES (4, 7);
	ANTS.connectCITIES (6, 7);
	/* ANTS -> connectCITIES(8, 2);
	ANTS -> connectCITIES(8, 6);
	ANTS -> connectCITIES(8, 7); */

	ANTS.setCITYPOSITION (0,  1,  1);
	ANTS.setCITYPOSITION (1, 10, 10);
	ANTS.setCITYPOSITION (2, 20, 10);
	ANTS.setCITYPOSITION (3, 10, 30);
	ANTS.setCITYPOSITION (4, 15,  5);
	ANTS.setCITYPOSITION (5, 10,  1);
	ANTS.setCITYPOSITION (6, 20, 20);
	ANTS.setCITYPOSITION (7, 20, 30);
	// ANTS -> setCITYPOSITION(8, 26, 20);

	ANTS.printGRAPH ();

	ANTS.printPHEROMONES ();

	ANTS.optimize (ITERATIONS);

	ANTS.printRESULTS ();

    }
    
}
