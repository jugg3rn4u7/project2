package prediction;
public class UserHistoryModel {

	private int UserID;
	private int Sequence;
	private StringBuffer JobTitle;

	public int getUserID() {
		return UserID;
	}

	public void setUserID(int userID) {
		UserID = userID;
	}

	public int getSequence() {
		return Sequence;
	}

	public void setSequence(int sequence) {
		Sequence = sequence;
	}

	public StringBuffer getJobTitle() {
		return JobTitle;
	}

	public void setJobTitle(StringBuffer jobTitle) {
		JobTitle = jobTitle;
	}

}
