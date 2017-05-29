package stream_metab.utils;

import java.util.*;

import org.w3c.dom.Element;

import neo.*;

/**
 * Abstract mode for the stream metabolism simulation execution strategy
 * 
 * @author robert.payn
 *
 */
public interface Mode {

    /**
     * Run the simulation in this mode
     * 
     * @param neoModelBase 
     *      model base
     * @param workingDir 
     *      working directory
     * @param modeElem 
     *      XML element for the mode
     * @throws Exception 
     *      if error in executing mode
     */
    public abstract void runMode(ModelBase neoModelBase, String workingDir, ConfigElement modeElem) throws Exception;
    
}
