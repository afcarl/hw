package org.hedgewars.mobile.EngineProtocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class EngineProtocolNetwork implements Runnable{

	public static final String GAMEMODE_LOCAL = "TL";
	public static final String GAMEMODE_DEMO = "TD";
	public static final String GAMEMODE_NET = "TN";
	public static final String GAMEMODE_SAVE = "TS";
	
	public static final int BUFFER_SIZE = 255; //From iOS code which got it from the origional frontend
	
	public static final int MODE_GENLANDPREVIEW = 0;
	public static final int MODE_GAME = 1;

	private ServerSocket serverSocket;
	private InputStream input;
	private OutputStream output;
	public int port;
	private final GameConfig config;

	public EngineProtocolNetwork(GameConfig _config){
		config = _config;
		try {
			serverSocket = new ServerSocket(0);
			port = serverSocket.getLocalPort();
			Thread ipcThread = new Thread(this, "IPC - Thread");			
			ipcThread.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		//if(mode == MODE_GENLANDPREVIEW) genLandPreviewIPC();
		/*else if (mode == MODE_GAME)*/ gameIPC();
	}
	
	private void gameIPC(){
		try{
			Socket sock = serverSocket.accept();
			input = sock.getInputStream();
			output = sock.getOutputStream();
			
			boolean clientQuit = false;
			int msgSize = 0;
			byte[] buffer = new byte[BUFFER_SIZE];

			while(!clientQuit){
				msgSize = 0;

				input.read(buffer, 0, 1);
				msgSize = buffer[0];

				input.read(buffer, 0, msgSize);

				switch(buffer[0]){
				case 'C'://game init
					config.sendToEngine(this);
					break;
				case '?'://ping - pong
					sendToEngine("!");
					break;
				case 'E'://error - quits game

					break;
				case 'e':

					break;
				case 'i'://game statistics
					switch(buffer[1]){
					case 'r'://winning team
						break;
					case 'D'://best shot
						break;
					case 'k'://best hedgehog
						break;
					case 'K'://# hogs killed
						break;
					case 'H'://team health graph
						break;
					case 'T':// local team stats
						break;
					case 'P'://teams ranking
						break;
					case 's'://self damage
						break;
					case 'S'://friendly fire
						break;
					case 'B'://turn skipped
						break;
					default:

					}
					break;
				case 'q'://game ended remove save file

					break;
				case 'Q'://game ended but not finished

					break;
				}

			}

		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void sendToEngine(String s){
		int length = s.length();
		
		try {
			output.write(length);
			output.write(s.getBytes(), 0, length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
}
