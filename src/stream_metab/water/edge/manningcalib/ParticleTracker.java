package stream_metab.water.edge.manningcalib;

import java.util.ArrayList;

import stream_metab.water.edge.manningcalib.utils.Particle;

import neo.holon.Edge;
import neo.holon.EdgeGenerator;
import neo.motif.AbstractUpdaterDbl;
import neo.state.HStateDbl;
import neo.state.HStateInt;
import neo.state.HStateNotFoundException;
import neo.state.HStateStr;
import neo.uid.UniqueID;
import neo.uid.UniqueIDMgr;
import neo.util.Scheduler;

/**
 * Particle tracker for calculating average reach velocity
 * 
 * Particles are agents that independently track their progress along the
 * reach according to the local velocity of their current edge.  Particles
 * here are only supported for continuous downstream flow of water.
 * 
 * @author robert.payn
 *
 */
public class ParticleTracker extends AbstractUpdaterDbl {
   
    /**
     * List of particles in the reach
     */
    private ArrayList<Particle> particles;
    
    /**
     * Final destination edge for particles
     */
    private Edge endEdge;
    
    /**
     * Initial edge for particles
     */
    private Edge startEdge;
    
    /**
     * Last tick when a particle was released
     */
    private long lastTick;
    
    /**
     * Length of the reach
     */
    private HStateDbl reachLength;
    
    /**
     * Tick interval for particle release
     */
    private HStateInt tickInterval;

    /**
     * Introduce a new particle every 180 ticks.  Advance all particles in the reach.
     * Return the average velocity according to the average transport time of particles 
     * that have arrived during the current time step.
     */
    @Override
    protected double computeValue()
    {
        try
        {
            if (Scheduler.getCurrentTick() - lastTick == tickInterval.v)
            {
                particles.add(new Particle(startEdge, endEdge));
                lastTick = Scheduler.getCurrentTick();
            }
            ArrayList<Particle> killList = new ArrayList<Particle>();
            for (Particle particle: particles)
            {
                particle.process(Scheduler.getTimeStep());
                if (particle.isDone())
                {
                    killList.add(particle);
                }
            }
            double sumTravelTime = 0;
            int count = 0;
            for (Particle particle: killList)
            {
                count++;
                sumTravelTime += particle.getTravelTime();
                particles.remove(particle);
            }
            if (count > 0)
            {
                return reachLength.v / (sumTravelTime / (double)count);
            }
            else
            {
                return state.v;
            }
        }
        catch (HStateNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Start the initial new particle.
     */
    @Override
    protected double initValue()
    {
        try
        {
            lastTick = 0;
            Particle newParticle = new Particle(startEdge, endEdge);
            return 0;
        }
        catch (HStateNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Define the states necessary to run the particle tracker.
     */
    @Override
    protected void setDependencies()
    {
        particles = new ArrayList<Particle>();
        
        int numEdges = 1 + Integer.valueOf(((HStateStr)getInitHState("EDGENAME")).v.replace("manning", ""));
        reachLength = (HStateDbl)getInitHState("REACHLENGTH");
        tickInterval = (HStateInt)getInitHState("PARTICLETICKINTERVAL");
        
        String linkID = String.format("manning%03d", numEdges);
        UniqueID uid = UniqueIDMgr.cast(linkID);
        if (uid == null)
        {
            initError("Specified link id " + linkID + " does not exist.");
        }
        endEdge = EdgeGenerator.get(uid);

        uid = UniqueIDMgr.cast("manning001");
        if (uid == null)
        {
            initError("manning001 does not exist.");
        }
        startEdge = EdgeGenerator.get(uid);
        
        for (int i = 1; i < numEdges; i++)
        {
            String name = String.format("manning%03d", i);
            uid = UniqueIDMgr.cast(name);
            if (uid == null)
            {
                initError(name + " does not exist.");
            }
            Edge edge = EdgeGenerator.get(uid);
            getInitHState(edge, "Velocity");
        }
    }

}
