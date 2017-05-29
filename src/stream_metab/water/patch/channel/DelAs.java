package stream_metab.water.patch.channel;

import neo.motif.*;
import neo.state.*;
import neo.util.*;

public class DelAs extends AbstractParameterDbl {

    private HStateDbl bankSlope;
    private HStateDbl lengthPatch;

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
                return (2 / Math.tan(bankSlope.v)) * lengthPatch.v;
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
        lengthPatch = (HStateDbl) getInitHState("LENGTH");

    }

}
