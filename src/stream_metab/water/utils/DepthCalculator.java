package stream_metab.water.utils;

import neo.state.*;

/**
 * Definitions of fields and abstract method necessary to calculate depth in a
 * water edge.
 * 
 * @author robert.payn
 */
public abstract class DepthCalculator {

    /**
     * Depth of water in an associated patch [Length]
     */
    protected HStateDbl depthPatch;
    /**
     * Base elevation (elevation at zero depth) for a given location on the edge
     * [Length]
     */
    protected double elevBase;
    /**
     * Head of water in an associated patch [Length]
     */
    protected HStateDbl headPatch;

    /**
     * Constructs and instance of the depth calculator with references to the
     * data and states needed for calculation.
     * 
     * @param elevBase
     *            - Base elevation (elevation at zero depth) for a given
     *            location on the edge [Length]
     * @param headPatch
     *            - Head of water in an associated patch [Length]
     * @param depthPatch
     *            - Depth of water in an associated patch [Length]
     */
    public DepthCalculator(double elevBase, HStateDbl headPatch, HStateDbl depthPatch)
    {

        this.depthPatch = depthPatch;
        this.elevBase = elevBase;
        this.headPatch = headPatch;

    }

    /**
     * Calculates the depth
     * 
     * @return depth [Length]
     */
    public abstract double calcDepth();

}
