package com.hitech.quizchallenge;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
 import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.hitech.quizchallenge.utils.ConnectionDetector;
import com.mongodb.lang.NonNull;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int i = 0;
    private StitchAppClient client;
    private RemoteMongoClient mongoClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);

        EditText editTeamName = findViewById(R.id.editTeamName);
        EditText editPlayerName = findViewById(R.id.editPlayerName);
        TextInputLayout playerNameLo = findViewById(R.id.playerNameLo);
        TextInputLayout teamNameLo = findViewById(R.id.teamNameLo);

        //Hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        client =
                Stitch.initializeDefaultAppClient("quizchallengestitch-ernws");
        mongoClient =
                client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(findViewById(R.id.linearLayout).getWindowToken(), 0);
                MainActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                if (editTeamName.getText().toString().trim().equalsIgnoreCase("")) {
                    teamNameLo.setError(getApplicationContext().getString(R.string.errorTeamNameStr));
                } else if (editPlayerName.getText().toString().trim().equalsIgnoreCase("")) {
                    playerNameLo.setError(getApplicationContext().getString(R.string.errorPlayerNameStr));
                } else {
                    ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
                    //Check internet availability status
                    if (connectionDetector.isConnectingToInternet()) {
                        sendCreatePlayerRequest(editTeamName, editPlayerName);
                    } else {
                        connectTimeoutLayoutDialog(getString(R.string.noInternetConnectionMgs));
                    }
                }


            }
        });
        editTeamName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editTeamName.getText().toString().trim().equalsIgnoreCase("")) {
                    teamNameLo.setError(getApplicationContext().getString(R.string.errorTeamNameStr));
                } else {
                    teamNameLo.setError(null);
                }
            }
        });
        editPlayerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editPlayerName.getText().toString().trim().equalsIgnoreCase("")) {
                    playerNameLo.setError(getApplicationContext().getString(R.string.errorPlayerNameStr));
                } else {
                    playerNameLo.setError(null);
                }
            }
        });
    }

    private void sendCreatePlayerRequest(EditText editTeamName, EditText editPlayerName) {

        final RemoteMongoCollection<Document> coll =
                mongoClient.getDatabase("QuizChallengeDBTest").getCollection("UserDetailsTest");

        client.getAuth().loginWithCredential(new AnonymousCredential()).continueWithTask(
                new Continuation<StitchUser, Task<RemoteUpdateResult>>() {

                    @Override
                    public Task<RemoteUpdateResult> then(@NonNull Task<StitchUser> task) throws Exception {
                        if (!task.isSuccessful()) {
                            Log.e("STITCH", "Login failed!");
                            throw task.getException();
                        }

                        final Document updateDoc = new Document(
                                "owner_id",
                                task.getResult().getId()
                        );

                        updateDoc.put("team_name", editTeamName.getText().toString().trim());
                        updateDoc.put("player_name", editPlayerName.getText().toString().trim());
                        return coll.updateOne(
                                null, updateDoc, new RemoteUpdateOptions().upsert(true)
                        );
                    }
                }
        ).continueWithTask(new Continuation<RemoteUpdateResult, Task<List<Document>>>() {
            @Override
            public Task<List<Document>> then(@NonNull Task<RemoteUpdateResult> task) throws Exception {
                if (!task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, getString(R.string.checkintorserver), Toast.LENGTH_SHORT).show();

                    Log.e("STITCH", "Update failed!");
                    throw task.getException();
                }
                List<Document> docs = new ArrayList<>();
                return coll
                        .find(new Document("owner_id", client.getAuth().getUser().getId()))
                        .limit(100)
                        .into(docs);
            }
        }).addOnCompleteListener(new OnCompleteListener<List<Document>>() {
            @Override
            public void onComplete(@NonNull Task<List<Document>> task) {
                if (task.isSuccessful()) {
                    Log.d("STITCH", "Found docs: " + task.getResult().toString());
                    Toast.makeText(MainActivity.this, getString(R.string.player_Created_str), Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.e("STITCH", "Error: " + task.getException().toString());
                task.getException().printStackTrace();
            }
        });
    }

    public void connectTimeoutLayoutDialog(String message) {
        final Dialog dialogForMessage = new Dialog(this);
        dialogForMessage.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogForMessage.setContentView(R.layout.alert_for_no_internet);
        dialogForMessage.setCancelable(false);
        TextView m = dialogForMessage.findViewById(R.id.message);
        final Button ok = dialogForMessage.findViewById(R.id.ok);
        m.setText(message);
        dialogForMessage.show();
        ok.setText(getString(R.string.ok));
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialogForMessage.dismiss();
            }
        });
    }
}
