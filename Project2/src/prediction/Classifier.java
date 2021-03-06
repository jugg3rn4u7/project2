package prediction;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Classifier {

	private List<FileModel> listOfFiles = new ArrayList<FileModel>();
	private List<UsersModel> usersList = null;
	private List<Integer> user2List = null;
	private List<UserHistoryModel> usersHistoryList = null;
	private List<JobsModel> jobsList = null;
	private List<AppsModel> appsList = null;
	private JSONObject configuration = null;
	private List<NearestNeighbor> listOfNeighbors = null;
	private HashMap<Integer, Integer> jobScore = null;

	public Classifier() throws IOException, ParseException {
		// TODO Auto-generated constructor stub

		createConfig();

		if ((Boolean) configuration.get("skipLoadingInputFiles") == false) {
			updateConfig("skipLoadingInputFiles", true, 1);
			reloadConfig();

			final File folder = new File("D://Materials//DataMining//Project2//data");
			List<FileModel> listOfFiles = listFilesForFolder(folder);
			loadInputFiles(listOfFiles);
		}

		if ((Boolean) configuration.get("skipCalculatingNeighbors") == true) {
			listOfNeighbors = readNeighborsTSV();
			scoreJobs(listOfNeighbors);
		}
	}

	public void scoreJobs(List<NearestNeighbor> listOfNeighbors) throws IOException, ParseException {

		readJobsTSV();

		jobScore = new HashMap<Integer, Integer>();

		Iterator<NearestNeighbor> listIter = listOfNeighbors.iterator();
		while (listIter.hasNext()) {
			NearestNeighbor n = listIter.next();

		}
	}

	public void readAppsTSV() throws IOException, ParseException {

		final File folder = new File("D://Materials//DataMining//Project2//data");
		List<FileModel> listOfFiles = listFilesForFolder(folder);

		// Iterate over file list
		Iterator<FileModel> listIter = listOfFiles.iterator();
		while (listIter.hasNext()) {
			FileModel listValue = (FileModel) listIter.next();

			// System.out.println("The value of the list is: " +
			// listValue.getName());

			System.out.println("Loading Started ...");

			if (listValue.getName().toString().trim().compareToIgnoreCase("apps.tsv") == 0) {

				System.out.println("Read apps table ...");
				appsList = readFileApps(listValue.getPath().toString());

				// TODO: Comment this iterator block
				// Iterator over apps list
				// Iterator<AppsModel> appsListIter = appsList.iterator();
				// while(appsListIter.hasNext())
				// {
				// AppsModel obj = (AppsModel)appsListIter.next();
				//
				// System.out.println("app: " +
				// obj.getApplicationDate().hashCode());
				// }
			}
		}

		System.out.println("Loading Ended ...");
	}

	public void readJobsTSV() throws IOException, ParseException {

		final File folder = new File("D://Materials//DataMining//Project2//data");
		List<FileModel> listOfFiles = listFilesForFolder(folder);

		// Iterate over file list
		Iterator<FileModel> listIter = listOfFiles.iterator();
		while (listIter.hasNext()) {
			FileModel listValue = (FileModel) listIter.next();

			// System.out.println("The value of the list is: " +
			// listValue.getName());

			System.out.println("Loading Started ...");

			if (listValue.getName().toString().trim().compareToIgnoreCase("jobs.tsv") == 0) {

				System.out.println("Read jobs table ...");
				jobsList = readFileJobs(listValue.getPath().toString());

				// TODO: Comment this iterator block
				// Iterator over jobs list
				// Iterator<JobsModel> jobsListIter = jobsList.iterator();
				// while(jobsListIter.hasNext())
				// {
				// JobsModel obj = (JobsModel)jobsListIter.next();
				//
				// System.out.println("job: " + obj.getJobID());
				// }
			}
		}

		System.out.println("Loading Ended ...");
	}

	public List<NearestNeighbor> readNeighborsTSV() throws IOException {

		BufferedReader reader;
		String line = null;
		String[] stringArray = new String[6];
		String fileToRead = "neighbors.tsv";

		listOfNeighbors = new ArrayList<NearestNeighbor>();

		// Read config file
		reader = new BufferedReader(new FileReader(fileToRead));

		while ((line = reader.readLine()) != null) {
			stringArray = line.toString().split("\\t");

			NearestNeighbor n = new NearestNeighbor();
			n.setPrimaryUser(Integer.parseInt(stringArray[0]));
			n.setNeighbor1(Integer.parseInt(stringArray[1]));
			n.setNeighbor2(Integer.parseInt(stringArray[2]));
			n.setNeighbor3(Integer.parseInt(stringArray[3]));
			n.setNeighbor4(Integer.parseInt(stringArray[4]));
			n.setNeighbor5(Integer.parseInt(stringArray[5]));

			listOfNeighbors.add(n);
			n = null;
		}

		reader.close();
		return listOfNeighbors;
	}

	public List<FileModel> listFilesForFolder(final File folder) throws IOException, ParseException {

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {

				// TODO: Remove this...
				// System.out.println("Filename: " +fileEntry.getName() +
				// "; Path: "
				// +convertPaths(FilenameUtils.separatorsToUnix(fileEntry.getAbsolutePath())));

				FileModel f = new FileModel();
				f.setName(new StringBuffer(fileEntry.getName()));
				f.setPath(new StringBuffer(convertPaths(FilenameUtils.separatorsToUnix(fileEntry.getAbsolutePath()))));

				listOfFiles.add(f);

				f = null;
			}
		}

		return listOfFiles;
	}

	public void loadInputFiles(List<FileModel> listOfFiles) throws IOException, ParseException {

		// Iterate over file list
		Iterator<FileModel> listIter = listOfFiles.iterator();
		while (listIter.hasNext()) {
			FileModel listValue = (FileModel) listIter.next();

			// System.out.println("The value of the list is: " +
			// listValue.getName());

			System.out.println("Loading Started ...");

			if (listValue.getName().toString().trim().compareToIgnoreCase("users.tsv") == 0) {

				System.out.println("Read users table ...");
				usersList = readFileUsers(listValue.getPath().toString());

				// TODO: Comment this iterator block
				// Iterator over users list
				// Iterator<UsersModel> usersListIter = usersList.iterator();
				// while(usersListIter.hasNext())
				// {
				// UsersModel obj = (UsersModel)usersListIter.next();
				//
				// System.out.println("users: " + obj.getUserID());
				// }
			}

			if (listValue.getName().toString().trim().compareToIgnoreCase("user_history.tsv") == 0) {

				System.out.println("Read user_history table ...");
				usersHistoryList = readFileUsersHistory(listValue.getPath().toString());

				// TODO: Comment this iterator block
				// Iterator over user_history list
				// Iterator<UserHistoryModel> usersListIter =
				// usersHistoryList.iterator();
				// while(usersListIter.hasNext())
				// {
				// UserHistoryModel obj =
				// (UserHistoryModel)usersListIter.next();
				//
				// System.out.println("sequence: " + obj.getSequence());
				// }
			}

			if (listValue.getName().toString().trim().compareToIgnoreCase("jobs.tsv") == 0) {

				System.out.println("Read jobs table ...");
				jobsList = readFileJobs(listValue.getPath().toString());

				// TODO: Comment this iterator block
				// Iterator over jobs list
				// Iterator<JobsModel> jobsListIter = jobsList.iterator();
				// while(jobsListIter.hasNext())
				// {
				// JobsModel obj = (JobsModel)jobsListIter.next();
				//
				// System.out.println("job: " + obj.getJobID());
				// }
			}

			if (listValue.getName().toString().trim().compareToIgnoreCase("apps.tsv") == 0) {

				System.out.println("Read apps table ...");
				appsList = readFileApps(listValue.getPath().toString());

				// TODO: Comment this iterator block
				// Iterator over apps list
				// Iterator<AppsModel> appsListIter = appsList.iterator();
				// while(appsListIter.hasNext())
				// {
				// AppsModel obj = (AppsModel)appsListIter.next();
				//
				// System.out.println("app: " +
				// obj.getApplicationDate().hashCode());
				// }
			}

			if (listValue.getName().toString().trim().compareToIgnoreCase("user2.tsv") == 0) {

				System.out.println("Read user2 table ...");
				user2List = readFileUsers2(listValue.getPath().toString());

				// TODO: Comment this iterator block
				// Iterator over user2 list
				// Iterator<Integer> user2ListIter = user2List.iterator();
				// while(user2ListIter.hasNext())
				// {
				// Integer obj = (Integer)user2ListIter.next();
				//
				// //System.out.println("user2: " + obj.intValue());
				// }
			}

			System.out.println("Loading Ended ...");
		}

		List<NearestNeighbor> neighbors = calculateNeighbor(usersList, user2List);
		Iterator<NearestNeighbor> user2ListIter = neighbors.iterator();
		while (user2ListIter.hasNext()) {
			NearestNeighbor obj = (NearestNeighbor) user2ListIter.next();

			System.out.println("primaryUser: " + obj.getPrimaryUser() + " ; neighbor1: " + obj.getNeighbor1()
					+ " ; neighbor2: " + obj.getNeighbor2() + " ; neighbor3: " + obj.getNeighbor3() + " ; neighbor4: "
					+ obj.getNeighbor4() + " ; neighbor5: " + obj.getNeighbor5());
		}

		if ((Boolean) configuration.get("skipCalculatingNeighbors") == false) {
			updateConfig("skipCalculatingNeighbors", true, 1);
			reloadConfig();
			writeNeighborsTSV(neighbors);
		}
	}

	public String convertPaths(String path) {
		return path.replaceAll("/", "//");
	}

	public boolean isDirtyData(String data, String expectedFormat) {

		if (data.trim().length() == 0 || data == null) {
			return true;
		} else if (expectedFormat.equalsIgnoreCase("number")) {
			return !isNumeric(data);
		} else {
			return false;
		}
	}

	public boolean isNumeric(String str) {
		try {
			int d = Integer.parseInt(str);
		} catch (NumberFormatException nfe) {
			return false;
		}

		return true;
	}

	public List<UsersModel> readFileUsers(String path) {

		try {
			List<UsersModel> users = new ArrayList<UsersModel>();

			String[] stringArray = null;
			BufferedReader reader;
			String line = null;
			Integer lineCounter = 0;

			// Load Users table
			reader = new BufferedReader(new FileReader(path));

			line = null;
			lineCounter = 0;

			while ((line = reader.readLine()) != null) {
				// skip headers
				if (lineCounter == 0) {
					lineCounter++;
					continue;
				}

				stringArray = line.toString().split("\\t");

				// TODO: Remove this...
				// System.out.println("Single line: "+ stringArray[0] +"; "+
				// stringArray[5] +"; "+ stringArray[4] +"; "+ stringArray[9]);

				lineCounter++;

				UsersModel newUser = new UsersModel();

				if (!isDirtyData(stringArray[0], "number")) {
					newUser.setUserID(Integer.parseInt(stringArray[0]));
				}

				if (!isDirtyData(stringArray[5], "text")) {
					newUser.setDegreeType(new StringBuffer(stringArray[5]));
				}

				if (!isDirtyData(stringArray[4], "number")) {
					newUser.setZipCode(Integer.parseInt(stringArray[4]));
				}

				if (!isDirtyData(stringArray[9], "number")) {
					newUser.setTotalYearsExperience(Integer.parseInt(stringArray[9]));
				}

				users.add(newUser);

				newUser = null;
			}

			reader.close();

			return users;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<UserHistoryModel> readFileUsersHistory(String path) {

		try {
			List<UserHistoryModel> users = new ArrayList<UserHistoryModel>();

			String[] stringArray = null;
			BufferedReader reader;
			String line = null;
			Integer lineCounter = 0;

			// Load Users table
			reader = new BufferedReader(new FileReader(path));

			line = null;
			lineCounter = 0;

			while ((line = reader.readLine()) != null) {
				// skip headers
				if (lineCounter == 0) {
					lineCounter++;
					continue;
				}

				stringArray = line.toString().split("\\t");

				// Missing JobTitle problem fix
				boolean jobTitleMissing = false;
				if (stringArray.length < 3) {
					jobTitleMissing = true;
				}

				// TODO: Remove this...
				// System.out.println("Single line: "+ stringArray[0] +"; "+
				// stringArray[1] +";");

				lineCounter++;

				UserHistoryModel newUser = new UserHistoryModel();

				if (!isDirtyData(stringArray[0], "number")) {
					newUser.setUserID(Integer.parseInt(stringArray[0]));
				}

				if (!isDirtyData(stringArray[1], "number")) {
					newUser.setSequence(Integer.parseInt(stringArray[1]));
				}

				if (!jobTitleMissing && !isDirtyData(stringArray[2], "text")) {
					newUser.setJobTitle(new StringBuffer(stringArray[2]));
				} else {
					jobTitleMissing = false;
				}

				users.add(newUser);

				newUser = null;
			}

			reader.close();

			return users;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<JobsModel> readFileJobs(String path) {

		try {
			List<JobsModel> users = new ArrayList<JobsModel>();

			String[] stringArray = null;
			BufferedReader reader;
			String line = null;
			Integer lineCounter = 0;

			// Load Users table
			reader = new BufferedReader(new FileReader(path));

			line = null;
			lineCounter = 0;

			while ((line = reader.readLine()) != null) {
				// skip headers
				if (lineCounter == 0) {
					lineCounter++;
					continue;
				}

				stringArray = line.toString().split("\\t");

				// TODO: Remove this...
				// System.out.println("Single line: "+ stringArray[0] +"; "+
				// stringArray[1] +";");

				lineCounter++;

				JobsModel newUser = new JobsModel();

				if (!isDirtyData(stringArray[0], "number")) {
					newUser.setJobID(Integer.parseInt(stringArray[0]));
				}

				if (!isDirtyData(stringArray[1], "text")) {
					newUser.setTitle(new StringBuffer(stringArray[1]));
				}

				if (!isDirtyData(stringArray[4], "text")) {
					newUser.setCity(new StringBuffer(stringArray[4]));
				}

				if (!isDirtyData(stringArray[7], "number")) {
					newUser.setZip5(Integer.parseInt(stringArray[7]));
				}

				users.add(newUser);

				newUser = null;
			}

			reader.close();

			return users;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	public List<AppsModel> readFileApps(String path) {

		try {
			List<AppsModel> users = new ArrayList<AppsModel>();

			String[] stringArray = null;
			BufferedReader reader;
			String line = null;
			Integer lineCounter = 0;

			// Load Users table
			reader = new BufferedReader(new FileReader(path));

			line = null;
			lineCounter = 0;

			while ((line = reader.readLine()) != null) {
				// skip headers
				if (lineCounter == 0) {
					lineCounter++;
					continue;
				}

				stringArray = line.toString().split("\\t");

				// TODO: Remove this...
				// System.out.println("Single line: "+ stringArray[0] +"; "+
				// stringArray[1] +"; "+ stringArray[2]);

				lineCounter++;

				AppsModel newUser = new AppsModel();

				if (!isDirtyData(stringArray[0], "number")) {
					newUser.setUserID(Integer.parseInt(stringArray[0]));
				}

				if (!isDirtyData(stringArray[1], "text")) {
					newUser.setApplicationDate(new SimpleDateFormat(stringArray[1]));
				}

				if (!isDirtyData(stringArray[2], "text")) {
					newUser.setJobID(Integer.parseInt(stringArray[2]));
				}

				users.add(newUser);

				newUser = null;
			}

			reader.close();

			return users;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Integer> readFileUsers2(String path) {

		try {
			List<Integer> users = new ArrayList<Integer>();

			String[] stringArray = null;
			BufferedReader reader;
			String line = null;
			Integer lineCounter = 0;

			// Load Users table
			reader = new BufferedReader(new FileReader(path));

			line = null;
			lineCounter = 0;

			while ((line = reader.readLine()) != null) {
				// skip headers
				if (lineCounter == 0) {
					lineCounter++;
					continue;
				}

				stringArray = line.toString().split("\\t");

				// TODO: Remove this...
				// System.out.println("Single line: "+ stringArray[0] +"; ");

				lineCounter++;

				if (!isDirtyData(stringArray[0], "number")) {
					users.add(Integer.parseInt(stringArray[0]));
				}

			}

			reader.close();

			return users;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<NearestNeighbor> calculateNeighbor(List<UsersModel> u, List<Integer> u2) {

		try {

			List<UsersModel> subsetUserDetails = new ArrayList<UsersModel>();

			// Iterator over the 'u2' list
			Iterator<Integer> listIter = u2.iterator();
			while (listIter.hasNext()) {
				Integer user = (Integer) listIter.next();

				// Iterator over the 'u' list
				Iterator<UsersModel> listIterU = u.iterator();
				while (listIterU.hasNext()) {
					UsersModel anotherUser = (UsersModel) listIterU.next();

					// Compare if the user from u2 with the user from u
					if (user == anotherUser.getUserID()) {
						subsetUserDetails.add(anotherUser);
						break;
					}
				}
			}

			List<NearestNeighbor> listOfNeighbors = new ArrayList<NearestNeighbor>();

			// TODO: Remove this block later
			// Display subset of users and their details
			// Iterator over the subsetUserDetails list
			Iterator<UsersModel> subsetListIter = subsetUserDetails.iterator();
			while (subsetListIter.hasNext()) {
				UsersModel userObj = (UsersModel) subsetListIter.next();

				// System.out.println("U2 subset list (details): " +
				// userObj.getUserID() + "; " +userObj.getZipCode());

				Integer[] nearestNeighborForDatapoint = find5NeighborsOf(u, userObj);

				System.out.println("Neighbors of " + userObj.getUserID() + " are " + nearestNeighborForDatapoint[0]
						+ ", " + nearestNeighborForDatapoint[1] + ", " + nearestNeighborForDatapoint[2] + ", "
						+ nearestNeighborForDatapoint[3] + ", " + nearestNeighborForDatapoint[4]);

				NearestNeighbor n = new NearestNeighbor();

				n.setPrimaryUser(userObj.getUserID());
				n.setNeighbor1(nearestNeighborForDatapoint[0]);
				n.setNeighbor2(nearestNeighborForDatapoint[1]);
				n.setNeighbor3(nearestNeighborForDatapoint[2]);
				n.setNeighbor4(nearestNeighborForDatapoint[3]);
				n.setNeighbor5(nearestNeighborForDatapoint[4]);

				listOfNeighbors.add(n);
				n = null;
			}

			return listOfNeighbors;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public int calculateDistance(UsersModel comparisonRecord, UsersModel keyRecord) {

		try {

			int distance = 0;

			if (comparisonRecord.getZipCode() == keyRecord.getZipCode()) {
				distance += 1;
			} else {
				distance += 0;
			}

			if ((comparisonRecord.getMajor() != null)
					&& comparisonRecord.getMajor().toString().compareToIgnoreCase(keyRecord.getMajor().toString()) == 0) {
				distance += 1;
			} else {
				distance += 0;
			}

			if (comparisonRecord.getTotalYearsExperience() == keyRecord.getTotalYearsExperience()) {
				distance += 1;
			} else {
				distance += 0;
			}

			return distance;

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public Integer[] find5NeighborsOf(List<UsersModel> searchList, UsersModel keyRecord) {

		try {

			Integer users[] = new Integer[searchList.size()];
			Integer distances[] = new Integer[searchList.size()];

			int counter = 0;
			int skipIterationCounter = 0;

			Iterator<UsersModel> listIter = searchList.iterator();
			while (listIter.hasNext()) {
				UsersModel comparisonRecord = (UsersModel) listIter.next();

				// comparison record and key record should not be the same
				// record
				// If it is the same ... ignore that record and hence skip the
				// iteration
				if (comparisonRecord.getUserID() == keyRecord.getUserID()) {
					skipIterationCounter++; // Count the number of iterations
											// skipped (or number of common
											// users between U & U2)
					continue;
				}

				users[counter] = comparisonRecord.getUserID();
				distances[counter] = calculateDistance(comparisonRecord, keyRecord);

				counter++;
			}

			// TODO: Remove this block...
			// for (int i = 0; i < distances.length; i++) {
			// System.out.println("i: "+ i +" Distances: "+ distances[i] +
			// " ; userID : "+users[i]);
			// assert(distances[i] == null);
			// }

			int n = users.length - skipIterationCounter; // Ignore common users
			int temp = 0, temp1 = 0;

			for (int i = 0; i < n; i++) {
				for (int j = 1; j < (n - i); j++) {

					if (distances[j - 1] > distances[j]) {
						// swap the elements!
						temp = distances[j - 1];
						distances[j - 1] = distances[j];
						distances[j] = temp;

						temp1 = users[j - 1];
						users[j - 1] = users[j];
						users[j] = temp1;
					}

				}
			}

			Integer finalReturnList[] = new Integer[5];
			Integer counter2 = 0;

			for (int i = n - 1; i >= n - 5; i--) {
				finalReturnList[counter2] = users[i];
				counter2++;
			}

			// TODO: Remove this block...
			// for (int i = 0; i < finalReturnList.length; i++) {
			// System.out.println("sorted list of neighbors: "+
			// finalReturnList[i]);
			// }

			return finalReturnList;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void writeNeighborsTSV(List<NearestNeighbor> listOfNeighbors) throws IOException {

		String consolidatedData = "", fileToWrite = "neighbors.tsv";

		Iterator<NearestNeighbor> neighborsListIter = listOfNeighbors.iterator();
		while (neighborsListIter.hasNext()) {
			NearestNeighbor obj = (NearestNeighbor) neighborsListIter.next();

			consolidatedData += obj.getPrimaryUser() + "\t" + obj.getNeighbor1() + "\t" + obj.getNeighbor2() + "\t"
					+ obj.getNeighbor3() + "\t" + obj.getNeighbor4() + "\t" + obj.getNeighbor5();

			if (!(listOfNeighbors.indexOf(obj) == (listOfNeighbors.size() - 1))) {
				consolidatedData += "\n";
			}
		}

		write(consolidatedData, fileToWrite);
	}

	public void write(String dataToWrite, String filename) {
		try {

			File file = new File(filename);

			if (!file.exists()) {
				file.createNewFile();
			}

			FileOutputStream out = new FileOutputStream(file, false);
			out.write(dataToWrite.getBytes(Charset.forName("UTF-8")));
			out.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void createConfig() {

		String consolidatedData = "", fileToWrite = "config.json";

		JSONObject obj = new JSONObject();

		obj.put("skipNewConfig", new Boolean(false));
		obj.put("skipCalculatingNeighbors", new Boolean(false));
		obj.put("skipLoadingInputFiles", new Boolean(false));

		// Load config JSONObject in memory
		configuration = obj;

		consolidatedData += JSONObject.toJSONString(obj);

		write(consolidatedData, fileToWrite);
	}

	@SuppressWarnings("unchecked")
	public void updateConfig(String attr, Object value, int type) throws ParseException, IOException {

		String fileToReadWrite = "config.json";
		String configString = read(fileToReadWrite);
		JSONParser parser = new JSONParser();
		JSONObject configObj = (JSONObject) parser.parse(cleanseJSON(configString));

		switch (type) {
		case 1:
			configObj.put(attr, new Boolean((Boolean) value));
			break;

		default:
			break;
		}

		// Write updated data
		write(configObj.toJSONString(), fileToReadWrite);
	}

	public JSONObject getConfig() throws IOException, ParseException {

		String fileToReadWrite = "config.json";
		String configString = read(fileToReadWrite);
		JSONParser parser = new JSONParser();
		JSONObject configObj = (JSONObject) parser.parse(cleanseJSON(configString));

		return configObj;
	}

	public void reloadConfig() throws IOException, ParseException {
		configuration = getConfig();
	}

	public String read(String filename) throws IOException {

		BufferedReader reader;
		String line = null;
		String consolidatedData = "";

		// Read config file
		reader = new BufferedReader(new FileReader(filename));

		while ((line = reader.readLine()) != null) {
			consolidatedData += line;
		}

		reader.close();
		return consolidatedData;
	}

	public String cleanseJSON(String str) {
		return str.replaceAll("\"", "\\\"");
	}
}
