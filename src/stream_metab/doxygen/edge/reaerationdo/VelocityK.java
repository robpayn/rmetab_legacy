package stream_metab.doxygen.edge.reaerationdo;

import neo.holon.*;
import neo.motif.*;
import neo.state.*;
import neo.uid.*;
import neo.util.*;
import stream_metab.doxygen.utils.*;

/**
 * Controls the temperature corrected first-order reaeration rate times water
 * depth (piston velocity)
 * <p>
 * <b>References:</b>
 * </p>
 * <ul style="list-style-type: none"> <li
 * style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px"> Jahne, B.,
 * K. O. Munnich, R. Bosinger, A. Dutzi, W. Huber, and P. Libner (1987) On
 * parameters influencing air-water gas exchange. Journal of Geophysical
 * Research 74, 456-464. </li> </ul>
 * 
 * @author robert.payn
 */
public class VelocityK extends AbstractUpdaterDbl {

    /**
     * First-order reaeration rate at a Schmidt number of 600 [Length
     * Time<sup><small>-1</small></sup>]
     */
    private HStateDbl k600;
    
    /**
     * Temperature of water (&deg;C)
     */
    private HStateDbl temp;

    /**
     * Calculate the temperature corrected reaeration rate (Jahne et al. 1987)
     * 
     * @return First-order reaeration rate times depth [Length
     *         Time<sup><small>-1</small></sup>]
     */
    @Override
    protected double computeValue()
    {
        return k600.v * Math.sqrt(600 / (Calculators.schmidtNumberEmpirical(temp.v)));
    }

    /**
     * Calculate the initial temperature corrected reaeration rate
     * 
     * @return First-order reaeration rate times depth [Length
     *         Time<sup><small>-1</small></sup>]
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Define the state dependencies for calculating reaeration rate
     * 
     * @see stream_metab.doxygen.bound.loaddo.TempObs
     */
    @Override
    protected void setDependencies()
    {
        Boundary boundary;
        UniqueID uid;

        // Use temperature from a specified boundary holon if "TempBound" exists
        // as a state and has a value
        // otherwise use the temperature from the attached water holon
        try
        {
            // Attempt to get the state with the tame of the boundary with
            // temperature state
            HStateStr tempBound = (HStateStr) myHolon.getHState(Doxygen.Names.BOUND_NAME_TEMP);

            // Throw a throwable if "TempBound" state is found but doesn't have
            // a value
            if (tempBound.isNil())
            {
                throw new Throwable("Use temperature from water cell");
            }

            // Get the boundary UID with the provided boundary name
            uid = UniqueIDMgr.cast(tempBound.v);

            // Check if UID was found
            if (uid == null)
            {
                // If UID is not found, log an error that the requested boundary
                // holon doesn't exist
                Logger.logError("Holon " + myHolon.getUID().toString() + " cannot find holon " + tempBound.v);
            }
            else
            {
                // If UID is found, get the boundary reference and set the
                // dependency on
                // the temperature state in that boundary
                boundary = neo.holon.BoundaryGenerator.get(uid);
                temp = (HStateDbl) getInitHState(boundary, Doxygen.Names.TEMP_BOUND);
            }
        }
        catch (Throwable t)
        {
            // If TempBound is not present or doesn't have a value, declare a
            // dependency on the
            // temperature state of the attached water cell
            temp = (HStateDbl) getInitHState(((Edge) myHolon).getTo(), Doxygen.Names.TEMP_CELL);
        }

        // Set other dependencies
        k600 = (HStateDbl) getInitHState(Doxygen.Names.K_SCHMIDT_600);
    }

}
