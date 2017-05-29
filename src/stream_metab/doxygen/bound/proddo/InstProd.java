package stream_metab.doxygen.bound.proddo;

import neo.holon.*;
import neo.key.*;
import neo.motif.*;
import neo.state.*;
import neo.util.*;

/**
 * Controls the instantaneous oxygen mass flux due to production from metabolic
 * processes, based on the distribution of photosynthetically active radiation
 * through the day.
 * 
 * @author Rob Payn
 */
public class InstProd extends AbstractUpdaterDbl {

    /**
     * Daily average ratio of oxygen production to PAR [(Mass
     * Length<sup><small>-2</small></sup> Time<sup><small>-1</small></sup>)
     * (Energy Length<sup><small>-2</small></sup>
     * Time<sup><small>-1</small></sup>)<sup><small>-1</small></sup>]
     */
    private HStateDbl pToParRatio;

    /**
     * Instantaneous photosynthetically active radiation (PAR) energy flux
     * [Energy Length<sup><small>-2</small></sup>
     * Time<sup><small>-1</small></sup>]
     */
    private HStateDbl instPAR;

    /**
     * Calculate the oxygen mass flux due to production from metabolic processes
     * 
     * @return Mass flux of oxygen [Mass Length<sup><small>-2</small></sup>
     *         Time<sup><small>-1</small></sup>]
     */
    @Override
    protected double computeValue()
    {
        // Daily production is redistributed to instantaneous values
        // based on the distribution of radiation through daylight hours
        return instPAR.v * pToParRatio.v;
    }

    /**
     * Calculate the initial oxygen mass flux due to production from metabolic
     * processes
     * 
     * @return Mass flux of oxygen [Mass Length<sup><small>-2</small></sup>
     *         Time<sup><small>-1</small></sup>]
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Define the dependencies for calculating oxygen production
     * 
     * @see stream_metab.doxygen.patch.airdo.InstPAR
     */
    @Override
    protected void setDependencies()
    {
        // Get reference to attached patch
        Patch myPatch = ((Boundary) myHolon).getPatch();

        // Find all edges with the resource doxygen and the behavior
        // reaerationdo
        Holon[] reaerationEdges = myPatch.getAttachedHolons(Key.cast(Doxygen.class.getSimpleName()), 
                Doxygen.Names.MOTIF_REAERATION);

        // Check for valid number of edges
        if (reaerationEdges.length == 1)
        {
            // One edge is valid, get the references and set the dependencies
            instPAR = (HStateDbl) getInitHState(((Edge) reaerationEdges[0]).getFrom(), Doxygen.Names.PAR);
        }
        else
        {
            // Anything other than one edge is invalid, log an error
            Logger.logError("Must be one and only one edge connect to " + myPatch.getUID().toString()
                    + " with a doxygen.edge.reaeration behavior.");
        }

        // Get other references and set dependencies
        pToParRatio = (HStateDbl) getInitHState(Doxygen.Names.P_TO_PAR_RATIO);
    }

}
