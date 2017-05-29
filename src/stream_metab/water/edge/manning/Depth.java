package stream_metab.water.edge.manning;

import neo.holon.Edge;
import neo.holon.Holon;
import neo.motif.AbstractUpdaterDbl;
import neo.state.HStateDbl;

public class Depth extends AbstractUpdaterDbl {

    private HStateDbl t_head = null;
    private HStateDbl f_head = null;
    private HStateDbl t_water = null;
    private HStateDbl f_water = null;
    private HStateDbl f_depth = null;
    private HStateDbl t_depth = null;
    private HStateDbl zval = null;
    private HStateDbl boundfrac = null;

    private Holon frHolon;
    private Holon toHolon;

    public void setDependencies()
    {
        frHolon = ((Edge) myHolon).getFrom();
        toHolon = ((Edge) myHolon).getTo();

        zval = (HStateDbl) getInitHState("CHANNELZ");
        boundfrac = (HStateDbl) getInitHState("BoundaryFrac");
        f_head = (HStateDbl) getInitHState(frHolon, "HEAD");
        t_head = (HStateDbl) getInitHState(toHolon, "HEAD");
        f_water = (HStateDbl) getInitHState(frHolon, "WATER");
        t_water = (HStateDbl) getInitHState(toHolon, "WATER");
        f_depth = (HStateDbl) getInitHState(frHolon, "DEPTH");
        t_depth = (HStateDbl) getInitHState(toHolon, "DEPTH");

    }

    public double initValue()
    {
        return computeValue();
    }

    public double computeValue()
    {

        if (f_water.v <= 0 && t_water.v <= 0)
            return 0;

        // determine straight-line depth between heads
        double avgd = ((1.0 - boundfrac.v) * f_head.v + boundfrac.v * t_head.v) - zval.v;

        // straight-line depth should never be deeper than depth of source
        // patch, so
        // determine source head
        double sh;
        double sd;
        double sfrac;

        if (f_head.v > t_head.v)
        {
            sh = f_head.v;
            sd = f_depth.v;
            sfrac = (1.0 - boundfrac.v);
        }
        else
        {
            sh = t_head.v;
            sd = t_depth.v;
            sfrac = boundfrac.v;
        }

        // determine flow depth from source patch, and make sure straight-line
        // depth
        // is not greater
        sd = Math.min(sd, sh - zval.v);
        avgd = Math.min(sd, avgd);

        // determine if source head alone is sufficient to cause flow (e.g. into
        // dry channel).
        // If distance weighting (determined by sfrac) is too large, model
        // instability can
        // result, so only make this check when distance weighting is <0.9. When
        // distance
        // weighting is >0.9, avgd is sufficient to force flow into a dry
        // channel.
        sd = (sfrac < 0.9) ? sd * (sfrac) : 0;

        // Use the deepest of avgd, sd, or 0;
        double d = Math.max(sd, Math.max(avgd, 0));

        return d;
    }

}
