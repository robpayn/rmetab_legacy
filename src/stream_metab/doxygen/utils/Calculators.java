package stream_metab.doxygen.utils;

/**
 * Utility static calculators that are used my multiple behaviors
 * 
 * @author robpayn
 */
public class Calculators {
    
    /**
     * Calculates the density of water at a given temperature
     * 
     * @param tempCelsius temperature (&deg;C)
     * @return density (kg L<sup><small>-1</small></sup>)
     */
    public static double densityWaterEmpirical(double tempCelsius)
    {
        return 0.999842 + tempCelsius
                * (6.7940e-5 + tempCelsius
                * (-9.0953e-6 + tempCelsius
                * (1.0017e-7 + tempCelsius
                * (-1.1201e-9 + tempCelsius
                * 6.5363e-12))));
    }
    
    /**
     * <p>Calculates the saturated DO concentration at standard pressure for
     * the given temperature</p>
     * <b>References:</b>
     * <ul style="list-style-type: none"> <li
     * style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px">García, H.
     * E., and L. I. Gordon (1992) Oxygen solubility in seawater: better fitting
     * equations. Limnology and Oceanography 37, 1307-1312.</li> </ul>
     * 
     * @param tempCelsius temperature (&deg;C)
     * @return DO concentration at standard pressure (moles kg<sup><small>-1</small></sup>)
     */
    public static double satDOEmpirical(double tempCelsius)
    {
        // calculate the current normalized temperature
        double tempNorm = Math.log((298.15 - tempCelsius) / (tempCelsius + 273.15));
        return  Math.exp(
                5.80871 + tempNorm
                * (3.20291 + tempNorm
                * (4.17887 + tempNorm
                * (5.10006 + tempNorm
                * (-0.0986643 + tempNorm 
                * 3.80369))))
                );
    }
    
    /**
     * <p>Calculate the Schmidt number for oxygen in water at a given temperature</p>
     * <b>References:</b>
     * <ul style="list-style-type: none">
     *      <li style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px">
     *      Wanninkhof, R.  1992.  Relationship between wind speed and gas exchange 
     *      over the ocean.  Journal of Geophysical Research 97(C5), 7373-7382.
     *      </li>
     * </ul>
     * 
     * @param tempCelsius temperature (&deg;C)
     * @return Schmidt number
     */
    public static double schmidtNumberEmpirical(double tempCelsius)
    {
        return 1800.6 + tempCelsius
                * (-120.1 + tempCelsius
                * (3.7818 + tempCelsius
                * -0.047608));
    }

}
