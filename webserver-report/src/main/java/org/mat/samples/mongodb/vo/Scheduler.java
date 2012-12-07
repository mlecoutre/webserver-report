package org.mat.samples.mongodb.vo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.mat.samples.mongodb.Constants;

/**
 * User: mlecoutre Date: 07/12/12 Time: 10:25
 */
@XmlRootElement
public class Scheduler implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient String _id = null;

	private String applicationName = null;
	private String serverName = null;
	private String asName = null;

	private int requestRepeatIntervalInMinutes = 5;
	private int maxHistoryToKeepInDays = 31 * 6;

	private String initialState = Constants.STATUS_RUNNING;
	private String state = Constants.STATUS_RUNNING;

	private String endPointURL = null;

	public Scheduler() {
		super();
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Scheduler [_id=").append(_id)
				.append(", applicationName=").append(applicationName)
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
	public boolean stateStopped() {

		boolean stopped = Constants.STATUS_STOPPED.equals(
				this.state);
		return stopped;
	}

}
