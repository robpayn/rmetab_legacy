package stream_metab.water.edge.culvert;

import stream_metab.water.utils.DepthCalculator;
import stream_metab.water.utils.culvert.*;
import neo.holon.*;
import neo.motif.*;
import neo.state.*;
import neo.util.Logger;

/**
 * Controls the depth of water in the culvert at the "from" side of the edge.
 * 
 * @author robert.payn
 */
public class DepthFrom extends AbstractUpdaterDbl {

    /**
     * Calculator for the depth (will be implemented with high side or low side
     * algorithms)
     */
    private DepthCalculator depthCalc;
    /**
     * Depth of water in patch on "from" side of culvert
     */
    private HStateDbl depthFromPatch;
    /**
     * Elevation of base of culvert on "from" side of edge
     */
    private HStateDbl elevFrom;
    /**
     * Elevation of base of culvert on "to" side of edge
     */
    private HStateDbl elevTo;
    /**
     * Hydraulic head in patch on "from" side of culvert
     */
    private HStateDbl headFromPatch;
    /**
     * Elevation of bed in "from" patch [Length]
     */
    private HStateDbl zBedFrom = null;
    /**
     * Elevation of bed in "from" patch [Length]
     */
    private HStateDbl zBedTo = null;

    /**
     * Calculate the depth on the from side of the culvert
     * 
     * @return depth of water [Length]
     */
    @Override
    protected double computeValue()
    {

        return depthCalc.calcDepth();

    }

    /**
     * Calculate the initial depth of water on the from side of culvert.
     * <p>
     * Creates and assigns the appropriate depth calculator object, based on
     * whether the from side of the culvert has a higher or lower elevation than
     * the to side.
     * </p>
     */
    @Override
    protected double initValue()
    {

        double hiElevCulvert = Math.max(elevFrom.v, elevTo.v);
        double meanElevPatch = (zBedFrom.v + zBedTo.v) / 2;
        if (hiElevCulvert < meanElevPatch)
        {
            Logger.logError("Elevation of the culvert base (" + Double.toString(hiElevCulvert)
                    + ") at high end of the culvert is less than the mean bed elevation of adjacent patches ("
                    + Double.toString(meanElevPatch) + ").");
        }
        if (elevFrom.v > elevTo.v)
        {
            // From side has higher elevation
            depthCalc = new DepthCalcHigh(elevFrom.v, headFromPatch, depthFromPatch);
        }
        else
        {
            // From side has lower elevation
            depthCalc = new DepthCalcLow(elevFrom.v, headFromPatch, depthFromPatch);
        }
        return computeValue();

    }

    /**
     * Defines the state dependencies for calculating depth at the "from" end of
     * the culvert
     */
    @Override
    protected void setDependencies()
    {

        depthFromPatch = (HStateDbl) getInitHState(((Edge) myHolon).getFrom(), "DEPTH");
        elevFrom = (HStateDbl) getInitHState("ELEVFROM");
        elevTo = (HStateDbl) getInitHState("ELEVTO");
        headFromPatch = (HStateDbl) getInitHState(((Edge) myHolon).getFrom(), "HEAD");
        zBedFrom = (HStateDbl) getInitHState(((Edge) myHolon).getFrom(), "ZBOT");
        zBedTo = (HStateDbl) getInitHState(((Edge) myHolon).getTo(), "ZBOT");

    }

}
