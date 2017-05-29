package stream_metab.water.edge.manning;

import neo.motif.*;
import neo.state.*;

public class BtmWth extends AbstractParameterDbl {

    private HStateDbl bankSlope;
    private HStateDbl depthActive;
    private HStateDbl widthAvg;

    @Override
    protected double initValue()
    {

        if (isNil())
        {
            if (bankSlope.v < 0)
            {
                return widthAvg.v;
            }
            else
            {
                return widthAvg.v - ((depthActive.v) / Math.tan(bankSlope.v));
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
        depthActive = (HStateDbl) getInitHState("DEPTHACTIVE");
        widthAvg = (HStateDbl) getInitHState("WIDTHAVG");

    }

}
