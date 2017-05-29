package stream_metab.doxygen.patch.channeldo;

import neo.holon.*;
import neo.motif.*;
import neo.state.*;
import neo.uid.*;
import neo.util.*;

/**
 * Controls the oxygen saturation deficit in the surface water cell
 * 
 * @author Administrator
 */
public class SatDODef extends AbstractUpdaterDbl {

    /**
     * Name of init field for specifying a boundary from which the DO concentration
     * should be obtained
     */
    public static final String DO_CONC_BOUND = "DOCONCBOUND";
    
    /**
     * DO concentration in the cell [Mass Length<sup><small>-3</small></sup>]
     */
    private HStateDbl dOConc;
    /**
     * DO saturation concentration in the cell [Mass
     * Length<sup><small>-3</small></sup>]
     */
    private HStateDbl satDOConc;

    /**
     * Calculate the DO saturation deficit
     * 
     * @return Saturation deficit [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double computeValue()
    {
        return satDOConc.v - dOConc.v;
    }

    /**
     * Calculate the initial DO saturation deficit
     * 
     * @return Saturation deficit [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Define the state dependencies for calculating saturation deficit.
     * 
     * @see DOConc
     * @see stream_metab.doxygen.bound.loaddo.DOConc
     */
    @Override
    protected void setDependencies()
    {
        Boundary boundary;
        UniqueID uid;

        // Use DOConc from a specified boundary holon if "DOConcBound" exists as
        // a state and has a value
        // otherwise use the DOConc from the attached water holon
        try
        {
            // Attempt to get the state with the name of the boundary with
            // temperature state
            HStateStr dOConcBound = (HStateStr) myHolon.getHState(DO_CONC_BOUND);

            // Throw a throwable (caught below) if "DOConcBound" state is found
            // but doesn't have a value
            if (dOConcBound.isNil())
            {
                throw new Throwable("Use DOConc from water cell");
            }

            // Get the boundary UID with the provided boundary name
            uid = UniqueIDMgr.cast(dOConcBound.v);

            // Check if UID was found
            if (uid == null)
            {
                // If UID is not found, log an error that the requested boundary
                // holon doesn't exist
                Logger.logError("Holon " + myHolon.getUID().toString() 
                        + " cannot find holon " + dOConcBound.v);
            }
            else
            {
                // If UID is found, get the boundary reference and set the
                // dependency on
                // the temperature state in that boundary
                boundary = neo.holon.BoundaryGenerator.get(uid);
                dOConc = (HStateDbl) getInitHState(boundary, 
                        stream_metab.doxygen.bound.loaddo.DOConc.class.getSimpleName());
            }
        }
        catch (Throwable t)
        {
            // If DOConcBound is not present or doesn't have a value, declare a
            // dependency on the
            // DOConc state in the present cell
            dOConc = (HStateDbl) getInitHState(DOConc.class.getSimpleName());
        }

        // Set other dependencies
        satDOConc = (HStateDbl) getInitHState(SatDOConc.class.getSimpleName());
    }

}
