package com.example.cosmi.vigilante;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp_Fragment extends Fragment implements OnClickListener {
	private static View view;
	private static EditText fullName,lastName, emailId, mobileNumber, location,
			password, confirmPassword, code;
	private static TextView login;
	private static Button signUpButton;
	private static CheckBox terms_conditions;

	private String direction = "http://proyectomovilesvigilancia.hostingerapp.com/app/";

	public SignUp_Fragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.signup_layout, container, false);
		initViews();
		setListeners();
		return view;
	}

	// Initialize all views
	private void initViews() {
		fullName = (EditText) view.findViewById(R.id.fullName);
		lastName = (EditText) view.findViewById(R.id.lastName);
		emailId = (EditText) view.findViewById(R.id.userEmailId);
		mobileNumber = (EditText) view.findViewById(R.id.mobileNumber);
		location = (EditText) view.findViewById(R.id.location);
		password = (EditText) view.findViewById(R.id.password);
		confirmPassword = (EditText) view.findViewById(R.id.confirmPassword);
		code = (EditText) view.findViewById(R.id.codigo);
		signUpButton = (Button) view.findViewById(R.id.signUpBtn);
		login = (TextView) view.findViewById(R.id.already_user);
		terms_conditions = (CheckBox) view.findViewById(R.id.terms_conditions);

		// Setting text selector over textviews
		@SuppressLint("ResourceType") XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
		try {
			ColorStateList csl = ColorStateList.createFromXml(getResources(),
					xrp);

			login.setTextColor(csl);
			terms_conditions.setTextColor(csl);
		} catch (Exception e) {
		}
	}

	// Set Listeners
	private void setListeners() {
		signUpButton.setOnClickListener(this);
		login.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.signUpBtn:

			// Call checkValidation method
			checkValidation(v);

			break;

		case R.id.already_user:

			// Replace login fragment
			new ActivityloginRegister().replaceLoginFragment();
			break;
		}

	}

	class Save extends AsyncTask<String, Void, String> {
		String codigo;
		@Override
		protected String doInBackground(String... resultado) {// método que no tiene acceso a la parte visual
			HttpURLConnection connection;
			String nombre    = resultado[0];
			String apellidos = resultado[1];
			String correo    = resultado[2];
			String movil     = resultado[3];
			String localizacion     = resultado[4];
			String contrasenia      = resultado[5];
			codigo     = resultado[6];

			String res = null;

			String direccion = direction+"saveUser.php";
			try {

				Log.d("FCMToken", "token "+ FirebaseInstanceId.getInstance().getToken());
				String FCMToken = FirebaseInstanceId.getInstance().getToken();

				connection = (HttpURLConnection) new URL(direccion).openConnection();
				connection.setRequestMethod("POST");
				connection.setDoInput(true);

				OutputStream outputStream = connection.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

				writer.write("nombre="+nombre +"&"+"apellidos="+apellidos+"&"+"correo="+correo+"&"+"movil="+movil+"&"+"localizacion="+localizacion+"&"+"contrasenia="+contrasenia+"&"+"codigo="+codigo+"&"+"FCMToken="+FCMToken);
				writer.flush();
				writer.close();
				outputStream.close();

				connection.connect();
				InputStream is = (InputStream) connection.getContent();

				byte [] b = new byte[100000];//buffer
				Integer numBytes = is.read(b);// numero de bites que leyó
				//convertimos ese num de bites a una cadena
				res = new String(b, 0,  numBytes, "utf-8");
				Log.d("saveUser", res);

			} catch (IOException e) {
				e.printStackTrace();
			}
			return res;
		}
		protected void onPostExecute(String result) {
			if (result.equals("SUCCES")){
				Toast.makeText(getActivity(), "Do SignUp.", Toast.LENGTH_SHORT)
						.show();
				if(codigo.equals("INQ101118")){
					Intent intent = new Intent(getContext(), MainActivity.class);
					intent.putExtra("op","main");
					startActivity(intent);
				}
				else if(codigo.equals("VIG151118")){
					Intent intent = new Intent(getContext(), Visitante.class);
					startActivity(intent);
				}
			}
		}

	}

	// Check Validation Method
	private void checkValidation(View v) {

		// Get all edittext texts
		String getFullName = fullName.getText().toString();
		String getlastName = lastName.getText().toString();
		String getEmailId = emailId.getText().toString();
		String getMobileNumber = mobileNumber.getText().toString();
		String getLocation = location.getText().toString();
		String getPassword = password.getText().toString();
		String getConfirmPassword = confirmPassword.getText().toString();
		String getCode = code.getText().toString();

		// Pattern match for email id
		Pattern p = Pattern.compile(Utils.regEx);
		Matcher m = p.matcher(getEmailId);

		// Check if all strings are null or not
		if (getFullName.equals("") || getFullName.length() == 0
				|| getlastName.equals("") || getlastName.length() == 0
				|| getEmailId.equals("") || getEmailId.length() == 0
				|| getMobileNumber.equals("") || getMobileNumber.length() == 0
				|| getLocation.equals("") || getLocation.length() == 0
				|| getPassword.equals("") || getPassword.length() == 0
				|| getCode.equals("") || getCode.length() == 0
				|| getConfirmPassword.equals("")
				|| getConfirmPassword.length() == 0)

			new CustomToast().Show_Toast(getActivity(), view,
					"All fields are required.");

		// Check if email id valid or not
		else if (!m.find())
			new CustomToast().Show_Toast(getActivity(), view,
					"Your Email Id is Invalid.");

		// Check if both password should be equal
		else if (!getConfirmPassword.equals(getPassword))
			new CustomToast().Show_Toast(getActivity(), view,
					"Both password doesn't match.");

		// Make sure user should check Terms and Conditions checkbox
		else if (!terms_conditions.isChecked())
			new CustomToast().Show_Toast(getActivity(), view,
					"Please select Terms and Conditions.");

		// Else do signup or do your stuff
		else {
			v.setEnabled(false);
			new Save().execute(getFullName.toUpperCase(), getlastName.toUpperCase(),getEmailId.toUpperCase(), getMobileNumber, getLocation.toUpperCase(),getPassword, getCode );

			ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.loading_spinner);
			if (progressBar.getVisibility() == View.GONE) {
				progressBar.setVisibility(View.VISIBLE);
			}



		}

	}
}
