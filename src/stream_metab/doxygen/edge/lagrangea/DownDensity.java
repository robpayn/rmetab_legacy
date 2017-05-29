package stream_metab.doxygen.edge.lagrangea;

import stream_metab.doxygen.utils.Calculators;
import neo.motif.*;
import neo.state.*;

/**
 * Controls calculation of the density of water
 * 
 * <p>WARNING!  This is a unit-specific calculation from an empirical relationship. </p> 
 * 
 * @author robert.payn
 *
 */
public class DownDensity extends AbstractUpdaterDbl {

    /**
     * Temperature (&deg;C)
     */
    private HStateDbl tempCelsius;
    
    /**
     * Calculates the density of water based on temperature in &deg;C
     * 
     * @return density (kg L<sup><small>-1</small></sup>)
     */
    @Override
    protected double computeValue()
    {
        return Calculators.densityWaterEmpirical(tempCelsius.v);
    }

    /**
     * Calculates the density of water based on temperature in &deg;C
     * 
     * @return density (kg L<sup><small>-1</small></sup>)
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Define the state dependencies for calculating saturated DO concentration
     * 
     * @see DownTemp
     */
    @Override
    protected void setDependencies()
    {
        tempCelsius = (HStateDbl)getInitHState(DownTemp.class.getSimpleName());
    }

}
