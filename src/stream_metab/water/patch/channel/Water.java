package stream_metab.water.patch.channel;

import neo.motif.AbstractHub;
import neo.state.HStateDbl;
import stream_metab.water.Utility;

/**
 * This is the class loaded by the Monad (Reservoir) to act as its identity by
 * which required properties are updated. This class also provides an
 * initIdentity() method that allows the Monad to be set to an initial value.
 * Afterward, the associated properties' update() methods are called in proper
 * sequence to initialize everything with the starting values.
 * <p>
 * The constructor installs required dynamic property objects (via
 * AbstractMonadIdentity) in the specific update order. In other words,
 * calculation of some properties are sequentially dependent such that to be
 * properly update, the HProperty's update() methods must be called in a
 * specific order.
 * </p>
 */
public class Water extends AbstractHub {

    HStateDbl inihead = null;
    HStateDbl ztop = null;
    HStateDbl zbot = null;
    HStateDbl as = null;
    HStateDbl delas = null;
    HStateDbl asmax = null;

    protected void setBalance()
    {
        balanceFlag = true;
    }

    /**
     * Set monad value of surface water values with initial head value read from
     * INIHEAD field of input file.
     */
    public void setDependencies()
    {

        inihead = (HStateDbl) getInitHState("INIHEAD");
        ztop = (HStateDbl) getInitHState("ZTOP");
        zbot = (HStateDbl) getInitHState("ZBOT");
        as = (HStateDbl) getInitHState("AS");
        delas = (HStateDbl) getInitHState("DELAS");
        asmax = (HStateDbl) getInitHState("ASMAX");

    }

    public double initValue()
    {
        return (Utility.surfaceHeadToVol(inihead.v, ztop.v, zbot.v, as.v, delas.v, asmax.v));
    }

}
