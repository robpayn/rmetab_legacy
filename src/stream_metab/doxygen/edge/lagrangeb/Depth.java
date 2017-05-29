package stream_metab.doxygen.edge.lagrangeb;

import neo.holon.*;
import neo.motif.*;
import neo.state.*;

/**
 * Controls the simulated average depth of water over the reach
 * 
 * @author robert.payn
 */
public class Depth extends AbstractUpdaterDbl {

    /**
     * Depth of water in attached patch [Length]
     */
    private HStateDbl depth;

    /**
     * Calculates the current depth
     * 
     * @return depth [Length]
     */
    @Override
    protected double computeValue()
    {
        return depth.v;
    }

    /**
     * Calculates the initial depth
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Defines the dependencies for calculation of depth
     */
    @Override
    protected void setDependencies()
    {
        try 
        {
            myHolon.getHState(Doxygen.Names.DEPTH_WATER);
            depth = (HStateDbl)getInitHState(Doxygen.Names.DEPTH_WATER);
        }
        catch (Throwable t)
        {
            depth = (HStateDbl)getInitHState(((Edge)myHolon).getFrom(), Doxygen.Names.DEPTH_WATER);
        }
    }

}
