package stream_metab.water.edge.manning.utils;

import stream_metab.water.edge.manning.C;
import stream_metab.water.edge.manning.Friction;
import neo.holon.Holon;
import neo.motif.AbstractSimpleCalculatorDbl;
import neo.state.HStateDbl;
import neo.state.HStateNotFoundException;
import neo.util.Logger;

public class FrictionCalculatorWiele extends FrictionCalculator {
    
    private AbstractSimpleCalculatorDbl friction;
    private HStateDbl chezeyState;
    private AbstractSimpleCalculatorDbl chezey;

    public FrictionCalculatorWiele(Holon holon)
    {
        try
        {
            this.friction = (AbstractSimpleCalculatorDbl)holon.getHState(
                    Friction.class.getSimpleName()).getPrimaryModifier();
            this.chezeyState = (HStateDbl)holon.getHState(C.class.getSimpleName());
            this.chezey = (AbstractSimpleCalculatorDbl)chezeyState.getPrimaryModifier();
        }
        catch (HStateNotFoundException e)
        {
            Logger.logError("Wiele friction calculator cannot find states.");
        }
    }

    @Override
    public double calculate()
    {
        friction.calculate();
        chezey.calculate();
        return chezeyState.v;
    }

}
