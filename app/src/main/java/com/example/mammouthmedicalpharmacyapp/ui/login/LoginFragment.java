package com.example.mammouthmedicalpharmacyapp.ui.login;

import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.*;

import com.example.mammouthmedicalpharmacyapp.databinding.FragmentLoginBinding;

import com.example.mammouthmedicalpharmacyapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginFragment extends Fragment {
    private static final String LOG_TAG = LoginFragment.class.getName();
    private static final String PREF_KEY = Objects.requireNonNull(LoginFragment.class.getPackage()).toString();

    private LoginViewModel loginViewModel;
    private FragmentLoginBinding binding;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private SharedPreferences preferences;
    private FirebaseAuth firebaseAuthInstance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usernameEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        Button loginButton1 = view.findViewById(R.id.loginButton);

        preferences = requireActivity().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);

        String username = preferences.getString("username", "");
        String password = preferences.getString("password", "");

        usernameEditText.setText(username);
        passwordEditText.setText(password);

        firebaseAuthInstance = FirebaseAuth.getInstance();

        loginButton1.setOnClickListener(v -> {
            Log.d(LOG_TAG, "LOGIN BUTTON CLICKED");
            loginLogic();
        });

        Button cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> {
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            TranslateAnimation animate = new TranslateAnimation(
                    0,
                    0,
                    0,
                    screenHeight);
            animate.setDuration(500);
            animate.setFillAfter(true);

            view.startAnimation(animate);

            animate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    getParentFragmentManager().popBackStack();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        });

        view.setOnTouchListener((v, event) -> true);

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText emailEditText = binding.emailEditText;
        final EditText passwordEditText = binding.passwordEditText;
        final Button loginButton = binding.loginButton;

        if (!emailEditText.toString().isEmpty() && !passwordEditText.toString().isEmpty()) {
            loginButton.setEnabled(true);
        }

        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                emailEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
    }

    private void loginLogic() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        firebaseAuthInstance.signInWithEmailAndPassword(username, password).addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                Log.d(LOG_TAG, "User logged in successfully");
                gotoShopPage();
            } else {
                Log.d(LOG_TAG, "User login error");
                Toast.makeText(requireActivity(), "User login error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void gotoShopPage() {
        // TODO: create ShopPage component
        // Intent intent = new Intent(this, ShopListActivity.class);
        // startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", usernameEditText.getText().toString());
        editor.putString("password", passwordEditText.getText().toString());
        editor.apply();


        Log.i(LOG_TAG, "onPause");
    }

}