	
// For input, the program requires: 
	// DRY(dry-bulb reading),
	// WET(wet-bulb) readings, 
	// a yes or no decision regarding ISNOW (snow on the ground), 
	// PRECIP (preceding 24-hour precipitation), 
	// WIND (current windspeed), 
	// BUO (yesterday's buildup index), and 
	// IHERB (current herbaceous stage of vegetation). 

import java.util.Scanner;
import java.lang.Math;
// TODO: Auto-generated Javadoc

/**
 * The Class FLOAD to calculate the Fire Load.
 */
public class FLOAD{

	private static Scanner input;
	
	/**
	 * Cal bui.
	 *
	 * @param BUO the buo
	 * @param PRECIP the precip
	 * @return the double
	 */
	// calculates Current Buildup Index
	public static double CalBUI(double BUO, double PRECIP){
		double BUI = 0;
		// if the preceding 24-hour Precipitation > 0.1 
		// BUI must be adjusted by adding the DF(HOW?) after correction **
				if (PRECIP > .1 ) {
				BUI = -50 * Math.log(1-(1-Math.exp(-BUO/50))*Math.exp(-1.175*(PRECIP-.1))); 
						if (BUI > 0) {
							return BUI;
						}else {
							BUI = 0;
						}
					}
		return BUO;
	}
					
	/**
	 * Cal ffm.
	 *
	 * @param DRY the dry
	 * @param WET the wet
	 * @return the double
	 */
	// calculates Fine Fuel Moisture ( NO SNOW)	
	public static double CalFFM(double DRY, double WET){
		double FFM = 99;
		double Ai [] = {30.0,19.2,13.8,22.5};
		double Bi [] = {-0.185900,-0.859000, -0.059660,-0.077373};
		double Ci [] = {4.5, 12.5, 27.5};	
		double DIF = DRY - WET;
			for (int i=0; i<3; i++)
				if ((DIF - Ci [i]) <= 0) {
					FFM = Ai[i]*Math.exp(Bi[i])*DIF;
				}else {
					i = 4;
					FFM = Ai[i]*Math.exp(Bi[i])*DIF;
				}	
				return FFM;
		}
	
	/**
	 * Cal df.
	 *
	 * @param IHERB the iherb
	 * @return the double
	 */
	// calculates Drying Factor for the day ( NO SNOW)		
	public static double CalDF(double IHERB){
		double DF,FFM;
		DF = 1;
		FFM = 99;
		double Di [] = {16.00, 10.0, 7.0, 5.0, 4.0, 3.0};
		// loop to 
			for (int i=0; i<6; i++)
				if (FFM - Di [i] <= 0) {
					DF = 7;
				}else {
					DF = i - 1; // test to see if the Fine Moisture is 1 or less
				}
				if (FFM < 1) {
				FFM = 1;
				}	
				else {
				// Herb stage  ('1' for Cured, '2' for Transition, '3' for Green) 
				// is used to adjust the calculated FFM by adding 5% for Transition or 10% for Green.
				FFM = FFM + (IHERB - 1) * 5; 
				}
			return DF;
		}
	
	/**
	 * Cal adfm.
	 *
	 * @param WIND the wind
	 * @return the double
	 */
	// calculates Adjusted Fuel Moist ( NO SNOW)
	public static double CalADFM(double WIND){
		double FFM,ADFM,BUO;
		FFM = 99;
		ADFM = 99;
		BUO = 0;
		ADFM = 0.9*FFM + 0.5 + 9.5 * Math.exp(-BUO/50); 
		return ADFM;
	}
	
	/**
	 * Cal grass.
	 *
	 * @param WIND the wind
	 * @return the double
	 */
	// calculates Fine Fuel Spread 
	public static double CalGRASS(double WIND){
		double FFM,GRASS;
		FFM = 99;
		if (WIND < 14) {
			GRASS = 0.01312 * (WIND + 6) * Math.pow((33 - FFM),1.65) - 3;
		}else {
			GRASS = 0.00918 * (WIND + 14.4) * Math.pow((33 - FFM),1.65) - 3;
					}
		return GRASS;
		}
		
