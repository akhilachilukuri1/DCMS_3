package Server;




import java.io.IOException;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import Conf.Constants;
import Conf.LogManager;
import Conf.ServerCenterLocation;
import Models.Record;
import Models.Student;
import Models.Teacher;

/**
 * 
 * DcmsServerImpl performs all the functionality for MTL,LVL and DDO server
 * Locations
 *
 */
@WebService(endpointInterface = "Server.Dcms",portName="DcmsPort",serviceName="DcmsService")
@SOAPBinding(style = Style.RPC)
public class DcmsServerImpl implements Dcms  {
	

	LogManager logManager;
	ServerUDP serverUDP;
	String IPaddress;
	public HashMap<String, List<Record>> recordsMap;
	int studentCount = 0;
	int teacherCount = 0;
	String recordsCount;
	String location;

	public DcmsServerImpl(ServerCenterLocation loc) {
		logManager = new LogManager(loc.toString());
		recordsMap = new HashMap<>();
		serverUDP = new ServerUDP(loc, logManager.logger, this);
		serverUDP.start();
		location = loc.toString();
		setIPAddress(loc);
	}



	private void setIPAddress(ServerCenterLocation loc) {
		switch (loc) {
		case MTL:
			IPaddress = Constants.MTL_SERVER_ADDRESS;
			break;
		case LVL:
			IPaddress = Constants.LVL_SERVER_ADDRESS;
			break;
		case DDO:
			IPaddress = Constants.DDO_SERVER_ADDRESS;
			break;
		}
	}

	/* (non-Javadoc)
	 * @see Server.Dcms#createTRecord(java.lang.String, java.lang.String)
	 */

	@Override
	public String createTRecord(String managerID, String teacher) {

		String temp[] = teacher.split(",");
		// String managerID = temp[0];
		String teacherID = "TR" + (++teacherCount);
		String firstName = temp[0];
		String lastname = temp[1];
		String address = temp[2];
		String phone = temp[3];
		String specialization = temp[4];
		String location = temp[5];
		Teacher teacherObj = new Teacher(managerID, teacherID, firstName, lastname,
				address, phone, specialization, location);
		String key = lastname.substring(0, 1);
		String message = addRecordToHashMap(key, teacherObj, null);
		System.out.println("teacher is added " + teacherObj + " with this key " + key
				+ " by Manager " + managerID);
		logManager.logger.log(Level.INFO, "Teacher record created " + teacherID
				+ " by Manager : " + managerID);
		return teacherID;

	}

	/* (non-Javadoc)
	 * @see Server.Dcms#createSRecord(java.lang.String, java.lang.String)
	 */

	@Override
	public String createSRecord(String managerID, String student) {

		String temp[] = student.split(",");
		// String managerID = temp[0];
		String firstName = temp[0];
		String lastName = temp[1];
		String CoursesRegistered = temp[2];
		List<String> courseList = putCoursesinList(CoursesRegistered);
		String status = temp[3];
		String statusDate = temp[4];
		String studentID = "SR" + (++studentCount);
		Student studentObj = new Student(managerID, studentID, firstName, lastName,
				courseList, status, statusDate);
		String key = lastName.substring(0, 1);
		String message = addRecordToHashMap(key, null, studentObj);
		if (message.equals("success")) {
			System.out.println(" Student is added " + studentObj + " with this key "
					+ key + " by Manager " + managerID);
			logManager.logger.log(Level.INFO, "Student record created " + studentID
					+ " by manager : " + managerID);
		}
		return studentID;
	}

	/**
	 * Adds the Teacher and Student to the HashMap the function addRecordToHashMap
	 * returns the success message, if the student / teacher record is created
	 * successfully else returns Error message
	 * 
	 * @param key
	 *            gets the key of the recordID stored in the HashMap
	 * @param teacher
	 *            gets the teacher object if received from createTRecord function
	 * @param student
	 *            gets the student object if received from createSRecord function
	 *            which are received the respective functions.
	 * 
	 */
	@WebMethod(exclude=true)
	public synchronized String addRecordToHashMap(String key, Teacher teacher,
			Student student) {
		String message = "Error";
		if (teacher != null) {
			List<Record> recordList = recordsMap.get(key);
			if (recordList != null) {
				recordList.add(teacher);
			} else {
				List<Record> records = new ArrayList<Record>();
				records.add(teacher);
				recordList = records;
			}
			recordsMap.put(key, recordList);
			message = "success";
		}

		if (student != null) {
			List<Record> recordList = recordsMap.get(key);
			if (recordList != null) {
				recordList.add(student);
			} else {
				List<Record> records = new ArrayList<Record>();
				records.add(student);
				recordList = records;
			}
			recordsMap.put(key, recordList);
			message = "success";
		}

		return message;
	}

