package stream_metab.water.edge.manning;

import neo.motif.AbstractSimpleCalculatorDbl;
import neo.state.HStateDbl;
import neo.state.HStateNotFoundException;
import neo.util.Logger;

public class C extends AbstractSimpleCalculatorDbl {
    
    private static final double ONE_THIRD = 1.0 / 3.0;
    
    private static final double G_ACC = 9.8067;
    
    private HStateDbl hRadius;
    
    private HStateDbl friction;
    
    @Override
    protected double computeValue()
    {
        return Math.pow(hRadius.v, ONE_THIRD) / (G_ACC * Math.pow(friction.v, 2));
    }

    @Override
    protected double initValue()
    {
        if (isNil())
        {
            return computeValue();
        }
        else
        {
            return ((HStateDbl)myHState).v;
        }
    }

    @Override
    protected void setDependencies()
    {
        try 
        {
            myHolon.getHState("WIELEINT");
            myHolon.getHState("WIELESLOPE");
            try
            {
                hRadius = (HStateDbl)myHolon.getHState(HRadius.class.getSimpleName());
                friction = (HStateDbl)myHolon.getHState(Friction.class.getSimpleName());
            }
            catch (HStateNotFoundException e1)
            {
                Logger.logError("Wiele friction enabled but hydraulic radius or friction is not available.");
            }
        }
        catch (Exception e)
        {
        }
    }

}
