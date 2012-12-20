package org.mat.samples.mongodb.services;

import org.mat.samples.mongodb.policy.MonitorPolicy;
import org.mat.samples.mongodb.vo.ApplicationStats;
import org.mat.samples.mongodb.vo.HttpFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Allow to get Configuration elements to initialize pages
 * User: E010925
 * Date: 28/11/12
 * Time: 11:21
 */
@Path("/MonitorConfig")
@Produces(MediaType.APPLICATION_JSON)
public class MonitorConfigService {
	
	private final Logger logger = LoggerFactory.getLogger(MonitorConfigService.class);

    @GET
    @Path("/applications")
    public Set<String> listApplication() {
        return MonitorPolicy.listApplications();
    }

    @GET
    @Path("/dataSources/{appliName}/{serverName}/{asName}")
    public List<String> listDataSources(@PathParam("appliName") String appliName, @PathParam("serverName") String serverName, @PathParam("asName") String asName) {
        return MonitorPolicy.listDataSources(appliName, serverName, asName);
    }

    @GET
    @Path("/qcfs/{appliName}/{serverName}/{asName}")
    public List<String> listQCFs(@PathParam("appliName") String appliName, @PathParam("serverName") String serverName, @PathParam("asName") String asName) {
        return MonitorPolicy.listQCFs(appliName, serverName, asName);
    }

    @GET
    @Path("/ass/{appliName}")
    public List<String> listASs(@PathParam("appliName") String appliName) {
        return MonitorPolicy.listASs(appliName);
    }

    @GET
    @Path("/servers/{appliName}")
    public List<String> listServers(@PathParam("appliName") String appliName) {
        return MonitorPolicy.listServers(appliName);
    }

    @POST
    @Path("/{applicationName}")
    public String loadData(List<HttpFile> files, @PathParam("applicationName") String applicationName) {
        long count = 0;
        for (HttpFile f : files) {
            //extract asName and serverName from the fileName
            String[] arr = f.getFileName().split("/");
            String asName = arr[arr.length - 2];
            String serverName = arr[2];
            try {
				long added = MonitorPolicy.batchInsert(f.getFileName(), applicationName, serverName, asName);
				count = count + added;
			} catch (IOException e) {
				logger.warn("Exception occured while insert datas from " + f.getFileName(), e);
			}
        }
        return String.format("%d elements stored in the dataStore for the application %s", count, applicationName);
    }

    @GET
    @Path("/stats/{appliName}")
    public ApplicationStats getStats(@PathParam("appliName") String appliName) {
        return MonitorPolicy.requestStats(appliName);
    }

    @GET
    @Path("/purge/{appliName}")
    public boolean purge(@PathParam("appliName") String appliName) {
        try {
            MonitorPolicy.purgeDB(appliName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @GET
    @Path("/update/{applicationName}")
    public boolean update(@PathParam("applicationName") String applicationName){
        logger.info("update "+applicationName);
        return MonitorPolicy.updateModel(applicationName);
    }
}