	/**
	 *
	 * returns the current server record count
	 * 
	 */

	private synchronized int getCurrServerCnt() {
		int count = 0;
		for (Map.Entry<String, List<Record>> entry : this.recordsMap.entrySet()) {
			List<Record> list = entry.getValue();
			count += list.size();
		}
		return count;
	}

	/* (non-Javadoc)
	 * @see Server.Dcms#getRecordCount()
	 */

	@Override
	public String getRecordCount() {
		String recordCount = null;
		UDPRequestProvider[] req = new UDPRequestProvider[2];
		int counter = 0;
		ArrayList<String> locList = new ArrayList<>();
		locList.add("MTL");
		locList.add("LVL");
		locList.add("DDO");
		for (String loc : locList) {
			if (loc == this.location) {
				recordCount = loc + " " + getCurrServerCnt();
			} else {
				try {
					req[counter] = new UDPRequestProvider(
							DcmsServer.serverRepo.get(loc), "GET_RECORD_COUNT",
							null);
				} catch (IOException e) {
					logManager.logger.log(Level.SEVERE, e.getMessage());
				}
				req[counter].start();
				counter++;
			}
		}
		for (UDPRequestProvider request : req) {
			try {
				request.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			recordCount += " , " + request.getRemoteRecordCount().trim();
		}
		return recordCount;
	}

	/* (non-Javadoc)
	 * @see Server.Dcms#editRecord(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */

	@Override
	public String editRecord(String managerID, String recordID, String fieldname,
			String newvalue) {
		String type = recordID.substring(0, 2);
		if (type.equals("TR")) {
			return editTRRecord(managerID, recordID, fieldname, newvalue);
		} else if (type.equals("SR")) {
			return editSRRecord(managerID, recordID, fieldname, newvalue);
		}
		logManager.logger.log(Level.INFO, "Record edit successful");
		return "Operation not performed!";
	}

	/* (non-Javadoc)
	 * @see Server.Dcms#transferRecord(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String transferRecord(String ManagerID, String recordID,
			String remoteCenterServerName) {
		String type = recordID.substring(0, 2);
		UDPRequestProvider req = null;
		try {
			System.out
					.println("Transferring record to :: " + remoteCenterServerName);
			Record record = getRecordForTransfer(recordID);
			if (record == null) {
				return "RecordID unavailable!";
			}else if(remoteCenterServerName.equals(this.location)) {
				return "Please enter a valid location to transfer. The record is already present in "+location;
			}
			req = new UDPRequestProvider(
					DcmsServer.serverRepo.get(remoteCenterServerName),
					"TRANSFER_RECORD", record);
		} catch (IOException e) {
			logManager.logger.log(Level.SEVERE, e.getMessage());
		}
		req.start();
		try {
			req.join();
			if (removeRecordAfterTransfer(recordID) == "success") {
				System.out.println(recordsMap);
				logManager.logger.log(Level.INFO, "Record created in  "
						+ remoteCenterServerName + "  and removed from " + location);
				return "Record created in " + remoteCenterServerName
						+ "and removed from " + location;
			}
		} catch (Exception e) {

		}

		return "Transfer record operation unsuccessful!";
	}

	private synchronized String removeRecordAfterTransfer(String recordID) {
		for (Entry<String, List<Record>> element : recordsMap.entrySet()) {
			List<Record> mylist = element.getValue();
			for (int i = 0; i < mylist.size(); i++) {
				if (mylist.get(i).getRecordID().equals(recordID)) {
					mylist.remove(i);
				}
			}
			recordsMap.put(element.getKey(), mylist);
		}
		return "success";
	}

	private synchronized Record getRecordForTransfer(String recordID) {
		for (Entry<String, List<Record>> value : recordsMap.entrySet()) {
			List<Record> mylist = value.getValue();
			Optional<Record> record = mylist.stream()
					.filter(x -> x.getRecordID().equals(recordID)).findFirst();
			if (recordID.contains("TR")) {
				if (record.isPresent())
					return (Teacher) record.get();
			} else {
				if (record.isPresent())
					return (Student) record.get();
			}
		}
		return null;
	}

	/**
	 * The putCoursesinList function adds the newCourses to the List
	 * 
	 * @param newvalue
	 *            gets the newcourses value and adds to the list
	 *
	 */
	@WebMethod(exclude=true)
	public synchronized List<String> putCoursesinList(String newvalue) {
		String[] courses = newvalue.split("//");
		ArrayList<String> courseList = new ArrayList<>();
		for (String course : courses)
			courseList.add(course);
		return courseList;
	}

	/**
	 * The editSRRecord function performs the edit operation on the student record
	 * and returns the appropriate message
	 * 
	 * @param managerID
	 *            gets the managerID
	 * @param recordID
	 *            gets the recordID to be edited
	 * @param fieldname
	 *            gets the fieldname to be edited for the given recordID
	 * @param newvalue
	 *            gets the newvalue to be replaced to the given fieldname from the
	 *            client
	 */

	private synchronized String editSRRecord(String maangerID, String recordID,
			String fieldname, String newvalue) {
		for (Entry<String, List<Record>> value : recordsMap.entrySet()) {
			List<Record> mylist = value.getValue();
			Optional<Record> record = mylist.stream()
					.filter(x -> x.getRecordID().equals(recordID)).findFirst();
			if (record.isPresent()) {
				if (record.isPresent() && fieldname.equals("Status")) {
					((Student) record.get()).setStatus(newvalue);
					logManager.logger.log(Level.INFO,
							maangerID + "Updated the records\t" + location);
					return "Updated record with status :: " + newvalue;
				} else if (record.isPresent() && fieldname.equals("StatusDate")) {
					((Student) record.get()).setStatusDate(newvalue);
					logManager.logger.log(Level.INFO,
							maangerID + "Updated the records\t" + location);
					return "Updated record with status date :: " + newvalue;
				} else if (record.isPresent()
						&& fieldname.equals("CoursesRegistered")) {
					List<String> courseList = putCoursesinList(newvalue);
					((Student) record.get()).setCoursesRegistered(courseList);
					return "Updated record with courses :: " + courseList;
				} else {
					System.out.println("Record with " + recordID + " not found");
					logManager.logger.log(Level.INFO,
							"Record with " + recordID + "not found!" + location);
					return "Record with " + recordID + " not found";
				}
			}
		}
		return "Record with " + recordID + "not found!";
	}

	/**
	 * The editTRRecord function performs the edit operation on the Teacher record
	 * and returns the appropriate message
	 * 
	 * @param managerID
	 *            gets the managerID
	 * @param recordID
	 *            gets the recordID to be edited
	 * @param fieldname
	 *            gets the fieldname to be edited for the given recordID
	 * @param newvalue
	 *            gets the newvalue to be replaced to the given fieldname from the
	 *            client
	 */

	private synchronized String editTRRecord(String managerID, String recordID,
			String fieldname, String newvalue) {
		for (Entry<String, List<Record>> val : recordsMap.entrySet()) {

			List<Record> mylist = val.getValue();
			Optional<Record> record = mylist.stream()
					.filter(x -> x.getRecordID().equals(recordID)).findFirst();

			if (record.isPresent()) {
				if (record.isPresent() && fieldname.equals("Phone")) {
					((Teacher) record.get()).setPhone(newvalue);
					logManager.logger.log(Level.INFO,
							managerID + "Updated the records\t" + location);
					return "Updated record with Phone :: " + newvalue;
				}

				else if (record.isPresent() && fieldname.equals("Address")) {
					((Teacher) record.get()).setAddress(newvalue);
					logManager.logger.log(Level.INFO,
							managerID + "Updated the records\t" + location);
					return "Updated record with address :: " + newvalue;
				}

				else if (record.isPresent() && fieldname.equals("Location")) {
					((Teacher) record.get()).setLocation(newvalue);
					logManager.logger.log(Level.INFO,
							managerID + "Updated the records\t" + location);
					return "Updated record with location :: " + newvalue;
				} else {
					System.out.println("Record with " + recordID + " not found");
					logManager.logger.log(Level.INFO,
							"Record with " + recordID + "not found!" + location);
					return "Record with " + recordID + " not found";
				}
			}
		}
		return "Record with " + recordID + " not found";
	}
}