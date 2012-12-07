package org.mat.samples.mongodb.services;

import org.mat.samples.mongodb.vo.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * User: E010925
 * Date: 06/12/12
 * Time: 09:22
 */
@Path("/schedulers")
@Produces(MediaType.APPLICATION_JSON)
public class SchedulerService {

    private static Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    private static List<Scheduler> schedulers = null;

    static {

        schedulers = new ArrayList<Scheduler>();
        schedulers.add(new Scheduler("id1", "Appli1", "Server1", "AS1", "Endpoint1", 10));
        schedulers.add(new Scheduler("id2", "Appli2", "Server2", "AS1", "Endpoint2", 20));
    }

    @GET
    public List<Scheduler> listSchedulers() {

        return schedulers;
    }

    @GET
    @Path("/{schedulerId}")
    public Scheduler getScheduler(@PathParam("schedulerId") String schedulerId) {
        logger.info("getScheduler for " + schedulerId);
        Scheduler scheduler = new Scheduler("id2", "Appli2", "Server2", "AS1", "Endpoint2", 20);

        return scheduler;
    }

    @POST
    public String addScheduler(Scheduler scheduler) {
        logger.info("Add scheduler " + scheduler);
        schedulers.add(scheduler);
        return "1";
    }

    /**
     * Will be used to update the scheduler,
     * either its definition, either its status (Constants.STATUS_RUNNING, Constants.STATUS_STOPPED)
     *
     * @param scheduler
     * @param schedulerId
     * @return ok or not to be displayed on the user interface.
     */
    @POST
    @Path("/{schedulerId}")
    public boolean updateScheduler(Scheduler scheduler, @PathParam("schedulerId") String schedulerId) {
        logger.info("Update Scheduler " + scheduler);
        return true;
    }
}
