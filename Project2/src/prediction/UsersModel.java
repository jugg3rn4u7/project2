package prediction;

import java.util.Date;

public class UsersModel {

	private int UserID;
	private StringBuffer City;
	private StringBuffer State;
	private StringBuffer Country;
	private int ZipCode;
	private StringBuffer DegreeType;
	private StringBuffer Major;
	private Date GraduationDate;
	private int WorkHistoryCount;
	private int TotalYearsExperience;
	private StringBuffer CurrentlyEmployed;
	private StringBuffer ManagedOthers;
	private int ManagedHowMany;

	public int getUserID() {
		return UserID;
	}

	public void setUserID(int userID) {
		UserID = userID;
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

	public int getZipCode() {
		return ZipCode;
	}

	public void setZipCode(int zipCode) {
		ZipCode = zipCode;
	}

	public StringBuffer getDegreeType() {
		return DegreeType;
	}

	public void setDegreeType(StringBuffer degreeType) {
		DegreeType = degreeType;
	}

	public StringBuffer getMajor() {
		return Major;
	}

	public void setMajor(StringBuffer major) {
		Major = major;
	}

	public Date getGraduationDate() {
		return GraduationDate;
	}

	public void setGraduationDate(Date graduationDate) {
		GraduationDate = graduationDate;
	}

	public int getWorkHistoryCount() {
		return WorkHistoryCount;
	}

	public void setWorkHistoryCount(int workHistoryCount) {
		WorkHistoryCount = workHistoryCount;
	}

	public int getTotalYearsExperience() {
		return TotalYearsExperience;
	}

	public void setTotalYearsExperience(int totalYearsExperience) {
		TotalYearsExperience = totalYearsExperience;
	}

	public StringBuffer getCurrentlyEmployed() {
		return CurrentlyEmployed;
	}

	public void setCurrentlyEmployed(StringBuffer currentlyEmployed) {
		CurrentlyEmployed = currentlyEmployed;
	}

	public StringBuffer getManagedOthers() {
		return ManagedOthers;
	}

	public void setManagedOthers(StringBuffer managedOthers) {
		ManagedOthers = managedOthers;
	}

	public int getManagedHowMany() {
		return ManagedHowMany;
	}

	public void setManagedHowMany(int managedHowMany) {
		ManagedHowMany = managedHowMany;
	}

}
