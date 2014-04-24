package com.northstar.minimap;

import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjin on 11/9/13.
 */
public class Communicator {
    private URL serverIpAddress = null;
    private ReceivedData dataHolder;

    public void setServerIP(URL address) {
        serverIpAddress = address;
        Globals.log("Address set to '" + address.toString() + "'");
    }
    
    public void setDataHolder(ReceivedData dataHolder) {
        this.dataHolder = dataHolder;
    }

    public URL getServerIP()
    {
        return serverIpAddress;
    }

    public void getMapsJson(CallbackListener l) {
        Globals.log(">> getting maps json from " + serverIpAddress.toString());
        try {
            new GetMapsJsonTask(l).execute(new URL(serverIpAddress, "/api/maps"));
        } catch (MalformedURLException e) {}
    }

    private class GetMapsJsonTask extends AsyncTask<URL, Void, String> {
        private CallbackListener listener;

        public GetMapsJsonTask(CallbackListener l){
            this.listener = l;
        }

        protected String doInBackground(URL... url) {
            return GET(url[0]);
        }

        protected void onPostExecute(String json) {
            Globals.log("Json response (oPE): " + json);
            dataHolder.mapsJson = json;
            listener.jsonCallback();
        }
    }
    
    public void getTeamsJson(CallbackListener l) {
        Globals.log(">> getting teams json from " + serverIpAddress.toString());
        try {
            new GetTeamsJsonTask(l).execute(new URL(serverIpAddress, "/api/teams"));
        } catch (MalformedURLException e) {}
    }

    private class GetTeamsJsonTask extends AsyncTask<URL, Void, String> {
        private CallbackListener listener;

        public GetTeamsJsonTask(CallbackListener l){
            this.listener = l;
        }

        protected String doInBackground(URL... url) {
            return GET(url[0]);
        }

        protected void onPostExecute(String json) {
            Globals.log("Json response (oPE): " + json);
            dataHolder.teamsJson = json;
            listener.jsonCallback();
        }
    }
    
    public void getUsersJson(CallbackListener l) {
        Globals.log(">> getting users json from " + serverIpAddress.toString());
        try {
            new GetUsersJsonTask(l).execute(new URL(serverIpAddress, "/api/users"));
        } catch (MalformedURLException e) {}
    }

    private class GetUsersJsonTask extends AsyncTask<URL, Void, String> {
        private CallbackListener listener;

        public GetUsersJsonTask(CallbackListener l){
            this.listener = l;
        }

        protected String doInBackground(URL... url) {
            return GET(url[0]);
        }

        protected void onPostExecute(String json) {
            Globals.log("Json response (oPE): " + json);
            dataHolder.usersJson = json;
            listener.jsonCallback();
        }
    }
    
    public void registerUser(CallbackListener l, String json){
    	Globals.log(">> getting users json from " + serverIpAddress.toString());
        try {
            new registerUserTask(l, json).execute(new URL(serverIpAddress, "/api/users"));
        } catch (MalformedURLException e) {}
    }
    
    private class registerUserTask extends AsyncTask<URL, Void, String> {
        private CallbackListener listener;
        private String json;

        public registerUserTask(CallbackListener l, String json){
            this.listener = l;
            this.json = json;
        }

        protected String doInBackground(URL... url) {
            return jsonPOST(url[0], json);
        }

        protected void onPostExecute(String json) {
            Globals.log("Json response (oPE): " + json);
            dataHolder.userJson = json;
            try {
				JSONObject user = new JSONObject(json);
				dataHolder.userID = user.getString("Id");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            listener.jsonCallback();
        }
    }
    
    public void updateUser(CallbackListener l, String json){
    	Globals.log(">> getting users json from " + serverIpAddress.toString());
        try {
            new updateUserTask(l, json).execute(new URL(serverIpAddress, "/api/users/" + dataHolder.userID));
        } catch (MalformedURLException e) {}
    }
    
    private class updateUserTask extends AsyncTask<URL, Void, String> {
        private CallbackListener listener;
        private String json;

        public updateUserTask(CallbackListener l, String json){
            this.listener = l;
            this.json = json;
        }

        protected String doInBackground(URL... url) {
            return jsonPUT(url[0], json);
        }

        protected void onPostExecute(String json) {
            Globals.log("Json response (oPE): " + json);
            dataHolder.userJson = json;
            listener.jsonCallback();
        }
    }

    public String GET(URL url) {
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url.toString()));
            inputStream = httpResponse.getEntity().getContent();

            if (inputStream != null) {
                result = readStream(inputStream);
            } else {
                result = "Did not work!";
            }

        } catch (Exception e) {
            Globals.log(e.getLocalizedMessage());
        }

        return result;
    }
    
    public String jsonPOST(URL url, String json) {
    	InputStream inputStream = null;
    	String result = "";
    	try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url.toString());
            
            StringEntity se = new StringEntity(json);  
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(se);
            
            HttpResponse httpResponse = httpclient.execute(httppost);
            inputStream = httpResponse.getEntity().getContent();

            if (inputStream != null) {
                result = readStream(inputStream);
            } else {
                result = "Did not work!";
            }

        } catch (Exception e) {
            Globals.log(e.getLocalizedMessage());
        }
    	
    	return result;
    }
    
    public String jsonPUT(URL url, String json) {
    	InputStream inputStream = null;
    	String result = "";
    	try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPut httpput = new HttpPut(url.toString());
            
            StringEntity se = new StringEntity(json);  
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpput.setEntity(se);
            
            HttpResponse httpResponse = httpclient.execute(httpput);
            inputStream = httpResponse.getEntity().getContent();

            if (inputStream != null) {
                result = readStream(inputStream);
            } else {
                result = "Did not work!";
            }

        } catch (Exception e) {
            Globals.log(e.getLocalizedMessage());
        }
    	
    	return result;
    }

    private String readStream(InputStream in) throws IOException {
        Globals.log("entering readStream");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = reader.readLine();
        String output = line;

        while ((line = reader.readLine()) != null) {
            output += line;
        }

        return output;
    }
}
