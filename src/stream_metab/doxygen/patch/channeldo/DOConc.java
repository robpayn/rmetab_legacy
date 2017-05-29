package stream_metab.doxygen.patch.channeldo;

import java.io.IOException;

import stream_metab.doxygen.bound.loaddo.Doxygen.Names;
import stream_metab.doxygen.utils.WaterGetter;
import stream_metab.doxygen.utils.WaterGetterHState;
import stream_metab.doxygen.utils.WaterGetterReader;
import neo.motif.*;
import neo.state.*;
import neo.util.Logger;

/**
 * Controls the concentration of dissolved oxygen in the cell
 * 
 * @author Administrator
 */
public class DOConc extends AbstractUpdaterDbl {

    /**
     * Mass of dissolved oxygen in the cell [M]
     */
    private HStateDbl oxygen;
    
    /**
     * Volume of water in the cell [Length<sup><small>3</small></sup>]
     */
    private WaterGetter volume;

    /**
     * Calculates the concentration of dissolved oxygen in the cell
     * 
     * @return oxygen concentration [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double computeValue()
    {
        return oxygen.v / volume.getValue();
    }

    /**
     * Set to zero and log a warning if initial concentration is not provided.
     * Otherwise, use the initial value from the initialization tables.
     * 
     * @return oxygen concentration [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Define the state dependencies for calculation
     * 
     * @see Doxygen
     * @see stream_metab.water.patch.channel.Water
     */
    @Override
    protected void setDependencies()
    {
        oxygen = (HStateDbl) getInitHState(Doxygen.class.getSimpleName());
        try
        {
            myHolon.getHState(Names.WATER);
            volume = new WaterGetterHState((HStateDbl)getInitHState(Names.WATER));
        }
        catch (HStateNotFoundException e)
        {
            try
            {
                volume = new WaterGetterReader(
                        ((HStateStr)getInitHState("ReaderPath")).v,
                        myHolon.getUID().toString(),
                        "water");
            }
            catch (IOException e1)
            {
                Logger.logError("Can't get data.");
                e1.printStackTrace();
            }
        }
    }

}
