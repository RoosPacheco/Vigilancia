package com.example.cosmi.vigilante;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Login_Fragment extends Fragment implements OnClickListener {
	private static View view;

	private static EditText emailid, password;
	private static Button loginButton;
	private static TextView forgotPassword, signUp;
	private static CheckBox show_hide_password;
	private static LinearLayout loginLayout;
	private static Animation shakeAnimation;
	private static FragmentManager fragmentManager;

	public Login_Fragment() {

	}

	protected String direction = "http://proyectomovilesvigilancia.hostingerapp.com/app/";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.login_layout, container, false);
		initViews();
		setListeners();
		return view;
	}

	// Initiate Views
	private void initViews() {
		fragmentManager = getActivity().getSupportFragmentManager();

		emailid = (EditText) view.findViewById(R.id.login_emailid);
		password = (EditText) view.findViewById(R.id.login_password);
		loginButton = (Button) view.findViewById(R.id.loginBtn);
		forgotPassword = (TextView) view.findViewById(R.id.forgot_password);
		signUp = (TextView) view.findViewById(R.id.createAccount);
		show_hide_password = (CheckBox) view
				.findViewById(R.id.show_hide_password);
		loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);

		// Load ShakeAnimation
		shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.shake);

		// Setting text selector over textviews
		@SuppressLint("ResourceType") XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
		try {
			ColorStateList csl = ColorStateList.createFromXml(getResources(),
					xrp);

			forgotPassword.setTextColor(csl);
			show_hide_password.setTextColor(csl);
			signUp.setTextColor(csl);
		} catch (Exception e) {
		}
	}

	// Set Listeners
	private void setListeners() {
		loginButton.setOnClickListener(this);
		forgotPassword.setOnClickListener(this);
		signUp.setOnClickListener(this);

		// Set check listener over checkbox for showing and hiding password
		show_hide_password
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton button,
							boolean isChecked) {

						// If it is checkec then show password else hide
						// password
						if (isChecked) {

							show_hide_password.setText(R.string.hide_pwd);// change
																			// checkbox
																			// text

							password.setInputType(InputType.TYPE_CLASS_TEXT);
							password.setTransformationMethod(HideReturnsTransformationMethod
									.getInstance());// show password
						} else {
							show_hide_password.setText(R.string.show_pwd);// change
																			// checkbox
																			// text

							password.setInputType(InputType.TYPE_CLASS_TEXT
									| InputType.TYPE_TEXT_VARIATION_PASSWORD);
							password.setTransformationMethod(PasswordTransformationMethod
									.getInstance());// hide password

						}

					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loginBtn:

			/*Conectar conectar = new Conectar();
			conectar.execute();*/

			checkValidation();
			break;

		case R.id.forgot_password:

			// Replace forgot password fragment with animation
			fragmentManager
					.beginTransaction()
					.setCustomAnimations(R.anim.right_enter, R.anim.left_out)
					.replace(R.id.frameContainer,
							new ForgotPassword_Fragment(),
							Utils.ForgotPassword_Fragment).commit();
			break;
		case R.id.createAccount:

			// Replace signup frgament with animation
			fragmentManager
					.beginTransaction()
					.setCustomAnimations(R.anim.right_enter, R.anim.left_out)
					.replace(R.id.frameContainer, new SignUp_Fragment(),
							Utils.SignUp_Fragment).commit();
			break;
		}

	}

	// Check Validation before login
	private void checkValidation() {
		// Get email id and password
		String getEmailId = emailid.getText().toString();
		String getPassword = password.getText().toString();

		// Check patter for email id
		Pattern p = Pattern.compile(Utils.regEx);

		Matcher m = p.matcher(getEmailId);

		// Check for both field is empty or not
		if (getEmailId.equals("") || getEmailId.length() == 0
				|| getPassword.equals("") || getPassword.length() == 0) {
			loginLayout.startAnimation(shakeAnimation);
			new CustomToast().Show_Toast(getActivity(), view,
					"Enter both credentials.");

		}
		// Check if email id is valid or not
		else if (!m.find())
			new CustomToast().Show_Toast(getActivity(), view,
					"Your Email Id is Invalid.");
		// Else do login and do your stuff
		else {
			new inicio_sesion().execute(getEmailId,getPassword);
		}

	}

	class inicio_sesion extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... parameter) {// método que no tiene acceso a la parte visual
			HttpURLConnection connection;

			String EmailId    = parameter[0];
			String Password   = parameter[1];

			String direccion = direction+"sesion/inicio_sesion.php";

			String res = null;

			try {


				connection = (HttpURLConnection) new URL(direccion).openConnection();
				connection.setRequestMethod("POST");
				connection.setDoInput(true);

				/*********/

				String params = "correo=" + EmailId + "&" + "contrasena=" + Password ;

				OutputStream outputStream = connection.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

				writer.write(params);
				//writer.write("image=" + URLEncoder.encode(imagen, "UTF-8"));
				writer.flush();
				writer.close();
				outputStream.close();

				connection.connect();
				InputStream is = (InputStream) connection.getContent();
				byte [] b = new byte[100000];//buffer
				Integer numBytes = is.read(b);// numero de bites que lleyó
				//convertimos ese num de bites a una cadena
				res = new String(b, 0,  numBytes, "utf-8");

				Log.d("res", res);


			} catch (IOException e) {
				e.printStackTrace();
			}
			return res;
		}

		@Override
		protected void onPostExecute(String res) {
			super.onPostExecute(res);
			if (res.equals("HABITANTE FAIL") || res.equals("VIGILANTE FAIL"))
			{
				//Toast.makeText(getActivity(), "Invalido.", Toast.LENGTH_SHORT).show();
				loginLayout.startAnimation(shakeAnimation);
				new CustomToast().Show_Toast(getActivity(), view,
						"Usuario o contraseña inválido");
			}
			else if (res.equals("HABITANTE OK"))
			{
				Intent intent = new Intent(getContext(), MainActivity.class);
				intent.putExtra("op","main");
				startActivity(intent);

			}
			else if (res.equals("VIGILANTE OK"))
			{
				Intent intent = new Intent(getContext(), Visitante.class);
				startActivity(intent);

			}
			else if (res.equals("DESCONOCIDO"))
			{
				loginLayout.startAnimation(shakeAnimation);
				new CustomToast().Show_Toast(getActivity(), view,
						"Usuario no registrado");

			}

		}
	}
















	private class Conectar extends AsyncTask<Void, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				//String url = "jdbc:jtds:sqlserver://ip_servidor;USER=xxx;PASSWORD=xxx";
			//	Log.i("url", url);
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
                Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection conn = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.1.143:3306","Jesus123","123456789");
				if (conn == null)
				{
					return false;
				}
			} catch (NoClassDefFoundError e){
				Log.e("Definicion de clase",e.getMessage());
			} catch (ClassNotFoundException e) {
				Log.e("Clase no encontrada",e.getMessage());
			} catch (Exception e) {
				Log.e("ERROR Conexion:",e.getMessage());
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean resultado) {
			if(resultado) {
				Toast.makeText(getContext(), "Conectado.", Toast.LENGTH_SHORT).show();
				Log.d("LOG:", "conectado");
			}else {
				Toast.makeText(getContext(), "No conectado", Toast.LENGTH_SHORT).show();
				Log.d("LOG:", "no conectado");
			}
		}
	}
}
