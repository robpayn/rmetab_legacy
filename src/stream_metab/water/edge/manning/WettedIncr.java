package stream_metab.water.edge.manning;

import neo.motif.*;
import neo.state.*;
import neo.util.*;

public class WettedIncr extends AbstractParameterDbl {

    private HStateDbl bankSlope;

    @Override
    protected double initValue()
    {

        if (isNil())
        {
            if (bankSlope.v < 0)
            {
                return 0.0;
            }
            else if (bankSlope.v > Math.PI / 2)
            {
                Logger.logError("Invalid bank slope.");
                return 0.0;
            }
            else
            {
                return (2 / Math.tan(bankSlope.v));
            }
        }
        else
        {
            return getValue();
        }

    }

    @Override
    protected void setDependencies()
    {

        bankSlope = (HStateDbl) getInitHState("BANKSLOPE");

    }

}
