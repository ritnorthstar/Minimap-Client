package com.northstar.minimap;

import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

/**
 * Created by Benjin on 11/9/13.
 */
public class Communicator {
    private URL serverIpAddress = null;

    public void setServerIP(URL address)
    {
        serverIpAddress = address;
        Globals.log("Address set to '" + address.toString() + "'");
    }

    public URL getServerIP()
    {
        return serverIpAddress;
    }

    public String getMapsJson()
    {
        Globals.log(">> getting maps json from " + serverIpAddress.toString());
        String output = "";

        new GetMapsJsonTask().execute(serverIpAddress);

        return output;
    }

    private class GetMapsJsonTask extends AsyncTask<URL, Void, String>
    {
        private HttpURLConnection connection = null;

        protected String doInBackground(URL... url)
        {
            return GET(url[0]);
        }

        protected void onPostExecute(String json)
        {
            Globals.log("Json response (oPE): " + json);
        }
    }

    public String GET(URL url){
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url.toString()));
            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = readStream(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Globals.log(e.getLocalizedMessage());
        }

        return result;
    }

    private String readStream(InputStream in) throws IOException
    {
        Globals.log("entering readStream");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = reader.readLine();
        String output = line;

        while((line = reader.readLine()) != null)
            output += line;

        return output;
    }
}
