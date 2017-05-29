package stream_metab.doxygen.patch.channeldo;

import neo.holon.*;
import neo.motif.*;
import neo.state.*;
import neo.uid.*;
import neo.util.*;

/**
 * Controls the saturated dissolved oxygen concentration at a given water
 * temperature and atmospheric pressure
 * 
 * @author Rob Payn
 */
public class SatDOConc extends AbstractUpdaterDbl {

    /**
     * Average DO saturation concentration over the reach
     * [Mass Length<sup><small>-3</small></sup>]
     */
    private HStateDbl avgSatConc;

    /**
     * Calculate the DO saturation concentration
     * 
     * @return DO concentration [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double computeValue()
    {
        // Saturation concentration is estimated as the average of the
        // overall inlet and outlet boundary values
        return avgSatConc.v;
    }

    /**
     * Calculate the initial DO saturation concentration
     * 
     * @return DO concentration [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Define the state dependencies for calculating DO saturation concentration
     * 
     * @see stream_metab.doxygen.bound.loaddo.SatDOConc
     */
    @Override
    protected void setDependencies()
    {
        Edge edge;
        UniqueID uid;

        // Average saturation concentration
        try
        {
            // Get the initialization table value that has the name of the
            // outlet boundary holon
            HStateStr edgeName = 
                (HStateStr) myHolon.getHState(Doxygen.Names.SAT_DO_CONC_AVG_EDGE);

            // Get the unique id of the outlet boundary holon
            uid = UniqueIDMgr.cast(edgeName.v);

            if (uid == null)
            {
                // Log an error if outlet holon is not found
                Logger.logError("Holon " + myHolon.getUID().toString() + 
                        " cannot find holon " + edgeName.v);
            }
            else
            {
                // Get the boundary reference and set the dependency on
                // saturated DO concentration
                // in the outlet boundary
                edge = neo.holon.EdgeGenerator.get(uid);
                avgSatConc = (HStateDbl) getInitHState(edge, Doxygen.Names.SAT_DO_CONC_AVG);
            }
        }
        catch (Throwable t)
        {
            // Log an error if outlet boundary holon name is not provided or has
            // nil value
            Logger.logError("Must specify a DOConcBoundOut in for holon " 
                    + myHolon.getUID().toString());
        }
    }

}
