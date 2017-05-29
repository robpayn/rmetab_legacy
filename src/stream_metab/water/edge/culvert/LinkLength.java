package stream_metab.water.edge.culvert;

import neo.holon.*;
import neo.motif.*;
import neo.state.*;
import stream_metab.water.*;

/**
 * Controls the horizontal distance between the connected patches
 * <p>
 * <i>Horizontal distance</i> refers to the Euclidean distance on the x-y plane.
 * </p>
 * 
 * @author robert.payn
 */
public class LinkLength extends AbstractParameterDbl {

    /**
     * x coordinate of the from patch [Length]
     */
    HStateDbl coordXFrom = null;
    /**
     * x coordinate of the "to" patch [Length]
     */
    HStateDbl coordXTo = null;
    /**
     * y coordinate of the from patch [Length]
     */
    HStateDbl coordYFrom = null;
    /**
     * y coordinate of the "to" patch [Length]
     */
    HStateDbl coordYTo = null;
    /**
     * z coordinate of the from patch [Length]
     */
    HStateDbl coordZFrom = null;
    /**
     * z coordinate of the "to" patch [Length]
     */
    HStateDbl coordZTo = null;
    /**
     * Patch on the "from" side of edge
     */
    Patch holonFrom = null;
    /**
     * Patch on the "to" side of edge
     */
    Patch holonTo = null;
    /**
     * Distance associated with the "from" patch [Length]
     */
    HStateDbl lengthFrom = null;
    /**
     * Distance associated with the "to" patch [Length]
     */
    HStateDbl lengthTo = null;

    /**
     * Calculate the initial distance between connected patches
     * 
     * @return Distance between patches (Euclidean distance on horizontal plan)
     *         [Length]
     */
    public double initValue()
    {

        if (lengthFrom.v == 0 && lengthTo.v == 0)
        {
            return Utility.getLinkLengthHoriz(coordXFrom.v, coordYFrom.v, coordZFrom.v, coordXTo.v, coordYTo.v,
                    coordZTo.v);
        }
        else if (lengthFrom.v > 0 && lengthTo.v > 0)
        {
            return lengthFrom.v + lengthTo.v;
        }
        else
        {
            initError(" FromLength & ToLength improperly defined.");
            return 0.0;
        }

    }

    /**
     * Defines the state dependencies for calculation of link length
     */
    public void setDependencies()
    {

        holonFrom = ((Edge) myHolon).getFrom();
        holonTo = ((Edge) myHolon).getTo();
        lengthFrom = (HStateDbl) getInitHState("FromLen");
        lengthTo = (HStateDbl) getInitHState("ToLen");
        coordXFrom = (HStateDbl) getInitHState(holonFrom, "XCOORD");
        coordYFrom = (HStateDbl) getInitHState(holonFrom, "YCOORD");
        coordZFrom = (HStateDbl) getInitHState(holonFrom, "ZBOT");
        coordXTo = (HStateDbl) getInitHState(holonTo, "XCOORD");
        coordYTo = (HStateDbl) getInitHState(holonTo, "YCOORD");
        coordZTo = (HStateDbl) getInitHState(holonTo, "ZBOT");

    }

}
