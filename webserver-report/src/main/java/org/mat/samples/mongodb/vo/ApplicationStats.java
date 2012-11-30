package org.mat.samples.mongodb.vo;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * User: E010925
 * Date: 30/11/12
 * Time: 10:59
 */
@XmlRootElement
public class ApplicationStats {

    private String applicationName;
    private long nbElements;
    private String startDate;
    private String endDate;
    private List<String> ass;
    private List<String> dataSources;
    private List<String> servers;

    public ApplicationStats() {
    }

    public ApplicationStats(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public long getNbElements() {
        return nbElements;
    }

    public void setNbElements(long nbElements) {
        this.nbElements = nbElements;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<String> getAss() {
        return ass;
    }

    public void setAss(List<String> ass) {
        this.ass = ass;
    }

    public List<String> getDataSources() {
        return dataSources;
    }

    public void setDataSources(List<String> dataSources) {
        this.dataSources = dataSources;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }

    @Override
    public String toString() {
        return "ApplicationStats{" +
                "applicationName='" + applicationName + '\'' +
                ", nbElements=" + nbElements +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", ass=" + ass +
                ", dataSources=" + dataSources +
                ", servers=" + servers +
                '}';
    }
}
