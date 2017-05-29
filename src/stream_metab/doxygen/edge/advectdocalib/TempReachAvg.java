package stream_metab.doxygen.edge.advectdocalib;

import neo.holon.*;
import neo.motif.*;
import neo.state.*;
import neo.uid.*;
import neo.util.*;

/**
 * Controls the average temperature over a reach based on
 * observed upstream and downstream temperatures.
 * 
 * @author robert.payn
 */
public class TempReachAvg extends AbstractUpdaterDbl {

    /**
     * Temperature at the overall inlet boundary (&deg;C)
     */
    private HStateDbl inTemp;
    
    /**
     * Temperature at the overall outlet boundary (&deg;C)
     */
    private HStateDbl outTemp;

    /**
     * Calculates the average of upstream and downstream temperatures
     */
    @Override
    protected double computeValue()
    {
        // Temperature is estimated as the average of the inlet and outlet
        // temperatures
        return (inTemp.v + outTemp.v) * 0.5;
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
            HStateStr boundaryName = (HStateStr) myHolon.getHState(Doxygen.Names.TEMP_IN);

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
                inTemp = (HStateDbl) getInitHState(boundary, 
                        stream_metab.doxygen.bound.loaddo.TempObs.class.getSimpleName());
            }
        }
        catch (Throwable t)
        {
            // Log an error if inlet boundary holon name is not provided or has
            // nil value
            Logger.logError("Must specify a TempBoundIn in for holon " 
                    + myHolon.getUID().toString());
        }
        outTemp = (HStateDbl)getInitHState(TempObs.class.getSimpleName());
    }

}
