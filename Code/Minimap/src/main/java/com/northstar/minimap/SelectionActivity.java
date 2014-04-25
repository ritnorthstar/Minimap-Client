package com.northstar.minimap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SelectionActivity extends Activity {
    
    private String[] mapChoices = null;
    private String[] teamChoices = null;
    
    private Spinner mapSpinner;
    private Spinner teamSpinner;
    private EditText usernameTextbox;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        
        mapSpinner = (Spinner) findViewById(R.id.map_spinner);
        teamSpinner = (Spinner) findViewById(R.id.team_spinner);
        usernameTextbox = (EditText) findViewById(R.id.edit_username);
        
        Globals state = (Globals)getApplicationContext();
        
        CallbackListener map = new MapCallback(this);
        CallbackListener team = new TeamCallback(this);
        
        state.comm.getMapsJson(map);
        state.comm.getTeamsJson(team);
    }

    public void checkRegister() {
        boolean registered = true;
        Globals state = (Globals)getApplicationContext();
        String JSONUsersString = state.data.usersJson;

        try {
            boolean encountered = false;
            JSONArray users = new JSONArray(JSONUsersString);
            teamChoices = new String[users.length()];
            for (int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);
                String registeredUser = user.getString("Name");
                if (usernameTextbox.getText().toString().equals(registeredUser)) {
                    encountered = true;
                }
            }

            if (!encountered) {
                registered = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (registered) {
            Toast.makeText(this, R.string.username_unavailable, Toast.LENGTH_LONG).show();
        } else {
            JSONObject userJson = new JSONObject();
            try {
                userJson.put("Name", usernameTextbox.getText().toString());
                userJson.put("TeamId", state.data.teamID);
                userJson.put("X", 0);
                userJson.put("Y", 0);
                userJson.put("Z", 0);

                CallbackListener registerUser = new RegisterUserCallback(this);
                state.comm.registerUsers(registerUser, userJson.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void goToMap() {
        Intent mapIntent = new Intent(this, MapActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(MapActivity.KEY_ENV, MapActivity.ENV_PRODUCTION);
        mapIntent.putExtras(bundle);
        startActivity(mapIntent);
    }

    public void registerChoices(View view) {
        Globals state = (Globals)getApplicationContext();

        try {
            JSONArray maps = new JSONArray(state.data.mapsJson);
            JSONArray teams = new JSONArray(state.data.teamsJson);

            JSONObject map = maps.getJSONObject(mapSpinner.getSelectedItemPosition());
            JSONObject team = teams.getJSONObject(teamSpinner.getSelectedItemPosition());

            state.data.mapID = map.getString("Id");
            state.data.teamID = team.getString("Id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CallbackListener user = new UserCallback(this);
        state.comm.getUsersJson(user);
    }

    public void setMaps() {
        Globals state = (Globals)getApplicationContext();
        String JSONMapsString = state.data.mapsJson;
        Log.d("JP", "MapJSON: " + JSONMapsString);
        
        try {
            JSONArray maps = new JSONArray(JSONMapsString);
            mapChoices = new String[maps.length()];
            for(int i = 0; i < maps.length(); i++){
                JSONObject map = maps.getJSONObject(i);
                String mapChoice = map.getString("Name");
                mapChoices[i] = mapChoice;
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        ArrayAdapter<String> mapAdapter =
                new ArrayAdapter<String>(this, R.layout.selection_spinner_item, mapChoices);
        mapSpinner.setAdapter(mapAdapter);
    }
    
    public void setTeams() {
        Globals state = (Globals)getApplicationContext();
        String JSONTeamsString = state.data.teamsJson;
        Log.d("JP", "TeamJSON: " + JSONTeamsString);
        
        try {
            JSONArray teams = new JSONArray(JSONTeamsString);
            teamChoices = new String[teams.length()];
            for (int i = 0; i < teams.length(); i++) {
                JSONObject team = teams.getJSONObject(i);
                String teamChoice = team.getString("Name");
                teamChoices[i] = teamChoice;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        ArrayAdapter<String> teamAdapter =
                new ArrayAdapter<String>(this, R.layout.selection_spinner_item, teamChoices);
        teamSpinner.setAdapter(teamAdapter);
    }
}
