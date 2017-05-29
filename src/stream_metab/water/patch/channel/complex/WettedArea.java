package stream_metab.water.patch.channel.complex;

import neo.motif.AbstractUpdaterDbl;
import neo.state.HStateDbl;
import neo.table.TabledInterpolater;
import neo.util.ItemNotFoundException;
import neo.util.Param;

/** compute wetted surface area (plan view) with current depth. */
public class WettedArea extends AbstractUpdaterDbl {

    private HStateDbl asmax = null;
    private HStateDbl water = null;
    private HStateDbl wetarea_a = null;
    private HStateDbl wetarea_b = null;
    private HStateDbl maxvolume = null;

    private double[] vect;
    private double maxVol_1_3;

    private boolean planar;

    public void setDependencies()
    {

        asmax = (HStateDbl) getInitHState("ASMAX");
        water = (HStateDbl) getInitHState("WATER");

        if (!Param.interpGeom)
        {
            wetarea_a = (HStateDbl) getInitHState("WETAREA_A");
            wetarea_b = (HStateDbl) getInitHState("WETAREA_B");
        }

        maxvolume = (HStateDbl) getInitHState("MAXVOLUME");

    }

    public double initValue()
    {
        try
        {
            vect = TabledInterpolater.getVector("Vol1_3toInundArea", myHolon.getUID());
        }
        catch (ItemNotFoundException e)
        {
            initError("Table 'Vol1_3toInundArea' not available for interpolation of wetted area from H2O volume.");
        }
        maxVol_1_3 = Math.pow(maxvolume.v, Param.oneThird);
        return computeValue();
    }

    public double computeValue()
    {

        if (water.v <= 0)
            return 0.;
        else if (water.v >= maxvolume.v)
            return asmax.v;
        else
        {
            double wa;
            if (!Param.interpGeom)
                wa = Math.min(wetarea_a.v * water.v / (wetarea_b.v + water.v), asmax.v);
            else
                wa = TabledInterpolater.getValue(vect, Math.pow(water.v, Param.oneThird) / maxVol_1_3);
            return wa;
        }
    }
}
