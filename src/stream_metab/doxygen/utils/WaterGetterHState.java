package stream_metab.doxygen.utils;

import neo.state.HStateDbl;

public class WaterGetterHState implements WaterGetter {
    
    private HStateDbl state;
    
    public WaterGetterHState(HStateDbl state)
    {
        this.state = state;
    }

    @Override
    public double getValue()
    {
        return state.v;
    }
    
    

}
