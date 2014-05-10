package com.android.intellicycleV2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;

/*
* This class represents the bluetooth fragment of the app 
*/
public class BluetoothClass extends Fragment{
	public static final String ARG_SECTION_NUMBER = "section_number";
	/*
	 * Bluetooth Class Variable definitions
	 */
	Button button;
	ToggleButton toggle_discovery;
	ListView listView;
	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	ArrayAdapter<String> mArrayAdapter;
	ArrayAdapter<String> adapter;
	ArrayList<String> pairedDevicesList;
	ArrayList<String> unpairedDevicesList;
	ArrayList<String> combinedDevicesList;
	Set<BluetoothDevice> pairedDevices;
	Set<String> unpairedDevices;
	BroadcastReceiver mReceiver;
	String selectedFromList;
	String selectedFromListName;
	String selectedFromListAddress;
	BluetoothDevice selectedDevice;
	ActionBarActivity aba = new ActionBarActivity();
	
	/* 
	 * Important Bluetooth specific variables 
	 * and other important data variables
	 * 
	 */
	protected static final int SUCCESS_CONNECT = 0;
	protected static final int MESSAGE_READ = 1;
	final int STATE_CONNECTED = 2;
	final int STATE_CONNECTING = 1;
	final int STATE_DISCONNECTED = 0;
	private final UUID MY_UUID = UUID.fromString("0001101-0000-1000-8000-00805F9B34FB");
	private static final int REQUEST_ENABLE_BT = 1;
	public byte[] completeData;
	double totalDistance = 0;
	int wheelRotations=0;

	
	/*
	 * Bluetooth Handler Method
	 */
	ConnectedThread connectedThread;
	Handler mHandler = new Handler(){           
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			switch(msg.what){
				case SUCCESS_CONNECT:
					// Do Something;
					Toast.makeText(getActivity(),"CONNECTED",Toast.LENGTH_SHORT).show();
					/*
					 * For loop for test values
					 */
					connectedThread = new ConnectedThread((BluetoothSocket)msg.obj);
					listView.setVisibility(View.GONE);
					connectedThread.start();
					break;
					
				case MESSAGE_READ:
					byte[] readBuf = (byte[])msg.obj;
					int tempInt = byteToInt(readBuf[0]);
					int speedInt = byteToInt(readBuf[1]);
					int cadenceInt = byteToInt(readBuf[2]);
					int distanceInt = byteToInt(readBuf[3]);
					EditText temperatureData = (EditText)getActivity().findViewById(R.id.temperatureData);
					temperatureData.setText(Integer.toString(tempInt) + " C" );
					EditText cadenceData = (EditText)getActivity().findViewById(R.id.cadence);
					cadenceData.setText(Integer.toString(cadenceInt) + " rpm");
					EditText speedData = (EditText)getActivity().findViewById(R.id.speed_data);
					speedData.setText(Integer.toString(speedInt) + " kph");
					EditText distanceData = (EditText)getActivity().findViewById(R.id.distanceCovered);
					distanceData.setText(Double.toString(distanceMethod(distanceInt)) + " km");
			}
		}		
	};
	
	public double distanceMethod(int btDistance){
		wheelRotations = wheelRotations + btDistance;
		return RoundTo2Decimals((wheelRotations * 2.075)/1000);
	}
	
	public static int[] byteArrayToIntArray(byte[] b){
		int[] intArray = new int[b.length];
		for (int i =0 ; i< b.length; i++){
			intArray[i] = b[i] & 0xFF;
		}
	    return  intArray;
	}
	
	double RoundTo2Decimals(double val) {
       DecimalFormat df2 = new DecimalFormat("###.##");
       return Double.valueOf(df2.format(val));
	}
	
	public static int byteToInt(byte b){
		int value;
		value = b & 0xFF;
		return  value;
	}
	
	public void onCreate(Bundle savedInstance){
		super.onCreate(savedInstance);
		pairedDevicesList = new ArrayList<String>();
		unpairedDevicesList = new ArrayList<String>();
		unpairedDevices = new HashSet<String>();
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View rootview = inflater.inflate(R.layout.bluetooth, container,false);
		//Toast.makeText(getActivity(), "Fragment Created",Toast.LENGTH_SHORT).show();    		
		listView = (ListView)rootview.findViewById(R.id.listView);
		rootview.findViewById(R.id.findDevices).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mArrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,removeDuplicates(unpairedDevicesList,pairedDevicesList));
   			pairedDevices = mBluetoothAdapter.getBondedDevices();
				displayCominedDevices(mArrayAdapter);
			}
		});
	
		listView.setOnItemClickListener(new OnItemClickListener() {
	    			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
		    		selectedFromList = (String) (listView.getItemAtPosition(position));
		    		String[] parts = selectedFromList.split(" ");
		    		selectedFromListName = parts[0];
		    		selectedFromListAddress = parts[1];
		    		BluetoothDevice selectedDevice = selectedDevice(selectedFromListAddress);
		    		mBluetoothAdapter.cancelDiscovery();
		    		ConnectThread ct = new ConnectThread(selectedDevice);
		    		ct.start();
		    	}
		  });
		
		return rootview;
	}   
	
	public void onActivityCreared(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		/*RelativeLayout  relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.RR);
		relativeLayout.setBackgroundResource(R.drawable.bicycle2);*/
	}
	  		
	
	public void onStart(){
		super.onStart();
		//Toast.makeText(getActivity(), "Fragment started",Toast.LENGTH_SHORT).show();
	}    	

	public void onResume(){
		super.onStart();
		//Toast.makeText(getActivity(), "Fragment Resumed",Toast.LENGTH_SHORT).show();
		   		
	}
	
	public void onStop(){
		super.onStart();
		//Toast.makeText(getActivity(), "Fragment Stoped",Toast.LENGTH_SHORT).show();
		disableBT();
	}
	

	public void enableBT(){
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
			Toast.makeText(getActivity(), "Bluetooth is not suppourted on Device",Toast.LENGTH_SHORT).show();
		}
		
		if (!mBluetoothAdapter.isEnabled()) {
		   Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		   startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		   int resultCode = Activity.RESULT_OK;
		    if(resultCode < 1){
		    	Toast.makeText(getActivity(), "Please Accept Enabling Bluetooth Request!", Toast.LENGTH_LONG).show();
		    }
		   	else{
		    	Toast.makeText(getActivity(), "Enabling Bluetooth FAILED!", Toast.LENGTH_SHORT).show();
	    	}
		}
	}
	
	public void disableBT(){
		if (mBluetoothAdapter.isEnabled()){
			mBluetoothAdapter.disable();
		}
	}
	
	/*
	 * Display Helper Methods
	 */
	public void displayCominedDevices(ArrayAdapter<String> mArrayAdapter){
		displayPairedDevices();
		displayDetectedDevices();
		listView.setAdapter(mArrayAdapter);
		listView.setVisibility(View.VISIBLE);

		if (count == 0){
			Toast.makeText(getActivity(),"Running Tests",Toast.LENGTH_LONG).show();
			for(int i =0; i<= 999; i++){
				EditText temperatureData = (EditText)getActivity().findViewById(R.id.temperatureData);
				temperatureData.setText(Integer.toString(i));
				EditText cadenceData = (EditText)getActivity().findViewById(R.id.cadence);
				cadenceData.setText(Integer.toString(i));
				EditText speedData = (EditText)getActivity().findViewById(R.id.speed_data);
				speedData.setText(Integer.toString(i));
				EditText distanceData = (EditText)getActivity().findViewById(R.id.distanceCovered);
				distanceData.setText("XXX");
			}
		}
		count = 1;
	}
	
	
	public void displayPairedDevices(){
		// If there are paired devices
		enableBT();
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		        // Add the name and address to an array adapter to show in a ListView
		    	String s = " ";
		    	String deviceName = device.getName();
		    	String deviceAddress = device.getAddress();
		    	pairedDevicesList.add(deviceName + s + deviceAddress +" \n");
		    }
		}
	}
	
	public void displayDetectedDevices(){
		mBluetoothAdapter.startDiscovery();
		
		// Create a BroadcastReceiver for ACTION_FOUND
		mReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		        // When discovery finds a device
		        if(BluetoothDevice.ACTION_FOUND.equals(action)){
		        	BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		        	String deviceName = device.getName();
		        	String deviceAddress = device.getAddress();
		        	String s = " ";
		        	unpairedDevices.add(deviceName + s + deviceAddress +" \n");
		        	unpairedDevicesList = new ArrayList<String>(unpairedDevices);
		        	Toast.makeText(getActivity(), unpairedDevicesList.toString(), Toast.LENGTH_LONG).show();
		        }
			}
		};
  	}
	
	/* 
	 * Select bluetooth Device from list
	 */    	
	public BluetoothDevice selectedDevice(String deviceAddress){
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothDevice device;		
		device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
		return device;
	}
	
	public ArrayList<String> removeDuplicates(ArrayList<String> s1, ArrayList<String> s2){
		combinedDevicesList =  new ArrayList<String>();
		combinedDevicesList.addAll(s1);
		combinedDevicesList.addAll(s2);
		Set Unique_set = new HashSet(combinedDevicesList);
		combinedDevicesList = new ArrayList<String>(Unique_set);
		/*Debugging 
		Toast.makeText(getApplication(),"Combined List" + combinedDevicesList.toString(),Toast.LENGTH_LONG).show(); */
		return combinedDevicesList;
	}
	
	
	/*
	 * Bluetooth Connection Threads
	 */

	int count = 0;
	 
	public class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
       private final BluetoothDevice mmDevice;
	    public ConnectThread(BluetoothDevice device) {
	        
	    	/*
	         *  Use a temporary object that is later assigned to mmSocket,
	         *  because mmSocket is final    	         
	         */
	        
	        BluetoothSocket tmp = null;
	        
	        mmDevice = device;
	        	        
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        try {
	            // MY_UUID is the app's UUID string, also used by the server code
	            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
	        } catch (IOException e) { }
	        mmSocket = tmp;
	    }
	 
	    public void run() {
	        // Cancel discovery because it will slow down the connection
	        mBluetoothAdapter.cancelDiscovery();
	 
	        try {
           // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	            mmSocket.connect();
	        } catch (IOException connectException) {
	            // Unable to connect; close the socket and get out
	            try {
	                mmSocket.close();
	            } catch (IOException closeException) {
	            	Toast.makeText(getActivity(), "Connecting to device failed!", Toast.LENGTH_LONG).show();
	            }
	            	return;
	        }
		 
	        	// Do work to manage the connection (in a separate thread)
	           	mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();
		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			try {
				mmSocket.close();
		       } catch (IOException e) { }
		}
		
	}
	
	private class ConnectedThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final InputStream mmInStream;
	    private final OutputStream mmOutStream;
	    
	    public ConnectedThread(BluetoothSocket socket) {
	        mmSocket = socket;
	        InputStream tmpIn = null;
	        OutputStream tmpOut = null;
	 
	        // Get the input and output streams, using temp objects because
	        // member streams are final
	        try {
	            tmpIn = socket.getInputStream();
	            tmpOut = socket.getOutputStream();
	        } catch (IOException e) { }
	 
	        mmInStream = tmpIn;
	        mmOutStream = tmpOut;
	    }
	    
	    	           
       public void run() {
       	byte[] buffer; // buffer store for the stream
 	        int bytes; // bytes returned from read()
	        // Keep listening to the InputStream until an exception occurs
	        while (true) {
	            try {
	            	// Read from the InputStream
	                buffer = new byte[4];
	                	mmOutStream.write(253);
	                	bytes = mmInStream.read(buffer,0,1);
	                	mmOutStream.write(254);
	                	bytes = mmInStream.read(buffer,1,1);
	                	mmOutStream.write(255);
	                	bytes = mmInStream.read(buffer,2,1);
	                	mmOutStream.write(252);
	                	bytes = mmInStream.read(buffer,3,1);
	                	mHandler.obtainMessage(MESSAGE_READ, buffer).sendToTarget();
	                }
	             	catch (IOException e) {
	             		break;
	             }
	        }
	     }
	    /* Call this from the main activity to send data to the remote device */
	    public void write(byte[] bytes) {
	        try {
	            mmOutStream.write(bytes);
	        } catch (IOException e) { }
	    }
	}

}
