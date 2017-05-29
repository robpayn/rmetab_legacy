package stream_metab.water.edge.manning;

import neo.motif.AbstractSimpleCalculatorDbl;
import neo.state.HStateDbl;
import neo.state.HStateNotFoundException;
import neo.util.Logger;

public class Friction extends AbstractSimpleCalculatorDbl {
    
    private HStateDbl hRadius;
    
    private HStateDbl wieleInt;
    
    private HStateDbl wieleSlope;

    @Override
    protected double computeValue()
    {
        return wieleInt.v + wieleSlope.v * Math.log(hRadius.v);
    }

    @Override
    protected double initValue()
    {
        try 
        {
            wieleInt = (HStateDbl)myHolon.getHState("WIELEINT");
            wieleSlope = (HStateDbl)myHolon.getHState("WIELESLOPE");
            return computeValue();
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    @Override
    protected void setDependencies()
    {
        
        try 
        {
            wieleInt = (HStateDbl)myHolon.getHState("WIELEINT");
            wieleSlope = (HStateDbl)myHolon.getHState("WIELESLOPE");
            try
            {
                hRadius = (HStateDbl)myHolon.getHState(HRadius.class.getSimpleName());
            }
            catch (HStateNotFoundException e1)
            {
                Logger.logError("Wiele friction enabled but hydraulic radius not available.");
            }
        }
        catch (Exception e)
        {
        }
    }

}
