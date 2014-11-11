package prediction;
import java.util.Date;

public class JobsModel {

	private int JobID;
	private StringBuffer Title;
	private StringBuffer Description;
	private StringBuffer Requirements;
	private StringBuffer City;
	private StringBuffer State;
	private StringBuffer Country;
	private int Zip5;
	private Date StartDate;
	private Date EndDate;

	public int getJobID() {
		return JobID;
	}

	public void setJobID(int jobID) {
		JobID = jobID;
	}

	public StringBuffer getTitle() {
		return Title;
	}

	public void setTitle(StringBuffer title) {
		Title = title;
	}

	public StringBuffer getDescription() {
		return Description;
	}

	public void setDescription(StringBuffer description) {
		Description = description;
	}

	public StringBuffer getRequirements() {
		return Requirements;
	}

	public void setRequirements(StringBuffer requirements) {
		Requirements = requirements;
	}

	public StringBuffer getCity() {
		return City;
	}

	public void setCity(StringBuffer city) {
		City = city;
	}

	public StringBuffer getState() {
		return State;
	}

	public void setState(StringBuffer state) {
		State = state;
	}

	public StringBuffer getCountry() {
		return Country;
	}

	public void setCountry(StringBuffer country) {
		Country = country;
	}

	public int getZip5() {
		return Zip5;
	}

	public void setZip5(int zip5) {
		Zip5 = zip5;
	}

	public Date getStartDate() {
		return StartDate;
	}

	public void setStartDate(Date startDate) {
		StartDate = startDate;
	}

	public Date getEndDate() {
		return EndDate;
	}

	public void setEndDate(Date endDate) {
		EndDate = endDate;
	}

}
