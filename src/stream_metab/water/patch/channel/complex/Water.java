package stream_metab.water.patch.channel.complex;

import neo.motif.AbstractHub;
import neo.state.HStateDbl;
import neo.table.TabledInterpolater;
import neo.util.ItemNotFoundException;
import neo.util.Param;

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
    HStateDbl depth_a = null;
    HStateDbl depth_b = null;
    HStateDbl zcoord = null;
    HStateDbl maxvolume = null;
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

        if (Param.WDWBM)
        {
            initFatal("WDWBM flag in 'util.Param' class must = FALSE before "
                    + "using 'channel.complex' patches in the water package.");
        }

        inihead = (HStateDbl) getInitHState("INIHEAD");

        if (!Param.interpGeom)
        {
            depth_a = (HStateDbl) getInitHState("DEPTH_A");
            depth_b = (HStateDbl) getInitHState("DEPTH_B");
        }

        zcoord = (HStateDbl) getInitHState("ZBOT");
        maxvolume = (HStateDbl) getInitHState("MAXVOLUME");
        asmax = (HStateDbl) getInitHState("ASMAX");

    }

    public double initValue()
    {
        double v, maxd;
        double[] vect;

        double d = Math.max(0, inihead.v - zcoord.v);
        if (d <= 0.0)
        {
            return 0;
        }

        if (!Param.interpGeom)
        {
            maxd = depth_a.v * Math.pow(maxvolume.v, depth_b.v);
            if (d > maxd)
                v = maxvolume.v + (d - maxd) * asmax.v;
            else
                v = Math.pow((d / depth_a.v), (1 / depth_b.v));
        }
        else
        {
            try
            {
                vect = TabledInterpolater.getVector("Vol1_3toDepth", myHolon.getUID());
            }
            catch (ItemNotFoundException e)
            {
                initError("Table 'Vol1_3toDepth' not available for initialization of water volume from depth");
                return 0;
            }
            maxd = vect[vect.length - 1];
            if (d > maxd)
                v = maxvolume.v + (d - maxd) * asmax.v;
            else
            {
                int i;
                double frac;
                for (i = 1; i < vect.length; i++)
                {
                    if (vect[i] >= d)
                    {
                        break;
                    }
                }
                frac = (d - vect[i - 1]) / (vect[i] - vect[i - 1]);
                frac = Math.pow(maxvolume.v, Param.oneThird) * ((double) i - 1 + frac) / ((double) vect.length - 1);
                v = Math.pow(frac, 3);
            }
        }
        return v;
    }

}
