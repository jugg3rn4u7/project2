package prediction;
import java.text.SimpleDateFormat;

public class AppsModel {

	private int UserID;
	private SimpleDateFormat ApplicationDate;
	private int JobID;

	public int getUserID() {
		return UserID;
	}

	public void setUserID(int userID) {
		UserID = userID;
	}

	public SimpleDateFormat getApplicationDate() {
		return ApplicationDate;
	}

	public void setApplicationDate(SimpleDateFormat applicationDate) {
		ApplicationDate = applicationDate;
	}

	public int getJobID() {
		return JobID;
	}

	public void setJobID(int jobID) {
		JobID = jobID;
	}

}
