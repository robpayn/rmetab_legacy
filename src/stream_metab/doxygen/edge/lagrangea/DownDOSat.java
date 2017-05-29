package stream_metab.doxygen.edge.lagrangea;

import neo.holon.*;
import neo.key.*;
import neo.motif.*;
import neo.state.*;
import neo.util.*;
import stream_metab.doxygen.utils.*;

/**
 * Controls the saturated dissolved oxygen concentration at a given water
 * temperature and atmospheric pressure
 * <p>
 * <b>References:</b>
 * </p>
 * <ul style="list-style-type: none"> <li
 * style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px">García, H.
 * E., and L. I. Gordon (1992) Oxygen solubility in seawater: better fitting
 * equations. Limnology and Oceanography 37, 1307-1312.</li> </ul>
 * 
 * @author Rob Payn
 */
public class DownDOSat extends AbstractUpdaterDbl {

    /**
     * Air pressure [Length] of Hg
     */
    private HStateDbl airpressure;
    
    /**
     * Aggregation of constants for calculation
     */
    private double constant;
    
    /**
     * Density of water (kg L<sup><small>-1</small></sup>)
     */
    private HStateDbl densityWater;
    
    /**
     * Temperature (&deg;C)
     */
    private HStateDbl tempCelsius;
    
    /**
     * Unit conversion factor ({user defined units} (g
     * m<sup><small>-3</small></sup>)<sup><small>-1</small></sup>)
     */
    private HStateDbl unitConv;

    /**
     * Calculates the saturated DO concentration using an empirical relationship
     * (García and Gordon 1992) with temperature and barometric pressure.
     * <p>
     * Must provide a unit conversion factor (SatDOConcConv) in the input tables
     * if this is used with a model running with units of mass other than grams
     * or units of length other than meters.
     * </p>
     * 
     * @return DO concentration [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double computeValue()
    {
        // Based on empirical relationship between saturated DO concentration and
        // temperature/pressure
        // (García and Gordon 1992)
        return constant * densityWater.v * airpressure.v *
                Calculators.satDOEmpirical(tempCelsius.v);
    }

    /**
     * Calculates the initial saturated DO concentration using an empirical
     * relationship with temperature and barometric pressure.
     * <p>
     * Must provide a unit conversion factor (SatDOConcConv) in the input tables
     * if this is used with a model running with units of mass other than grams
     * or units of length other than meters.
     * </p>
     * 
     * @return DO concentration [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double initValue()
    {
        // calculate aggregated constant multiplier
        // note this integrates all constants needed for calculate method
        // including the molecular weight of oxygen gas (0.032 g/mmol)
        // and standard atmospheric pressure (760 mm Hg)
        constant = unitConv.v * (0.032 / 760);
        
        return computeValue();
    }

    /**
     * Define the state dependencies for calculating saturated DO concentration
     * 
     * @see SatDOConcConv
     * @see DownTemp
     * @see stream_metab.doxygen.patch.airdo.BaroPressure
     */
    @Override
    protected void setDependencies()
    {
        // Get reference to attached patch
        Patch myPatch = ((Edge)myHolon).getFrom();

        // Find all edges with the resource doxygen and the behavior
        // reaerationdo
        Holon[] reaerationEdges = myPatch.getAttachedHolons(Key.cast(Doxygen.class.getSimpleName()), 
                Doxygen.Names.MOTIF_REAERATION);

        // Check for valid number of edges
        if (reaerationEdges.length == 1)
        {
            // One edge is valid, get the references and set the dependencies
            airpressure = (HStateDbl) getInitHState(((Edge) reaerationEdges[0]).getFrom(), 
                    Doxygen.Names.AIR_PRESSURE);
        }
        else
        {
            // Anything other than one edge is invalid, log an error
            Logger.logError("Must be one and only one edge connect to " + myPatch.getUID().toString()
                    + " with a doxygen.edge.reaeration behavior.");
        }

        // Get other references and set dependencies
        densityWater = (HStateDbl)getInitHState(DownDensity.class.getSimpleName());
        tempCelsius = (HStateDbl)getInitHState(DownTemp.class.getSimpleName());
        unitConv = (HStateDbl)getInitHState(SatDOConcConv.class.getSimpleName());
    }

}
