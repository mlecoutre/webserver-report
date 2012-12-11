package org.mat.samples.mongodb.vo;

import org.mat.samples.mongodb.Constants;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 * User: mlecoutre Date: 07/12/12 Time: 10:25
 */
@XmlRootElement
public class Scheduler implements Serializable {

    private static final long serialVersionUID = 1L;

    private String schedulerId;

    private String applicationName = null;
    private String serverName = null;
    private String asName = null;

    private int requestRepeatIntervalInMinutes = 5;
    private int maxHistoryToKeepInDays = 31 * 6;
    private String initialState = Constants.STATUS_RUNNING;
    private String state = Constants.STATUS_RUNNING;
    
    private Date lastExecution = null;
    private String lastStatus = null;

    private String endPointURL = null;

    public Scheduler() {
        super();
    }

    public Scheduler(String schedulerId, 
    		String applicationName, String serverName, String asName, 
    		String endPointURL, int requestRepeatIntervalInMinutes) {
        this.schedulerId = schedulerId;
        this.applicationName = applicationName;
        this.serverName = serverName;
        this.asName = asName;
        this.endPointURL = endPointURL;
        this.requestRepeatIntervalInMinutes = requestRepeatIntervalInMinutes;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getAsName() {
        return asName;
    }

    public void setAsName(String asName) {
        this.asName = asName;
    }

    public int getRequestRepeatIntervalInMinutes() {
        return requestRepeatIntervalInMinutes;
    }

    public void setRequestRepeatIntervalInMinutes(
            int requestRepeatIntervalInMinutes) {
        this.requestRepeatIntervalInMinutes = requestRepeatIntervalInMinutes;
    }

    public int getMaxHistoryToKeepInDays() {
        return maxHistoryToKeepInDays;
    }

    public void setMaxHistoryToKeepInDays(int maxHistoryToKeepInDays) {
        this.maxHistoryToKeepInDays = maxHistoryToKeepInDays;
    }

    public String getInitialState() {
        return initialState;
    }

    public void setInitialState(String initialState) {
        this.initialState = initialState;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEndPointURL() {
        return endPointURL;
    }

    public void setEndPointURL(String endPointURL) {
        this.endPointURL = endPointURL;
    }
    
    public Date getLastExecution() {
		return lastExecution;
	}

	public void setLastExecution(Date lastExecution) {
		this.lastExecution = lastExecution;
	}

	public String getLastStatus() {
		return lastStatus;
	}

	public void setLastStatus(String lastStatus) {
		this.lastStatus = lastStatus;
	}

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Scheduler [schedulerId=").append(schedulerId)
				.append(", applicationName=").append(applicationName)
				.append(", serverName=").append(serverName).append(", asName=")
				.append(asName).append(", requestRepeatIntervalInMinutes=")
				.append(requestRepeatIntervalInMinutes)
				.append(", maxHistoryToKeepInDays=")
				.append(maxHistoryToKeepInDays).append(", initialState=")
				.append(initialState).append(", state=").append(state)
				.append(", endPointURL=").append(endPointURL)
				.append(", lastExecution=").append(lastExecution)
				.append(", lastStatus=").append(lastStatus).append("]");
		return builder.toString();
	}

	// check if the scheduler is stopped
    public boolean checkIfIsStopped() {
        return Constants.STATUS_STOPPED
                .equals(this.getState());
    }

    public String getSchedulerId() {
        return schedulerId;
    }

    public void setSchedulerId(String schedulerId) {
        this.schedulerId = schedulerId;
    }
}
