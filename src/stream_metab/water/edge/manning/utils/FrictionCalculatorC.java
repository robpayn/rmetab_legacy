package stream_metab.water.edge.manning.utils;

import neo.state.HStateDbl;

public class FrictionCalculatorC extends FrictionCalculator {
    
    private HStateDbl c;

    public FrictionCalculatorC(HStateDbl c)
    {
        this.c = c;
    }

    @Override
    public double calculate()
    {
        return c.v;
    }

}
