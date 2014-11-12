package prediction;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Classifier {

	private List<FileModel> listOfFiles = new ArrayList<FileModel>();
	private HashMap<Integer, UsersModel> usersList = null;
	private List<Integer> user2List = null;
	private List<UserHistoryModel> usersHistoryList = null;
	private HashMap<Integer, JobsModel> jobsList = null;
	private HashMap<Integer, AppsModel> appsList = null;
	private JSONObject configuration = null;
	private HashMap<Integer, NearestNeighbor> listOfNeighbors = null;
	private HashMap<Integer, Integer> jobScore = null;
	private LinkedHashMap<Integer, Integer> sortedJobScore = null;
	private Integer[] topJobs = null;
	private String INPUT_PATH = null;
	private String OUTPUT_PATH = null; 

	public Classifier(String folderLocation, String outputLocation) throws IOException, ParseException {
		// TODO Auto-generated constructor stub
		
		INPUT_PATH = folderLocation;
		OUTPUT_PATH = outputLocation;
		
		createConfig();

		if ((Boolean) configuration.get("skipLoadingInputFiles") == false) {
			updateConfig("skipLoadingInputFiles", true, 1);
			reloadConfig();

			final File folder = new File(INPUT_PATH);
			List<FileModel> listOfFiles = listFilesForFolder(folder);
			loadInputFiles(listOfFiles);
		}

		if ((Boolean) configuration.get("skipCalculatingNeighbors") == true) {
			listOfNeighbors = readNeighborsTSV();
			scoreJobs(listOfNeighbors);
		}
	}

	public void scoreJobs(HashMap<Integer, NearestNeighbor> listOfNeighbors) throws IOException, ParseException {

		readJobsTSV();
		readAppsTSV();

		jobScore = new HashMap<Integer, Integer>();

		Set set = listOfNeighbors.entrySet();
		Iterator i = set.iterator();
	    while(i.hasNext()) 
	    {
	    	Map.Entry me = (Map.Entry)i.next();
	        
	    	NearestNeighbor n = (NearestNeighbor)me.getValue();
	    	
	    	assignRank(getAppliedJobs(n.getNeighbor1()));
	    	assignRank(getAppliedJobs(n.getNeighbor2()));
	    	assignRank(getAppliedJobs(n.getNeighbor3()));
	    	assignRank(getAppliedJobs(n.getNeighbor4()));
	    	assignRank(getAppliedJobs(n.getNeighbor5()));
	    	
	    	n = null;
	    }
	    
	    sortedJobScore = sortHashMapByValues(jobScore);
	    displayScores();
	    writeOutputTSV();
	}
	
	public LinkedHashMap sortHashMapByValues(HashMap passedMap) {
		   
		List mapKeys = new ArrayList(passedMap.keySet());
		List mapValues = new ArrayList(passedMap.values());
		
		Collections.sort(mapValues);
		Collections.sort(mapKeys);

		LinkedHashMap sortedMap = new LinkedHashMap();

		Iterator valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			
		       Object val = valueIt.next();
		       Iterator keyIt = mapKeys.iterator();

		       while (keyIt.hasNext()) {
		    	   
		           Object key = keyIt.next();
		           Integer comp1 = (Integer)passedMap.get(key);
		           Integer comp2 = (Integer)val;

		           if (comp1 == comp2) {
		               passedMap.remove(key);
		               mapKeys.remove(key);
		               sortedMap.put(key, val);
		               break;
		           }
		       }
		}
		
		return sortedMap;
	}

	public void displayScores() {
		
		int counter = 0;
		Set set = sortedJobScore.entrySet();
	    Iterator i = set.iterator();
	    
	    topJobs = new Integer[set.size()];
	    
	    while(i.hasNext()) {	
	    	Map.Entry me = (Map.Entry)i.next();
	        System.out.println(counter + ". " + me.getKey() + " : " + me.getValue());
	        topJobs[counter] = (Integer)me.getKey();
	        counter++;
	    }
	}
	
	public void writeOutputTSV() {
		
		String consolidatedData = "", fileToWrite = "output.tsv";
		
		fileToWrite = OUTPUT_PATH + "//" + fileToWrite;
		
		for (int i = topJobs.length - 1; i > topJobs.length - 151; i--) 
		{
			consolidatedData += topJobs[i] + "\n";
		}

		write(consolidatedData, fileToWrite);
	}

	public List<Integer> getAppliedJobs(Integer userId) {
		
		List<Integer> appliedJobs = new ArrayList<Integer>();
		
		Set set = appsList.entrySet();
		Iterator i = set.iterator();
	    while(i.hasNext()) 
	    {
	    	Map.Entry me = (Map.Entry)i.next();
	    	
	        if(((AppsModel)me.getValue()).getUserID() == userId) {
	        	appliedJobs.add(((AppsModel)me.getValue()).getJobID());
	        }
	    }
	    
	    if(appliedJobs.isEmpty()) {
	    	return null;
	    } else {
	    	return appliedJobs;
	    }
	}
	
	public void assignRank(List<Integer> usersAppliedJobs) {
		
		if(usersAppliedJobs == null) {
			return; //No jobs applied
		}
		
		Iterator<Integer> listIter = usersAppliedJobs.iterator();
		while(listIter.hasNext())
		{
			Integer jobId = (Integer)listIter.next();
			
			if(jobScore.get(jobId) == null) {
	    		jobScore.put(jobId, new Integer(1));
	    	} else {
	    		Integer count = jobScore.get(jobId);
	    		jobScore.put(jobId, new Integer(++count));
	    	}    	
		}
	}

	public void readAppsTSV() throws IOException, ParseException {

		final File folder = new File(INPUT_PATH);
		List<FileModel> listOfFiles = listFilesForFolder(folder);

		// Iterate over file list
		Iterator<FileModel> listIter = listOfFiles.iterator();
		while (listIter.hasNext()) {
			FileModel listValue = (FileModel) listIter.next();

			// System.out.println("The value of the list is: " +
			// listValue.getName());

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
	}

	public void readJobsTSV() throws IOException, ParseException {

		final File folder = new File(INPUT_PATH);
		List<FileModel> listOfFiles = listFilesForFolder(folder);

		// Iterate over file list
		Iterator<FileModel> listIter = listOfFiles.iterator();
		while (listIter.hasNext()) {
			FileModel listValue = (FileModel) listIter.next();

			// System.out.println("The value of the list is: " +
			// listValue.getName());

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
	}

	public HashMap<Integer, NearestNeighbor> readNeighborsTSV() throws IOException {

		BufferedReader reader;
		String line = null;
		String[] stringArray = new String[6];
		String fileToRead = "neighbors.tsv";

		listOfNeighbors = new HashMap<Integer, NearestNeighbor>();

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

			listOfNeighbors.put(n.getPrimaryUser(), n);
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

		listOfNeighbors = calculateNeighbor(usersList, user2List);
		Set set = listOfNeighbors.entrySet();
		Iterator i = set.iterator();
	    while(i.hasNext()) 
	    {
	    	Map.Entry me = (Map.Entry)i.next();
	        
	        System.out.println("primaryUser: " + (Integer)me.getKey() + 
	        		" ; neighbor1: " + ((NearestNeighbor)me.getValue()).getNeighbor1() + 
	        		" ; neighbor2: " + ((NearestNeighbor)me.getValue()).getNeighbor2() + 
	        		" ; neighbor3: " + ((NearestNeighbor)me.getValue()).getNeighbor3() + 
	        		" ; neighbor4: " + ((NearestNeighbor)me.getValue()).getNeighbor4() + 
	        		" ; neighbor5: " + ((NearestNeighbor)me.getValue()).getNeighbor5());

	    }

		if ((Boolean) configuration.get("skipCalculatingNeighbors") == false) {
			updateConfig("skipCalculatingNeighbors", true, 1);
			reloadConfig();
			writeNeighborsTSV(listOfNeighbors);
			scoreJobs(listOfNeighbors);
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

	public HashMap<Integer, UsersModel> readFileUsers(String path) {

		try {
			//List<UsersModel> users = new ArrayList<UsersModel>();
			HashMap<Integer, UsersModel> users = new HashMap<Integer, UsersModel>();

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

				users.put(newUser.getUserID(), newUser);

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

	public HashMap<Integer, JobsModel> readFileJobs(String path) {

		try {
			//List<JobsModel> users = new ArrayList<JobsModel>();
			HashMap<Integer, JobsModel> jobs = new HashMap<Integer, JobsModel>();

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

				JobsModel newJob = new JobsModel();

				if (!isDirtyData(stringArray[0], "number")) {
					newJob.setJobID(Integer.parseInt(stringArray[0]));
				}

				if (!isDirtyData(stringArray[1], "text")) {
					newJob.setTitle(new StringBuffer(stringArray[1]));
				}

				if (!isDirtyData(stringArray[4], "text")) {
					newJob.setCity(new StringBuffer(stringArray[4]));
				}

				if (!isDirtyData(stringArray[7], "number")) {
					newJob.setZip5(Integer.parseInt(stringArray[7]));
				}

				jobs.put(newJob.getJobID(), newJob);

				newJob = null;
			}

			reader.close();

			return jobs;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	public HashMap<Integer, AppsModel> readFileApps(String path) {

		try {
			//List<AppsModel> users = new ArrayList<AppsModel>();
			HashMap<Integer, AppsModel> users = new HashMap<Integer, AppsModel>();
			
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

				AppsModel newApp = new AppsModel();

				if (!isDirtyData(stringArray[0], "number")) {
					newApp.setUserID(Integer.parseInt(stringArray[0]));
				}

				if (!isDirtyData(stringArray[1], "text")) {
					newApp.setApplicationDate(new SimpleDateFormat(stringArray[1]));
				}

				if (!isDirtyData(stringArray[2], "text")) {
					newApp.setJobID(Integer.parseInt(stringArray[2]));
				}

				users.put(lineCounter, newApp);

				newApp = null;
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
				
				//Note- Header skip not required ... U2 file has no headers row
				// skip headers
//				if (lineCounter == 0) {
//					lineCounter++;
//					continue;
//				}

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

	public HashMap<Integer, NearestNeighbor> calculateNeighbor(HashMap<Integer, UsersModel> u, List<Integer> u2) {

		try {

			HashMap<Integer, UsersModel> subsetUserDetails = new HashMap<Integer, UsersModel>();

			// Iterator over the 'u2' list
			Iterator<Integer> listIter = u2.iterator();
			while (listIter.hasNext()) {
				Integer user = (Integer) listIter.next();
				subsetUserDetails.put(user, u.get(user));
			}

			HashMap<Integer, NearestNeighbor> listOfNeighbors = new HashMap<Integer, NearestNeighbor>();
			
			Set set = subsetUserDetails.entrySet();
			Iterator i = set.iterator();
		    while(i.hasNext()) 
		    {
		    	Map.Entry me = (Map.Entry)i.next();
		        Integer[] nearestNeighborForDatapoint = find5NeighborsOf(u, (UsersModel)me.getValue());

				System.out.println("Neighbors of " + (Integer)me.getKey() + " are " + nearestNeighborForDatapoint[0]
							+ ", " + nearestNeighborForDatapoint[1] + ", " + nearestNeighborForDatapoint[2] + ", "
							+ nearestNeighborForDatapoint[3] + ", " + nearestNeighborForDatapoint[4]);

				NearestNeighbor n = new NearestNeighbor();

				n.setPrimaryUser((Integer)me.getKey());
				n.setNeighbor1(nearestNeighborForDatapoint[0]);
				n.setNeighbor2(nearestNeighborForDatapoint[1]);
				n.setNeighbor3(nearestNeighborForDatapoint[2]);
				n.setNeighbor4(nearestNeighborForDatapoint[3]);
				n.setNeighbor5(nearestNeighborForDatapoint[4]);

				listOfNeighbors.put((Integer)me.getKey(), n);
		    }
		    
			return listOfNeighbors;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public double calculateDistance(UsersModel comparisonRecord, UsersModel keyRecord) {

		try {

			double distance = 0;

			distance += 1.0/getModulusValue(comparisonRecord.getZipCode() - keyRecord.getZipCode());

			if ((comparisonRecord.getMajor() != null)
					&& comparisonRecord.getMajor().toString().compareToIgnoreCase(keyRecord.getMajor().toString()) == 0) {
				distance += 1;
			} else {
				distance += 0;
			}

			distance += 1.0/getModulusValue(comparisonRecord.getTotalYearsExperience() - keyRecord.getTotalYearsExperience());

			return distance;

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public double getModulusValue(double number) {
		return (number <= 0) ? -number : number;
	}

	public Integer[] find5NeighborsOf(HashMap<Integer, UsersModel> searchList, UsersModel keyRecord) {

		try {

			Integer users[] = new Integer[searchList.size()];
			Double distances[] = new Double[searchList.size()];

			int counter = 0;
			int skipIterationCounter = 0;
			
			Set set = searchList.entrySet();
			Iterator iter = set.iterator();
		    while(iter.hasNext()) 
		    {
		    	Map.Entry me = (Map.Entry)iter.next();
		    	
		    	if ((Integer)me.getKey() == keyRecord.getUserID()) {
					skipIterationCounter++; // Count the number of iterations
											// skipped (or number of common
											// users between U & U2)
					continue;
				}

				users[counter] = (Integer)me.getKey();
				distances[counter] = calculateDistance((UsersModel)me.getValue(), keyRecord);

				counter++;
		    }

			// TODO: Remove this block...
			// for (int i = 0; i < distances.length; i++) {
			// System.out.println("i: "+ i +" Distances: "+ distances[i] +
			// " ; userID : "+users[i]);
			// assert(distances[i] == null);
			// }

			int n = users.length - skipIterationCounter; // Ignore common users
			double temp = 0.0;
			int temp1 = 0;

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

	public void writeNeighborsTSV(HashMap<Integer, NearestNeighbor> listOfNeighbors) throws IOException {

		String consolidatedData = "", fileToWrite = "neighbors.tsv";
		
		Set set = listOfNeighbors.entrySet();
		Iterator iter = set.iterator();
	    while(iter.hasNext()) 
	    {
	    	Map.Entry me = (Map.Entry)iter.next();
	    	
	    	NearestNeighbor obj = (NearestNeighbor)me.getValue();

			consolidatedData += obj.getPrimaryUser() + "\t" + obj.getNeighbor1() + "\t" + obj.getNeighbor2() + "\t"
					+ obj.getNeighbor3() + "\t" + obj.getNeighbor4() + "\t" + obj.getNeighbor5();

			if (iter.hasNext()) {
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
	public void createConfig() throws IOException, ParseException {

		String consolidatedData = "", fileToWrite = "config.json";
		
		File file = new File(fileToWrite);

		if (file.exists()) {
			reloadConfig();
			return;
		}

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
