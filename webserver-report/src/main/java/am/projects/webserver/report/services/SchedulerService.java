package am.projects.webserver.report.services;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import am.projects.webserver.report.policy.SchedulerPolicy;
import am.projects.webserver.report.vo.Scheduler;
import org.quartz.SchedulerException;

/**
 * REST entry point to administer the Schedulers
 * 
 * User: E010925 Date: 06/12/12 Time: 09:22
 */
@Path("/schedulers")
@Produces(MediaType.APPLICATION_JSON)
public class SchedulerService {

	@GET
	public List<Scheduler> listSchedulers() {
		return SchedulerPolicy.listSchedulers();
	}

	@GET
	@Path("/{schedulerId}")
	public Scheduler getSchedulerById(
			@PathParam("schedulerId") String schedulerId) {
		return SchedulerPolicy.findSchedulerById(schedulerId);
	}

	@DELETE
	@Path("/{schedulerId}")
	public boolean deleteSchedulerById(
			@PathParam("schedulerId") String schedulerId) throws SchedulerException {
		return SchedulerPolicy.deleteSchedulerById(schedulerId);
	}
	
	@POST
	@Path("/{schedulerId}/stop")
	public boolean stopSchedulerById(
			@PathParam("schedulerId") String schedulerId) throws SchedulerException, IOException {
		return SchedulerPolicy.stopScheduler(schedulerId);
	}
	
	@POST
	@Path("/{schedulerId}/start")
	public boolean startSchedulerById(
			@PathParam("schedulerId") String schedulerId) throws SchedulerException, IOException {
		return SchedulerPolicy.startScheduler(schedulerId);
	}

	@POST
	public String addScheduler(Scheduler scheduler) throws IOException,
			SchedulerException {
		return SchedulerPolicy.addScheduler(scheduler);
	}

	/**
	 * Will be used to update the scheduler, either its definition, either its
	 * status (TestConstants.STATUS_RUNNING, TestConstants.STATUS_STOPPED)
	 * 
	 * @param scheduler
	 *            scheduler to update
	 * @param schedulerId
	 *            schedulerId
	 * @return ok or not to be displayed on the user interface.
	 * @throws IOException
	 * @throws SchedulerException 
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	@POST
	@Path("/{schedulerId}")
	public boolean updateScheduler(Scheduler scheduler,
			@PathParam("schedulerId") String schedulerId) throws IOException, SchedulerException {
		return SchedulerPolicy.updateScheduler(schedulerId, scheduler);
	}
}
