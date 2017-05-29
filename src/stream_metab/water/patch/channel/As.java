package stream_metab.water.patch.channel;

import neo.motif.*;
import neo.state.*;

public class As extends AbstractParameterDbl {

    private HStateDbl bankSlope;
    private HStateDbl elevBot;
    private HStateDbl elevTop;
    private HStateDbl lengthPatch;
    private HStateDbl widthAvg;

    @Override
    protected double initValue()
    {

        if (isNil())
        {
            if (bankSlope.v < 0)
            {
                return widthAvg.v * lengthPatch.v;
            }
            else
            {
                return (widthAvg.v - ((elevTop.v - elevBot.v) / Math.tan(bankSlope.v))) * lengthPatch.v;
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
        elevBot = (HStateDbl) getInitHState("ZBOT");
        elevTop = (HStateDbl) getInitHState("ZTOP");
        lengthPatch = (HStateDbl) getInitHState("LENGTH");
        widthAvg = (HStateDbl) getInitHState("WIDTHAVG");

    }

}
