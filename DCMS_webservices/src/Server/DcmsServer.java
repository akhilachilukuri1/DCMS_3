package Server;



import java.util.HashMap;

import javax.xml.ws.Endpoint;

import java.io.File;
import java.io.IOException;

import Conf.Constants;

import Conf.ServerCenterLocation.*;


import Conf.ServerCenterLocation;
/*
 * DcmsServer class creates the CORBA server instance and establishes the initial
 * communication between the client and the server for performing operations
 */
public class DcmsServer {
	static HashMap<String, DcmsServerImpl> serverRepo;
	

	

	private static void init() {

		boolean isMtlDir = new File(
				Constants.LOG_DIR + ServerCenterLocation.MTL.toString()).mkdir();
		boolean isLvlDir = new File(
				Constants.LOG_DIR + ServerCenterLocation.LVL.toString()).mkdir();
		boolean isDdoDir = new File(
				Constants.LOG_DIR + ServerCenterLocation.DDO.toString()).mkdir();
		boolean globalDir = new File(Constants.LOG_DIR + "ServerGlobal").mkdir();
	}

	public static void main(String args[]) {
		
		DcmsServerImpl mtlServer = new DcmsServerImpl(ServerCenterLocation.MTL);
		DcmsServerImpl lvlServer = new DcmsServerImpl(ServerCenterLocation.LVL);
		DcmsServerImpl ddoServer = new DcmsServerImpl(ServerCenterLocation.DDO);
		
		serverRepo = new HashMap<>();
		serverRepo.put("MTL", mtlServer);
		serverRepo.put("LVL", lvlServer);
		serverRepo.put("DDO", ddoServer);
		
		Endpoint endpointMtl = Endpoint.publish("http://localhost:3333/MTL",mtlServer);
		Endpoint endpointDdo = Endpoint.publish("http://localhost:3333/DDO",lvlServer);
		Endpoint endpointLvl = Endpoint.publish("http://localhost:3333/LVL",ddoServer);
		
		if(endpointMtl.isPublished())
			System.out.println("MTL web server is published");
		if(endpointDdo.isPublished())
			System.out.println("DDO web server is published");
		if(endpointLvl.isPublished())
			System.out.println("LVL web server is published");
		

	}
}
