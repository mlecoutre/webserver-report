package org.mat.samples.mongodb.services;

import org.mat.samples.mongodb.vo.ApplicationStats;
import org.mat.samples.mongodb.vo.HttpFile;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * User: E010925
 * Date: 28/11/12
 * Time: 11:21
 */
@Path("/MonitorConfig")
@Produces(MediaType.APPLICATION_JSON)
public class MonitorConfigService {

    @GET
    @Path("/dataSources/{appliName}/{serverName}/{asName}")
    public List<String> listDataSources(@PathParam("appliName") String appliName, @PathParam("serverName") String serverName, @PathParam("asName") String asName) {
        return MonitorService.listDataSources(appliName, serverName, asName);
    }

    @GET
    @Path("/ass/{appliName}")
    public List<String> listASs(@PathParam("appliName") String appliName) {
        return MonitorService.listASs(appliName);
    }

    @GET
    @Path("/servers/{appliName}")
    public List<String> listServers(@PathParam("appliName") String appliName) {
        return MonitorService.listServers(appliName);
    }

    @POST
    @Path("/{applicationName}")
    public String loadData(List<HttpFile> files, @PathParam("applicationName") String applicationName) {
        long sum = 0;
        for (HttpFile f : files) {
            //extract asName and serverName from the fileName
            String[] arr = f.getFileName().split("/");
            String asName = arr[arr.length - 2];
            String serverName = arr[2];
            sum += MonitorService.batchInsert(f.getFileName(), applicationName, serverName, asName);
        }
        return sum + "Elements loaded";
    }

    @GET
    @Path("/stats/{appliName}")
    public ApplicationStats getStats(@PathParam("appliName") String appliName) {
        return MonitorService.requestStats(appliName);
    }

    @GET
    @Path("/purge/{appliName}")
    public boolean purge(@PathParam("appliName") String appliName) {
        try {
            MonitorService.purgeDB(appliName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
