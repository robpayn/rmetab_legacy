package stream_metab.water.bound.negateinflows;

import java.util.ArrayList;
import java.util.List;

import neo.holon.Boundary;
import neo.holon.Patch;
import neo.motif.AbstractHalfRoute;
import neo.motif.AbstractHub;
import neo.motif.MotifNotFoundException;
import neo.state.HStateDbl;
import neo.state.HStateNotFoundException;
import neo.key.*;

/**
 * Return negative of inflows to attached node such that the node's volume stays
 * constant.
 */
public class Water extends AbstractHalfRoute {

    private Patch myCmpt;
    /** the edges providing the supply to our node */
    private HStateDbl[] paths = null;
    private HStateDbl dummy = null;
    private AbstractHub attachedhub;

    public void setDependencies()
    {
        myCmpt = ((Boundary) myHolon).getPatch();
        try
        {
            attachedhub = (AbstractHub) myCmpt.getMotifOfResource(getResource());
        }
        catch (MotifNotFoundException e)
        {
            throw new RuntimeException(getResource() + " hub not found in " + getResource()
                    + " negate inflow boundary " + myHolon.getUID());
        }

        // collect all of our node's "faces"
        // note that this won't work if linkages are dynamic
        /*
         * List<HStateDbl> states = new ArrayList<HStateDbl>(); try { for
         * (Boundary b : myCmpt.getAllBoundaries() ) { if ( b == myHolon )
         * continue; states.add( (HStateDbl)b.getHState(getResource())); dummy =
         * (HStateDbl) getInitHState(b, getResource()); } } catch
         * (HStateNotFoundException e) { throw new RuntimeException(e); } paths
         * = states.toArray(new HStateDbl[0]);
         */
        paths = attachedhub.getPaths().clone();
        for (int i = 0; i < paths.length; i++)
        {
            if (paths[i] != null && paths[i].getPrimaryModifier() != this)
                // This call is just to set the dependency. dummy will be
                // path[i]

                dummy = (HStateDbl) getInitHState(paths[i].getPrimaryModifier().getHolon(), getResource());
            else
                paths[i] = null;

        }
    }

    public double initValue()
    {
        return isNil() ? 0.0 : getValue();
    }

    public double computeValue()
    {
        // long id = myCmpt.getMyUID();
        // long tic = myCmpt.getMyEcoNet().getCurrTick();
        // String nm = myMonad.getMyName();

        double v = 0.;
        for (int i = 0; i < paths.length; i++)
            if (paths[i] != null)
                v -= paths[i].getValue();

        return v;
    }
}
