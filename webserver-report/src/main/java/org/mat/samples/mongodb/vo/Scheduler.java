package org.mat.samples.mongodb.vo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: mlecoutre Date: 07/12/12 Time: 10:25
 */
@XmlRootElement
public class Scheduler implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String applicationName = null;
	private String serverName = null;
	private String asName = null;

	private int requestRepeatIntervalInMinutes = 5;
	private int maxHistoryToKeepInDays = 31 * 6;

	private SchedulerState initialState = SchedulerState.STARTED;
	private SchedulerState state = SchedulerState.STARTED;

	private String endPointURL = null;

	public Scheduler() {
		super();
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

	public SchedulerState getInitialState() {
		return initialState;
	}

	public void setInitialState(SchedulerState initialState) {
		this.initialState = initialState;
	}

	public SchedulerState getState() {
		return state;
	}

	public void setState(SchedulerState state) {
		this.state = state;
	}

	public String getEndPointURL() {
		return endPointURL;
	}

	public void setEndPointURL(String endPointURL) {
		this.endPointURL = endPointURL;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Scheduler [applicationName=").append(applicationName)
				.append(", serverName=").append(serverName).append(", asName=")
				.append(asName).append(", requestRepeatIntervalInMinutes=")
				.append(requestRepeatIntervalInMinutes)
				.append(", maxHistoryToKeepInDays=")
				.append(maxHistoryToKeepInDays).append(", initialState=")
				.append(initialState).append(", state=").append(state)
				.append(", endPointURL=").append(endPointURL).append("]");
		return builder.toString();
	}

	// check if the scheduler is stopped
	public boolean isStopped() {
		
		boolean stopped = SchedulerState.STOPPED.getState()
				.equals(this.state.getState());
		return stopped;
	}

}
