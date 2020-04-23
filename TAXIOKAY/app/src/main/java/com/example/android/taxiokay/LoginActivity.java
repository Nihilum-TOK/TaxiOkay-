package com.example.android.taxiokay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.taxiokay.data.model.LoginBody;
import com.example.android.taxiokay.data.model.User;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int REQUEST_READ_CONTACTS = 0;

    private Retrofit mRestAdapter;
    private TOKApi mTOKapi;
    private Sesion sesion;

    String result = null;

    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mUser;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_login);
        sesion = new Sesion(this); //Una sesion, donde tengo guardadas las sharedPreferences, como el usuario y contraseña.
        mUser = (AutoCompleteTextView) findViewById(R.id.username);//Obtengo los datos de las vistas
        populateAutoComplete();

        mRestAdapter = new Retrofit.Builder()
                .baseUrl(TOKApi.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mTOKapi = mRestAdapter.create(TOKApi.class);

        mPasswordView = (EditText) findViewById(R.id.password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();//Se hace el login
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.login);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();//Login
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mUser, "Se necesitan permisos para el autocompletado", Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        mUser.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String user = mUser.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError("Contraseña muy corta");
            focusView = mPasswordView;
            cancel = true;
        }


        //Check user
        if (TextUtils.isEmpty(user)) {
            mUser.setError("Este campo es obligatorio");
            focusView = mUser;
            cancel = true;
        } else if (!isUsernameValid(user)) {
            mUser.setError("Usuario inválido");
            focusView = mUser;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            loginExitoso();
            /*
            Call<User> loginCall = mTOKapi.login(new LoginBody(user, password));
            loginCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    showProgress(false);
                    if (!response.isSuccessful())
                    {
                        //String error;
                        //if (response.errorBody()
                         //   .contentType()
                         //   .subtype()
                         //   .equals("application/json"))
                        //{
                        //}
                        return;
                    }
                    else
                    {
                        saveUser(response.body());
                        loginExitoso();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    showProgress(false);
                }
            });
            //mAuthTask = new UserLoginTask(user, password);
            //if (sesion.obtenerUsuario().equals("")) {
            //mAuthTask.execute((Void) null);
            //} else {
            //    loginExitoso();
            //}*/

        }
    }

    public void saveUser(User user)
    {
        if(user != null)
        {
            sesion.setmIsLoggedIn(true);
            sesion.guardarUsuario(user.getUsername());
            sesion.guardarId(user.getId());
            sesion.guardarNombre(user.getNombre());
            sesion.guardarTelefono(user.getTelefono());
        }
    }

    private boolean isUsernameValid(String user) {
        return user.length() > 4;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    //Para mostrar animación de spinner
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mUser.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    //Aquí se hace la autentificación
    public interface TOKApi{
        public static final String URL= "http://187.153.22.193/";

        @POST("login")
        Call<User> login(@Body LoginBody body);
    }
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;

        InputStream is;

        UserLoginTask(String user, String password) {
            mUsername = user;
            mPassword = password;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Boolean doInBackground(Void... params) {

            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                //Hago la conexión
                URL url = new URL("http://187.153.22.193/login");
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();//Se realiza la conexión

                httpConn.setRequestMethod("POST");
                httpConn.setRequestProperty("Content-Type", "application/json");
                httpConn.setRequestProperty("Accept", "application/json");
                httpConn.setDoOutput(true);

                String input = "{\"id\":\"" + mUsername + "\"," + "\"password\":\"" + mPassword + "\"}";

                httpConn.connect();
                try (OutputStream os = httpConn.getOutputStream()) {
                    byte[] query = input.getBytes("utf-8");
                    os.write(query, 0, query.length);
                }

                is = httpConn.getInputStream(); //Se obtiene el resultado
                //result = convertStreamToString(is);//Se convierte a String*/
                result = result.substring(1, result.length() - 1);

            } catch (Exception e) {
                result = "No";
                e.printStackTrace();
            }

            String comprobar = "No";
            if (comprobar.equals(result)) {
                return false;
            } else {
                return true;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                loginExitoso();

            } else {
                mPasswordView.setError("Usuario o contraseña incorrectos");
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    public void registro_intent(View v) {
        Intent nuevo_registro = new Intent(this, Registro.class);
        startActivity(nuevo_registro);
    }

    public void loginExitoso() {
        //Intent loginBien = new Intent(this, LoginExitoso.class);
        Intent loginBien = new Intent(this, MainActivity.class);
        startActivity(loginBien);
        return;
    }

    public void onBackPressed() {
        super.onBackPressed();

        finish();
        //System.exit(0);
    }

}
