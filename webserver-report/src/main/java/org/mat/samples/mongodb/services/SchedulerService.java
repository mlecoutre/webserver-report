package org.mat.samples.mongodb.services;

import org.mat.samples.mongodb.vo.Scheduler;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * User: E010925
 * Date: 06/12/12
 * Time: 09:22
 */
@Path("/schedulers")
@Produces(MediaType.APPLICATION_JSON)
public class SchedulerService {

    @GET
    public List<Scheduler> listSchedulers(){
        List<Scheduler> schedulers = null;

        return schedulers;
    }

    @POST
    public String addScheduler(Scheduler scheduler){

        return "schedulerId";
    }

    /**
     * Will be used to update the scheduler,
     * either its definition, either its status (Constants.STATUS_RUNNING, Constants.STATUS_STOPPED)
     * @param scheduler
     * @param schedulerId
     * @return ok or not to be displayed on the user interface.
     */
    @POST
    @Path("/{schedulerId}")
    public boolean updateScheduler(Scheduler scheduler, @PathParam("schedulerId") String schedulerId){

        return true;
    }
}
