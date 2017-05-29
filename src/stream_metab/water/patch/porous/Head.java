package stream_metab.water.patch.porous;

import neo.holon.Boundary;
import neo.holon.Edge;
import neo.holon.Holon;
import neo.holon.Patch;
import neo.key.Key;
import neo.motif.AbstractUpdaterDbl;
import neo.motif.Motif;
import neo.motif.MotifNotFoundException;
import neo.state.HStateDbl;
import neo.util.Logger;
import neo.util.Param;

/**
 * Water state as 'head' in the compartment, units are meters of elevation.
 * Allows for water table via unsaturated flow. If water vol < field capacity,
 * set head to value in node physically below it unless this is the bottom node,
 * then set head to ZBOT. Requires vertical nodes to be aligned within 1 cm
 * according to xcoord,ycoord.
 */
public class Head extends AbstractUpdaterDbl {

    private HStateDbl ztop = null;
    // private HStateDbl psiair = null;
    // private HStateDbl porendx = null;
    private HStateDbl h2o = null;
    // private HStateDbl maxh2o = null;
    private HStateDbl secstore = null;
    private HStateDbl grossvolume = null;
    private HStateDbl porosity = null;
    private HStateDbl fieldcap = null;
    private HStateDbl zbot = null;
    private HStateDbl surfarea = null;
    private HStateDbl n_head = null;
    private HStateDbl zcoord = null;
    private HStateDbl inihead = null;

    // private double poreNdxInv;
    // private double secStoreVolInv;

    private double saturatedVolume;
    private double fieldcapVolume;
    // private double satNumerTerm;
    private double satDenomTerm;
    // private double unsatNumerTerm;
    private double unsatDenomTerm;
    private double effectivePorosity;

    private Patch nextNode;

    public void setDependencies()
    {

        zbot = (HStateDbl) getInitHState("ZBOT");
        ztop = (HStateDbl) getInitHState("ZTOP");
        // psiair = (HStateDbl) myHolon.getInitHState( "PSIAIR");
        // porendx = (HStateDbl) getInitHState( "PORENDX" );
        h2o = (HStateDbl) getInitHState("WATER");
        // maxh2o = (HStateDbl) myHolon.getInitHState( "MAXH2O");
        secstore = (HStateDbl) getInitHState("SECSTORE");
        grossvolume = (HStateDbl) getInitHState("GROSSVOLUME");
        porosity = (HStateDbl) getInitHState("POROSITY");
        fieldcap = (HStateDbl) getInitHState("FIELDCAP");
        surfarea = (HStateDbl) getInitHState("SURFAREA");
        zcoord = (HStateDbl) getInitHState("ZCOORD");
        inihead = (HStateDbl) getInitHState("INIHEAD");
    }

    public double initValue()
    {

        // assert porendx.v >0:"PORENDX val <= 0";
        assert secstore.v > 0 : "SECSTOREVOL val <= 0";

        // poreNdxInv = 1. / porendx.v;
        // secStoreVolInv = 1. / (secstore.v * grossvolume.v);
        saturatedVolume = grossvolume.v * porosity.v;
        fieldcapVolume = grossvolume.v * fieldcap.v;
        effectivePorosity = porosity.v - fieldcap.v;

        // unsatNumerTerm = (zbot.v * porosity.v - ztop.v * fieldcap.v)
        // * surfarea.v;
        unsatDenomTerm = effectivePorosity * surfarea.v;

        // satNumerTerm = (porosity.v * zbot.v
        // + secstore.v * ztop.v
        // - porosity.v * ztop.v)
        // * surfarea.v;
        // satNumerTerm = ( secstore.v * ztop.v -
        // (ztop.v - zbot.v) * porosity.v )
        // * surfarea.v;
        satDenomTerm = secstore.v * grossvolume.v;

        double t_x = ((HStateDbl) getInitHState("XCOORD")).getValue();
        double t_y = ((HStateDbl) getInitHState("YCOORD")).getValue();
        double t_zbot = zbot.v;

        for (Boundary e : ((Patch) myHolon).getAllBoundaries())
        {
            if (!(e instanceof Edge))
                continue;
            // skip connections that aren't vdarcy
            try
            {
                Motif nm = e.getMotifOfResource(Key.cast("water"));
                // String nameofflux = nm.getBundleName().toString();
                if (!nm.getBundleName().toString().equals("vdarcy"))
                    continue;
            }
            catch (ClassCastException e1)
            {
                Logger.logError("SERIOUS ERROR: RESOURCE WATER NOT FOUND.");
            }
            catch (MotifNotFoundException e1)
            {
                Logger.logWarn(2, "<" + this + ">:" + e1);
                continue;
            }

            Patch p = ((Edge) e).getConnected().getPatch();
            if (p == null)
                continue;
            double f_x = ((HStateDbl) getInitHState(p, "XCOORD")).getValue();
            double f_y = ((HStateDbl) getInitHState(p, "YCOORD")).getValue();
            double f_zbot = ((HStateDbl) getInitHState(p, "ZBOT")).getValue();
            // is to-node directly under us?
            if (Math.abs(f_x - t_x) < 0.01 && Math.abs(f_y - t_y) < 0.01 && f_zbot < t_zbot)
            {
                nextNode = p;
                break;
            }
        }

        if (nextNode != null)
        {
            try
            {
                n_head = (HStateDbl) getInitHState(nextNode, "INIHEAD");
            }
            catch (Exception e)
            {
                initError(e.toString());
                return Param.EMPTYDBL;
            }
        }

        // return computeValue() ;
        double v;
        if (h2o.v > saturatedVolume)
            v = ztop.v + (h2o.v - saturatedVolume) / satDenomTerm;
        else if (h2o.v <= saturatedVolume && h2o.v > fieldcapVolume)
            v = zbot.v + (h2o.v - fieldcapVolume) / unsatDenomTerm;
        else
            // if (h2o.v <= fieldcapVolume )
            v = (nextNode == null) ? zbot.v : n_head.v;
        return v;

    }

    public double computeValue()
    {

        // if ("GP0532".equals(myHolon.getUID().toString())) {
        // System.err.println("WHEE");
        // }

        double v;
        if (h2o.v > saturatedVolume)
            v = ztop.v + (h2o.v - saturatedVolume) / satDenomTerm;
        else if (h2o.v <= saturatedVolume && h2o.v > fieldcapVolume)
            v = zbot.v + (h2o.v - fieldcapVolume) / unsatDenomTerm;
        else
            // if (h2o.v <= fieldcapVolume )
            v = (nextNode == null) ? zbot.v : ((HStateDbl) getInitHState(nextNode, "HEAD")).v;
        return v;
    }

}
