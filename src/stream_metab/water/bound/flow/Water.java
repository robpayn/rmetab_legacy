package stream_metab.water.bound.flow;

import neo.motif.AbstractHalfRoute;
import neo.state.HStateStr;
import neo.table.FileInterpolater;
import neo.table.InterpolaterFactory;
import neo.util.Scheduler;

/**
 * reads time-stamped volumetric flow values from a file, interpolates flow at a
 * given time, and presents it directly to the boundary holon.
 */

public class Water extends AbstractHalfRoute {

    private FileInterpolater interp = null;

    protected void setDependencies()
    {

        try
        {

            HStateStr infile = (HStateStr) getInitHState("FLOWFILE");
            interp = InterpolaterFactory.create(infile.v);

        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    protected double initValue()
    {
        // return isNil() ? 0.0 : getValue();
        return computeValue();
    }

    protected double computeValue()
    {

        return interp.getValue(Scheduler.getCurrentTime());
    }

}
