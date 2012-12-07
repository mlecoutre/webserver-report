package org.mat.samples.mongodb.vo;

public enum SchedulerState {

	STARTED("started"), STOPPED("stopped");

	private String state;

	SchedulerState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

}
