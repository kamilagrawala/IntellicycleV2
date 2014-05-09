/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.intellicycleV2;

import java.lang.*;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.android.intellicycleV2.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

@SuppressWarnings("unused")

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });
        

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    	// When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
            
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        
        int alreadySelected=0;
        @Override
        public Fragment getItem(int i){
            switch (i) {
               	case 0:
                    // The first section of the app is the most interesting -- it offers
                    // a launchpad into the other demonstrations in this example application.
                    return new LaunchpadSectionFragment();

               	case 1:
               		return new BluetoothClass();
              
               	default:
                    // The GPS section of the app .
               		return new GpsClass();
            	}
        }
        
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public String getPageTitle(int position) {
        	if (position == 0 ){
        		return "Welcome Page";
        	}
        	if (position == 1 ){
        		return "Sensors";
        	}
        	
        	return "GPS";
        }
    }

    /**
     * A fragment that launches other parts of the demo application.
     */
    public static class LaunchpadSectionFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_launchpad, container, false);

            // Demonstration of a collection-browsing activity.
            rootView.findViewById(R.id.test_button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), CollectionDemoActivity.class);
                            startActivity(intent);
                        }
                    });

            // Demonstration of navigating to external activities.
            rootView.findViewById(R.id.demo_external_activity)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Create an intent that asks the user to pick a photo, but using
                            // FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET, ensures that relaunching
                            // the application from the device home screen does not return
                            // to the external activity.
                            Intent externalActivityIntent = new Intent(Intent.ACTION_PICK);
                            externalActivityIntent.setType("image/*");
                            externalActivityIntent.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            startActivity(externalActivityIntent);
                        }
                    });

            return rootView;
        }
    }
    
    
     /*
     * This class represents the bluetooth fragment of the app 
     */
    public static class BluetoothClass extends Fragment{
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
    
    public static class GpsClass extends Fragment{
    	View rootView;
    	private GoogleMap mMap;
    	
    	public static final String ARG_SECTION_NUMBER = "section_number";
    		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
    		if (rootView != null){
    				((ViewGroup) rootView.getParent()).removeView(rootView);
    			}
    			try{
    				rootView = inflater.inflate(R.layout.gps, container,false);
    				mMap = ((SupportMapFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.mymap)).getMap();
    				mMap.setMyLocationEnabled(true);
    				
    			} catch(InflateException e){
    			 
    			}
    			return rootView;
    		}
    }
  
    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_dummy, container, false);
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                    getString(R.string.dummy_section_text, args.getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
}
