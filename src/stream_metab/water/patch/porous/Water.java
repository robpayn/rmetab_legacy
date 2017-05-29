package stream_metab.water.patch.porous;

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
 * <p>
 * The constructor installs required dynamic property objects (via
 * AbstractMonadIdentity) in the specific update order. In other words,
 * calculation of some properties are sequentially dependent such that to be
 * properly update, the HProperty's update() methods must be called in a
 * specific order.
 */

public class Water extends AbstractHub {

    private HStateDbl inihead = null;
    private HStateDbl ztop = null;
    private HStateDbl zbot = null;
    private HStateDbl porosity = null;
    private HStateDbl secstore = null;
    private HStateDbl surfarea = null;
    private HStateDbl fieldcap = null;

    public void setDependencies()
    {

        inihead = (HStateDbl) getInitHState("INIHEAD");
        ztop = (HStateDbl) getInitHState("ZTOP");
        zbot = (HStateDbl) getInitHState("ZBOT");
        porosity = (HStateDbl) getInitHState("POROSITY");
        secstore = (HStateDbl) getInitHState("SECSTORE");
        surfarea = (HStateDbl) getInitHState("SURFAREA");
        fieldcap = (HStateDbl) getInitHState("FIELDCAP");
    }

    public double initValue()
    {
        return (Utility.groundHeadToVol(inihead.v, ztop.v, zbot.v, porosity.v, secstore.v, surfarea.v, fieldcap.v));
    }

}
