package stream_metab.doxygen.edge.lagrangeb;

import neo.motif.*;
import neo.state.*;
import stream_metab.doxygen.utils.*;

/**
 * Controls calculation of the density of water at the upstream end of the reach
 * 
 * <p>WARNING!  This is a unit-specific calculation from and empirical relationship. </p> 
 * 
 * @author robert.payn
 *
 */
public class UpDensity extends AbstractUpdaterDbl {

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
     * @see UpTemp
     */
    @Override
    protected void setDependencies()
    {
        tempCelsius = (HStateDbl)getInitHState(UpTemp.class.getSimpleName());
    }

}
