package stream_metab.doxygen.edge.advectdo;

import java.io.IOException;

import stream_metab.doxygen.utils.WaterGetter;
import stream_metab.doxygen.utils.WaterGetterHState;
import stream_metab.doxygen.utils.WaterGetterReader;
import neo.motif.*;
import neo.state.*;

/**
 * Controls the rate of oxygen mass transfer due to advection in water
 * <p>
 * <b>Input parameters or externally controlled states required by this resource
 * behavior:</b>
 * </p>
 * </b><ul style="list-style-type:none"> <li
 * style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px"> <i>WATER
 * (double)</i> - Volumetric flow rate of water across the edge
 * [Length<sup><small>3</small></sup> Time<sup><small>-1</small></sup>] </li>
 * </ul>
 * 
 * @author Administrator
 */
public class Doxygen extends AbstractRoute {

    /**
     * An inner class specifying the names of states and motifs
     * used by this behavior 
     * 
     * @author robert.payn
     */
    public static class Names {
        
        /**
         * State name for the DO concentration in an attached cell
         */
        public static final String DO_CONC_CELL = stream_metab.doxygen.patch.channeldo.DOConc.class.getSimpleName();

        /**
         * State name for flow of water in this holon
         */
        public static final String WATER = "WATER";
        
    }

    /**
     * Volumetric flow rate of water through the edge
     * [Length<sup><small>3</small></sup> Time<sup><small>-1</small></sup>]
     */
    private WaterGetter discharge;
    
    /**
     * DO concentration
     * [Mass Length<sup><small>-3</small></sup>]
     */
    private HStateDbl dOConc;
    
    /**
     * Calculate the rate of oxygen mass transfer across the edge
     * 
     * @return Rate of oxygen mass transfer [Mass
     *         Time<sup><small>-1</small></sup>]
     */
    @Override
    protected double computeValue()
    {
        // solute load is volumetric rate of water movement
        // times the concentration of solute in the water
        return discharge.getValue() * dOConc.v;
    }

    /**
     * Calculate the initial rate of oxygen mass transfer across the edge
     * 
     * @return Rate of oxygen mass transfer [Mass
     *         Time<sup><small>-1</small></sup>]
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Define the state dependencies for calculating advection of oxygen
     * 
     * @see stream_metab.doxygen.patch.channeldo.DOConc
     */
    @Override
    protected void setDependencies()
    {
        dOConc = (HStateDbl) getInitHState(DoConc.class.getSimpleName());
        try
        {
            myHolon.getHState(Names.WATER);
            discharge = new WaterGetterHState((HStateDbl)getInitHState(Names.WATER));
        }
        catch (HStateNotFoundException e)
        {
            try
            {
                discharge = new WaterGetterReader(
                        ((HStateStr)getInitHState("ReaderPath")).v,
                        myHolon.getUID().toString(),
                        "water");
            }
            catch (IOException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

}
