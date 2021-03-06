package com.quickblox.sample.customobjects.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.sample.customobjects.R;
import com.quickblox.sample.customobjects.definition.Consts;
import com.quickblox.sample.customobjects.helper.DataHolder;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends Activity{

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initUI();

        // Initialize QuickBlox application with credentials.
        //
        QBSettings.getInstance().init(getApplicationContext(), String.valueOf(Consts.APP_ID), Consts.AUTH_KEY, Consts.AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(Consts.ACCOUNT_KEY);

        // Create QuickBlox session
        //
        QBUser qbUser = new QBUser(Consts.USER_LOGIN, Consts.USER_PASSWORD);
        QBAuth.createSession(qbUser, new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                DataHolder.getDataHolder().setSignInUserId(qbSession.getUserId());

                getNoteList();
            }

            @Override
            public void onError(QBResponseException error) {
                Toast.makeText(getBaseContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void initUI() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void getNoteList() {

        // Get all notes
        //
        QBCustomObjects.getObjects(Consts.CLASS_NAME, new QBEntityCallback<ArrayList<QBCustomObject>>() {
            @Override
            public void onSuccess(ArrayList<QBCustomObject> qbCustomObjects, Bundle bundle) {

                if (DataHolder.getDataHolder().size() > 0) {
                    DataHolder.getDataHolder().clear();
                }

                if (qbCustomObjects != null && qbCustomObjects.size() != 0) {
                    for (QBCustomObject customObject : qbCustomObjects) {
                        DataHolder.getDataHolder().addNoteToList(customObject);
                    }
                }

                startDisplayNoteListActivity();
            }

            @Override
            public void onError(QBResponseException strings) {
                Toast.makeText(getBaseContext(), strings.toString(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void startDisplayNoteListActivity() {
        Intent intent = new Intent(this, DisplayNoteListActivity.class);
        startActivity(intent);
        finish();
    }
}
