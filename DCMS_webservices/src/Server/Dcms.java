package Server;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService(name="Dcms")
@SOAPBinding(style = Style.RPC)
interface Dcms {

	/**
	 * Once the teacher record is created, createTRRecord function returns the
	 * record ID of the teacher record created to the client
	 * 
	 * @param managerID
	 *            gets the managerID
	 * @param teacherField
	 *            values of the teacher attribute concatenated by the comma which
	 *            are received from the client
	 * 
	 */
@WebMethod
	String createTRecord(String managerID, String teacher);

	/**
	 * Once the student record is created, the function createSRecord returns the
	 * record ID of the student record created to the client
	 * 
	 * @param managerID
	 *            gets the managerID
	 * @param studentFields
	 *            values of the student attribute concatenated by the comma which
	 *            are received the client
	 * 
	 */
@WebMethod
	String createSRecord(String managerID, String student);

	/**
	 * invokes record count on the corresponding MTL/LVL/DDO server to get record
	 * count on all the servers
	 * 
	 */
@WebMethod
	String getRecordCount();

	/**
	 * The edit record function performs the edit operation on the server and
	 * returns the appropriate message
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
@WebMethod
	String editRecord(String managerID, String recordID, String fieldname, String newvalue);

	/**
	 * Performs the transfer record to the remoteCenterServer by sending the
	 * appropriate packet to the UDPRequestProvider thread
	 *
	 * 
	 * @param managerID
	 *            gets the managerID
	 * @param recordID
	 *            gets the recordID to be edited
	 * @param remoteCenterServerName
	 *            gets the location to transfer the recordID from the client
	 */
@WebMethod
	String transferRecord(String ManagerID, String recordID, String remoteCenterServerName);

}