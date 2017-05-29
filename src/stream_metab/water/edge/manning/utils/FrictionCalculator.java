package stream_metab.water.edge.manning.utils;

import neo.holon.Holon;
import neo.state.HStateDbl;
import neo.state.HStateNotFoundException;
import neo.util.Logger;

public abstract class FrictionCalculator {
    
    public static FrictionCalculator createFrictionCalculator(Holon holon)
    {
        try
        {
           holon.getHState("WIELEINT");
           holon.getHState("WIELESLOPE");
           return new FrictionCalculatorWiele(holon);
        }
        catch (Exception e)
        {
            try
            {
                return new FrictionCalculatorC((HStateDbl)holon.getHState("C"));
            }
            catch (HStateNotFoundException e1)
            {
                Logger.logError("Parameters for wiele friction or static friction must be provided.");
                return null;
            }
        }
    }

    public abstract double calculate();
    
}
