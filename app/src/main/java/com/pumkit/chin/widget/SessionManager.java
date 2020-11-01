package com.pumkit.chin.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.pumkit.chin.chinchinso.MainActivity;

import java.util.HashMap;

public class SessionManager extends Activity {
	// Shared Preferences
	SharedPreferences pref;

	// Editor for Shared preferences
	Editor editor;

	// Context
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Sharedpref file name
	private static final String PREF_NAME = "Funsumer-success";

	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLoggedIn";

	// User name (make variable public to access from outside)
	public static final String KEY_NAME = "my_id";
	public static final String KEY_GENDER = "mygender";
	public static final String KEY_INVATE_CNT = "invite_cnt";

	// Email address (make variable public to access from outside)
	public static final String KEY_EMAIL = "email";

	// Constructor
	public SessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	/**
	 * Create login session
	 * */
	public void createLoginSession(String my_id, String gender) {
		editor.putBoolean(IS_LOGIN, true); // Storing login value as TRUE
		editor.putString(KEY_NAME, my_id); // Storing name in pref
		editor.putString(KEY_GENDER, gender);
		editor.commit(); // commit changes
	}

	public void saveInviteSession(String invite_num) {
		editor.putString(KEY_INVATE_CNT, invite_num);
		editor.commit(); // commit changes
	}

	/**
	 * Check login method wil check user login status If false it will redirect
	 * user to login page Else won't do anything
	 * */
	public void checkLogin() {
		// Check login status
		if (!this.isLoggedIn()) {
			Intent i = new Intent(_context, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			_context.startActivity(i);
		}
	}

	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails() {
		HashMap<String, String> user = new HashMap<String, String>();
		user.put(KEY_NAME, pref.getString(KEY_NAME, null)); // user name
		user.put(KEY_GENDER, pref.getString(KEY_GENDER, null));
		user.put(KEY_INVATE_CNT, pref.getString(KEY_INVATE_CNT, null));

		return user; // return user
	}

	/**
	 * Clear session details
	 * */
	public void logoutUser() {
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();

		// After logout redirect user to Loing Activity
		Intent i = new Intent(_context, MainActivity.class);
		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// Staring Login Activity
		_context.startActivity(i);
	}

	/**
	 * Quick check for login
	 * **/
	// Get Login State
	public boolean isLoggedIn() {
		return pref.getBoolean(IS_LOGIN, false);
	}
}
