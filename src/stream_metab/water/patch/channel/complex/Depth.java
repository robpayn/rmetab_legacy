package stream_metab.water.patch.channel.complex;

import neo.motif.AbstractUpdaterDbl;
import neo.state.HStateDbl;
import neo.table.TabledInterpolater;
import neo.util.ItemNotFoundException;
import neo.util.Param;

/** Water depth in meters. accounts for prismatic and parallel bank sides. */
public class Depth extends AbstractUpdaterDbl {

    private HStateDbl asmax = null;
    private HStateDbl h2o = null;
    private HStateDbl depth_a = null;
    private HStateDbl depth_b = null;
    private HStateDbl maxvolume = null;
    private double maxVol_1_3 = 0;
    private double[] vect = null;

    public void setDependencies()
    {

        asmax = (HStateDbl) getInitHState("ASMAX");
        h2o = (HStateDbl) getInitHState("WATER");

        if (!Param.interpGeom)
        {
            depth_a = (HStateDbl) getInitHState("DEPTH_A");
            depth_b = (HStateDbl) getInitHState("DEPTH_B");
        }

        maxvolume = (HStateDbl) getInitHState("MAXVOLUME");
    }

    public double initValue()
    {
        try
        {
            vect = TabledInterpolater.getVector("Vol1_3toDepth", myHolon.getUID());
        }
        catch (ItemNotFoundException e)
        {
            initError("Table 'Vol1_3toDepth' not available for interpolation of channel depth from H2O volume.");
        }
        maxVol_1_3 = Math.pow(maxvolume.v, Param.oneThird);
        return computeValue();
    }

    public double computeValue()
    {
        double vol, xtravol, d;

        if (h2o.v <= 0.)
            return 0.;

        if (h2o.v >= maxvolume.v)
        {
            vol = maxvolume.v;
            xtravol = h2o.v - maxvolume.v;
        }
        else
        {
            vol = h2o.v;
            xtravol = 0;
        }

        if (!Param.interpGeom)
            d = depth_a.v * Math.pow(vol, depth_b.v);
        else
            d = TabledInterpolater.getValue(vect, Math.pow(vol, Param.oneThird) / maxVol_1_3);

        d = d + (xtravol / asmax.v);

        return d;

    }

}
