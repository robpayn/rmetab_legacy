package stream_metab.water.edge.manningcalib;

import neo.motif.*;
import neo.state.*;

public class VelocityObserved extends AbstractUpdaterDbl {

    private HStateDbl waterObserved;
    private HStateDbl qVelInt;
    private HStateDbl qVelSlope;
    private HStateDbl reachLength;
    
    @Override
    protected double computeValue()
    {
        return reachLength.v / (qVelSlope.v * waterObserved.v + qVelInt.v);
//        return qVelSlope.v * waterObserved.v + qVelInt.v;
    }

    @Override
    protected double initValue()
    {
        return computeValue();
    }

    @Override
    protected void setDependencies()
    {
        waterObserved = (HStateDbl)getInitHState(WaterObserved.class.getSimpleName());
        qVelInt = (HStateDbl)getInitHState("QVELINT");
        qVelSlope = (HStateDbl)getInitHState("QVELSLOPE");
        reachLength = (HStateDbl)getInitHState("REACHLENGTH");
    }

    
    
}
