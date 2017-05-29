package stream_metab.doxygen.patch.channeldo;

import neo.holon.*;
import neo.motif.*;
import neo.state.*;
import neo.uid.*;
import neo.util.*;

/**
 * Controls the temperature of water in the cell
 * 
 * @author Administrator
 */
public class Temperature extends AbstractUpdaterDbl {

    /**
     * Temperature at the overall inlet boundary (&deg;C)
     */
    private HStateDbl avgTemp;
    
    /**
     * Calculate the temperature of water in the cell
     * 
     * @return Temperature (&deg;C)
     */
    @Override
    protected double computeValue()
    {
        // Temperature is estimated as the average of the inlet and outlet
        // temperatures
        return avgTemp.v;
    }

    /**
     * Calculate the initial temperature of water in the cell
     * 
     * @return Temperature (&deg;C)
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Define the state dependencies for calculating temperature
     * 
     * @see stream_metab.doxygen.bound.loaddo.TempObs
     */
    @Override
    protected void setDependencies()
    {
        Edge edge;
        UniqueID uid;

        // Average temperature
        try
        {
            // Get the initialization table value that has the name of the
            // outlet boundary holon
            HStateStr edgeName = (HStateStr) myHolon.getHState(Doxygen.Names.TEMP_AVG_EDGE);

            // Get the unique id of the outlet boundary holon
            uid = UniqueIDMgr.cast(edgeName.v);

            if (uid == null)
            {
                // Log an error if outlet holon is not found
                Logger.logError("Holon " + myHolon.getUID().toString() 
                        + " cannot find holon " + edgeName.v);
            }
            else
            {
                // Get the boundary reference and set the dependency on
                // temperature
                // in the outlet boundary
                edge = neo.holon.EdgeGenerator.get(uid);
                avgTemp = (HStateDbl) getInitHState(edge, Doxygen.Names.TEMP_AVG);
            }
        }
        catch (Throwable t)
        {
            // Log an error if outlet boundary holon name is not provided or has
            // nil value
            Logger.logError("Must specify a TempBoundOut in for holon " + myHolon.getUID().toString());
        }
    }

}
