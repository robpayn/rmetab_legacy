package stream_metab.doxygen.edge.advectdocalib;

import neo.holon.*;
import neo.motif.*;
import neo.state.*;
import neo.uid.*;
import neo.util.*;

/**
 * Controls the average saturated DO concentration over a reach based on
 * saturated DO at the upstream and downstream ends.
 * 
 * @author robert.payn
 */
public class SatDOConcReachAvg extends AbstractUpdaterDbl {

    /**
     * Saturated DO concentration at the upstream end
     * [Mass Length<sup><small>-3</small></sup>]
     */
    private HStateDbl inSatDOConc;
    
    /**
     * Saturated DO concentration at the downstream end
     * [Mass Length<sup><small>-3</small></sup>]
     */
    private HStateDbl outSatDOConc;

    /**
     * Calculates the average of upstream and downstream temperatures
     */
    @Override
    protected double computeValue()
    {
        // Temperature is estimated as the average of the inlet and outlet
        // temperatures
        return (inSatDOConc.v + outSatDOConc.v) * 0.5;
    }

    /**
     * Calculates the initial average of upstream and downstream temperatures
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Defines the dependencies for calculating average temperature
     * 
     * @see TempObs
     */
    @Override
    protected void setDependencies()
    {
        Boundary boundary;
        UniqueID uid;
        
        // Inlet temperature
        try
        {
            // Get the initialization table value that has the name of the inlet
            // boundary holon
            HStateStr boundaryName = (HStateStr) myHolon.getHState(Doxygen.Names.SAT_DO_CONC_IN);

            // Get the unique id of the inlet boundary holon
            uid = UniqueIDMgr.cast(boundaryName.v);

            if (uid == null)
            {
                // Log an error if inlet holon is not found
                Logger.logError("Holon " + myHolon.getUID().toString() 
                        + " cannot find holon " + boundaryName.v);
            }
            else
            {
                // Get the boundary reference and set the dependency on
                // temperature
                // in the inlet boundary
                boundary = neo.holon.BoundaryGenerator.get(uid);
                inSatDOConc = (HStateDbl) getInitHState(boundary, 
                        stream_metab.doxygen.bound.loaddo.SatDOConc.class.getSimpleName());
            }
        }
        catch (Throwable t)
        {
            // Log an error if inlet boundary holon name is not provided or has
            // nil value
            Logger.logError("Must specify a TempBoundIn in for holon " 
                    + myHolon.getUID().toString());
        }
        outSatDOConc = (HStateDbl)getInitHState(SatDOConc.class.getSimpleName());
    }

}
