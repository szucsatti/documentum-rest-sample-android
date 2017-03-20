/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.opentext.documentum.rest.sample.android.observables.LoginObservables;
import com.opentext.documentum.rest.sample.android.util.AccountHelper;

import org.springframework.util.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.loading_layout)
    View loadingLayout;
    @BindView(R.id.login_main_content)
    View loginMainContent;
    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.login_context_root)
    View contextRootView;
    @BindView(R.id.login_id)
    View idView;
    @BindView(R.id.login_password)
    View passwordView;
    @BindView(R.id.login_repo_logo)
    View repoLogoView;
    @BindView(R.id.login_repo)
    View repoView;
    @BindView(R.id.console_info)
    TextView consoleInfoTextView;
    @BindView((R.id.console_title))
    TextView consoleTitleView;
    @BindView(R.id.remember_checkbox)
    AppCompatCheckBox checkBox;
    @BindView(R.id.login_button)
    Button login_button;
    ArrayAdapter<String> spinnerAdapter;
    String selectedRepoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setupViews();
    }

    private void setupViews() {
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.app_title));
        toolbar.findViewById(R.id.toolbar_title).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        ((EditText) contextRootView).setHint("REST Service URL");
        ((EditText) contextRootView).setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        ((EditText) contextRootView).setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.vic_web), null, null, null);
        contextRootView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) return;
                if (!StringUtils.isEmpty(getContextRoot())) {
                    repoView.setVisibility(View.VISIBLE);
                    repoLogoView.setVisibility(View.VISIBLE);
                    consoleInfoTextView.setText("");
                    LoginObservables.getProductInfoAndRepos(LoginActivity.this);
                }
            }
        });

        ((EditText) idView).setHint("Username");
        ((EditText) idView).setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.vic_account), null, null, null);

        ((EditText) passwordView).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ((EditText) passwordView).setHint("Password");
        ((EditText) passwordView).setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.vic_key), null, null, null);

        repoLogoView.setVisibility(View.INVISIBLE);
        repoView.setVisibility(View.INVISIBLE);
        ((Spinner) repoView).setAdapter(spinnerAdapter);
        ((Spinner) repoView).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LoginActivity.this.selectedRepoName = parent.getAdapter().getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (AccountHelper.getRemember(this)) {
            setContextRoot(AccountHelper.getContextRoot(this));
            setId(AccountHelper.getId(this));
            setPassword(AccountHelper.getPassword(this));
            setRepo(AccountHelper.getREPO(this));
            setRemeber(true);
            LoginObservables.getProductInfoAndRepos(this);
        }
        login_button.setEnabled(false);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginObservables.login(LoginActivity.this);
            }
        });

        checkBox.setEnabled(false);
        consoleTitleView.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.vic_msg), null, null, null);
    }

    public void setContextRootEnable(boolean enable) {
        contextRootView.setEnabled(enable);
        repoView.setEnabled(enable);
    }

    public void setRepoViewEnable(boolean enable) {
        repoView.setEnabled(enable);
    }

    public void resetRepos(String[] repos) {
        spinnerAdapter.clear();
        spinnerAdapter.addAll(repos);
        spinnerAdapter.notifyDataSetChanged();
    }

    public void setConsoleInfo(String info, boolean failed) {
        if (failed) {
            consoleInfoTextView.setTextColor(getColor(R.color.error_msg));
            consoleInfoTextView.setText(String.format("%s%s", "Invalid service call: \r\n", info));
        } else {
            consoleInfoTextView.setTextColor(getColor(R.color.info_msg));
            consoleInfoTextView.setText(info);
        }
    }

    public void setLoadingLayout() {
        loadingLayout.setVisibility(VISIBLE);
        loginMainContent.setEnabled(false);
        loginMainContent.setAlpha(0.2f);
    }

    public void setLoginMainContent() {
        loadingLayout.setVisibility(View.GONE);
        loginMainContent.setEnabled(true);
        loginMainContent.setAlpha(1f);
    }

    public String getContextRoot() {
        return ((EditText) contextRootView).getText().toString().trim();
    }

    public void setContextRoot(String s) {
        ((EditText) contextRootView).getText().clear();
        ((EditText) contextRootView).getText().append(s);
    }

    public String getId() {
        return ((EditText) idView).getText().toString();
    }

    public void setId(String s) {
        ((EditText) idView).getText().clear();
        ((EditText) idView).getText().append(s);
    }

    public String getPassword() {
        return ((EditText) passwordView).getText().toString();
    }

    public void setPassword(String s) {
        ((EditText) passwordView).getText().clear();
        ((EditText) passwordView).getText().append(s);
    }

    public String getRepo() {
        return selectedRepoName;
    }

    public void setRepo(String s) {
        spinnerAdapter.clear();
        spinnerAdapter.add(s);
        spinnerAdapter.notifyDataSetChanged();
    }

    public boolean getRemeber() {
        return checkBox.isChecked();
    }

    public void setRemeber(boolean rem) {
        checkBox.setChecked(rem);
    }

    public void setEnableLogin() {
        login_button.setEnabled(true);
        login_button.setBackgroundColor(Color.parseColor("#0e8488"));
        checkBox.setEnabled(true);
    }
}