	/**
	 * Cal timber.
	 *
	 * @param WIND the wind
	 * @param BUO the buo
	 * @param IHERB the iherb
	 * @return the double
	 */
	// calculates Timber Spread Index				
	public static double CalTIMBER(double WIND, double BUO, double IHERB){
		double ADFM,TIMBER;
		ADFM = 99;
		if (WIND < 14) {
			TIMBER = 0.01312 * (WIND + 6) * Math.pow((33 - ADFM),1.65) - 3;
		}else {
			TIMBER = 0.00918 * (WIND + 14.4) * Math.pow((33 - ADFM),1.65) - 3;
		}
	return TIMBER;
	}
		
	// calculates Fire Load Index (Man-Hour Base)
	/**
	 * Cal fload.
	 *
	 * @param BUO the buo
	 * @return the double
	 */
	// calculates Fire Load Rating only if TIMBER & BUO are greater than 0
	public static double CalFLOAD(double BUO){
		double TIMBER,FLOAD;
		FLOAD = 0;
		TIMBER = 0;
		FLOAD = 1.75 * Math.log(TIMBER) + 0.32 * Math.log(BUO) - 1.640;  
			if (FLOAD < 0) {
				FLOAD = 0;
				return FLOAD;
			}else {
				FLOAD = Math.pow(10, FLOAD);
				return FLOAD;
			}
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	// main Program Here //
	public static void main(String[] args) {
		
		input = new Scanner(System.in);
        System.out.print("Please enter the Dry Bulb Temp: ");
        double DRY = input.nextDouble();
        System.out.print("Please enter the Wet Bulb Temp: ");
        double WET = input.nextDouble();
        System.out.print("Please enter the Current Wind Speed: ");
        double WIND = input.nextDouble();
        System.out.print("Please enter the last value of the Buildup Index : ");
        double BUO = input.nextDouble();
        System.out.print("Please enter the Current Herp State of District ('1' for Cured, '2' for Transition, '3' for Green): ");
        double IHERB = input.nextDouble();
        System.out.print("Please enter 0 for 'NO' snow and 1 for snow on the ground: ");
        int ISNOW = input.nextInt();
        System.out.print("Please enter the past 24 hours precipitation in inches and hundredths : ");
        double PRECIP = input.nextDouble();                

        /**
    	 * Check isnow.
    	 *
    	 * @param ISNOW the isnow
    	 * @return the int
    	 */
    	// checks ISNOW
        // tests to see if there is Snow
    		if (ISNOW == 1) {
    			
    		double GRASS = 0;
    		double TIMBER = 0;
 			double FLOAD = 0;
 			double BUI = CalBUI(PRECIP, BUO); //adjusts the BUI
   			System.out.printf("Grass Spread Index is: %s\n", GRASS);
   	        System.out.printf("Timber Spread Index is: %s\n", TIMBER);
   	        System.out.printf("Fire Load Raiting is: %s\n", FLOAD);
   	        System.out.printf("Build Up Index is: %s\n", BUI);
    		
    		}else {
    			
        	double FFM = CalFFM(DRY, WET);
            double ADFM = CalADFM(WIND);
            double GRASS = CalGRASS(WIND);
            double TIMBER = CalTIMBER(WIND, BUO, IHERB);
            double FLOAD = CalFLOAD(BUO);
            double BUI = CalBUI(PRECIP, BUO);
            
            System.out.printf("Fine Fuel Moisture is: %s\n", FFM);
            System.out.printf("Adjusted Fuel Moisture is: %s\n", ADFM);
            System.out.printf("Grass Spread Index is: %s\n", GRASS);
            System.out.printf("Timber Spread Index is: %s\n", TIMBER);
            System.out.printf("Fire Load Rating is: %s\n", FLOAD);
            System.out.printf("Build Up Index is: %s\n", BUI);
        }
  	
	}
}
