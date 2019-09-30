package com.hitech.quizchallenge;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.hitech.quizchallenge.adapter.QuizQuestionAdapter;
import com.hitech.quizchallenge.pojo.QuizQuestionDetailsPojo;
import com.hitech.quizchallenge.utils.ConnectionDetector;
import com.hitech.quizchallenge.utils.RecyclerTouchListener;
import com.mongodb.lang.NonNull;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.hitech.quizchallenge.MainActivity.client;
import static com.hitech.quizchallenge.MainActivity.mongoClient;

public class QuizQuestionListActivity extends AppCompatActivity {
    private static final String TAG = "QuizPage";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String toDateStr;
    String fromDateStr;
    ImageButton changeTimeBtn;
    ImageButton backbtn;
    TextView his_type;
    RecyclerView recyclerView;
    TextView emptymessage;
    LinearLayout nodata;
    ArrayList<QuizQuestionDetailsPojo> vehicleHistoryArrayList = new ArrayList<>();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private EditText fromDate;
    private EditText toDate;
    private ProgressDialog pDialog;
    ArrayList<QuizQuestionDetailsPojo> arrayList;
    private String jsonObjectstr;
    private JSONObject jsonObject;

    //customize text style bold italic....
    public static SpannableString bold(String s) {
        SpannableString spanString = new SpannableString(s);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0,
                spanString.length(), 0);
        return spanString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_question_list);
        pDialog = new ProgressDialog(this);
        recyclerView = findViewById(R.id.recyclerView);

        initQuestionJson();
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int correctAns = 0;
                for (int i = 1; i < arrayList.size(); i++) {
                    if (arrayList.get(i).getSelected() != null) {
                        if (arrayList.get(i).getSelected().equalsIgnoreCase("1")) {
                            correctAns++;
                        }
                    }
                }
                sendCreatePlayerRequest(correctAns);
                scoreLayoutDialog(correctAns + "/10");

            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(QuizQuestionListActivity.this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Intent intent = new Intent(BatterySwapHistoryActivity.this, OffLoadBatteryHistoryDetails.class);
//                BatterySwapPojo batteryHistoryDetails = vehicleHistoryArrayList.get(position);
//                intent.putExtra("swapId", batteryHistoryDetails.getVech_load_Swap_Id());
//                startActivity(intent);
//                overridePendingTransition(R.animator.right_in, R.animator.left_out);
            }

            //Long click
            @Override
            public void onLongClick(View view, int position) {
            }
        }));
        ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
        //Check internet availability status
        if (connectionDetector.isConnectingToInternet()) {
//            sendCreatePlayerRequest();
            setupRecyclerView();
        } else {
            connectTimeoutLayoutDialog(getString(R.string.noInternetConnectionMgs));
        }
    }

    private void initQuestionJson() {
        jsonObjectstr = "{\n" +
                "  \"1\": {\n" +
                "    \"question\": \"1. Which attribute specifies a unique alphanumeric identifier to be associated with an element?\",\n" +
                "    \"choice\": [\n" +
                "      \"a) class\",\n" +
                "      \"b) id\",\n" +
                "      \"c) article\",\n" +
                "      \"d) html\"\n" +
                "    ],\n" +
                "    \"ans\": \"b\"\n" +
                "  },\n" +
                "  \"2\": {\n" +
                "    \"question\": \"2. The _____________ attribute specifies an inline style associated with an element, which determines the rendering of the affected element?\",\n" +
                "    \"choice\": [\n" +
                "      \"a) dir\",\n" +
                "      \"b) style\",\n" +
                "      \"c) class\",\n" +
                "      \"d) article\"\n" +
                "    ],\n" +
                "    \"ans\": \"b\"\n" +
                "  },\n" +
                "  \"3\": {\n" +
                "    \"question\": \"3. Which attribute is used to provide an advisory text about an element or its contents?\",\n" +
                "    \"choice\": [\n" +
                "      \"a) tooltip\",\n" +
                "      \"b) dir\",\n" +
                "      \"c) title\",\n" +
                "      \"d) head\"\n" +
                "    ],\n" +
                "    \"ans\": \"c\"\n" +
                "  },\n" +
                "  \"4\": {\n" +
                "    \"question\": \"4. The __________ attribute sets the text direction as related to the lang attribute?\",\n" +
                "    \"choice\": [\n" +
                "      \"a) lang\",\n" +
                "      \"b) sub\",\n" +
                "      \"c) dir\",\n" +
                "      \"d) ds\"\n" +
                "    ],\n" +
                "    \"ans\": \"c\"\n" +
                "  },\n" +
                "  \"5\": {\n" +
                "    \"question\": \"5. Which of the following is the attribute that specifies the column name from the data source object that supplies the bound data?\",\n" +
                "    \"choice\": [\n" +
                "      \"a) dataFormatAs\",\n" +
                "      \"b) datafld\",\n" +
                "      \"c) disabled\",\n" +
                "      \"d) datasrc\"\n" +
                "    ],\n" +
                "    \"ans\": \"b\"\n" +
                "  },\n" +
                "  \"6\": {\n" +
                "    \"question\": \"6. Which of the following is the attribute that indicates the name of the data source object that supplies the data that is bound to this element?\",\n" +
                "    \"choice\": [\n" +
                "      \"a) dataFormatAs\",\n" +
                "      \"b) datafld\",\n" +
                "      \"c) disabled\",\n" +
                "      \"d) datasrc\"\n" +
                "    ],\n" +
                "    \"ans\": \"d\"\n" +
                "  },\n" +
                "  \"7\": {\n" +
                "    \"question\": \"7. Which of the following is the attribute that specifies additional horizontal space, in pixels, to be reserved on either side of an embedded item like an iframe, applet, image, and so on?\",\n" +
                "    \"choice\": [\n" +
                "      \"a) height\",\n" +
                "      \"b) hspace\",\n" +
                "      \"c) hidefocus\",\n" +
                "      \"d) datasrc\"\n" +
                "    ],\n" +
                "    \"ans\": \"b\"\n" +
                "  },\n" +
                "  \"8\": {\n" +
                "    \"question\": \"The attribute of <form> tag?\",\n" +
                "    \"choice\": [\n" +
                "      \"a) Method\",\n" +
                "      \"b) Action\",\n" +
                "\t  \"c) Both (a)&(b)\",\n" +
                "      \"d) False\"\n" +
                "    ],\n" +
                "    \"ans\": \"c\"\n" +
                "  },\n" +
                "  \"9\": {\n" +
                "    \"question\": \"9. Which of the following is the attribute that is used to set a global identifier for a microdata item?\",\n" +
                "    \"choice\": [\n" +
                "      \"a) key\",\n" +
                "      \"b) id\",\n" +
                "      \"c) itemclass\",\n" +
                "      \"d) itemid\"\n" +
                "    ],\n" +
                "    \"ans\": \"d\"\n" +
                "  },\n" +
                "  \"10\": {\n" +
                "    \"question\": \"10. Which of the following is the attribute that is used to add a name/value pair to a microdata item?\",\n" +
                "    \"choice\": [\n" +
                "      \"a) itemscope\",\n" +
                "      \"b) itemref\",\n" +
                "      \"c) itemprop\",\n" +
                "      \"d) itemid\"\n" +
                "    ],\n" +
                "    \"ans\": \"c\"\n" +
                "  }\n" +
                "}";
        try {
            jsonObject = new JSONObject(jsonObjectstr);
            arrayList = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                JSONObject jsonObjectIndex = jsonObject.getJSONObject(String.valueOf(i));
                JSONArray jsonObjectArray = (JSONArray) jsonObject.getJSONObject(String.valueOf(i)).get("choice");
                arrayList.add(new QuizQuestionDetailsPojo(jsonObjectIndex.get("question").toString(),
                        String.valueOf(i),
                        jsonObjectIndex.getString("ans"),
                        jsonObjectArray.get(0).toString(),
                        jsonObjectArray.get(1).toString(),
                        jsonObjectArray.get(2).toString(),
                        jsonObjectArray.get(3).toString()
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void sendCreatePlayerRequest() {

        final RemoteMongoCollection<Document> coll = mongoClient.getDatabase("QuizChallengeDBTest").getCollection("QuizQA");
        System.out.println("coll =************* " + coll.findOne().toString());
//        RemoteMongoClient mongoClient =client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
//        RemoteMongoDatabase db = mongoClient.getDatabase("video");
//        RemoteMongoCollection<Document> movieDetails = db.getCollection("movieDetails");

// Find 20 documents
        coll.find()
                .limit(20)
                .forEach(document -> {
                    // Print documents to the log.
                    Log.i(TAG, "Got document:****** " + document.toString());
                });
    }

    private void backpress() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            QuizQuestionListActivity.this.overridePendingTransition(R.animator.left_to_right, R.animator.right_to_left);
        }
        finish();
    }


    private void setupRecyclerView() {
        System.out.println(" loadHistoryList " + arrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(QuizQuestionListActivity.this));
        QuizQuestionAdapter historyInTransitAdapter = new QuizQuestionAdapter(arrayList, QuizQuestionListActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(QuizQuestionListActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //Every row separated by line
        // recyclerView.addItemDecoration(new DividerItemDecoration(BatterySwapHistoryActivity.this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(historyInTransitAdapter);

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

    public void scoreLayoutDialog(String message) {
        final Dialog dialogForMessage = new Dialog(this);
        dialogForMessage.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogForMessage.setContentView(R.layout.alert_score);
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
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent inte = new Intent(QuizQuestionListActivity.this, HomeActivity.class);
//        startActivity(inte);
        backpress();
        return;
    }

    private void sendCreatePlayerRequest(int score) {

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

                        updateDoc.put("team_name", getIntent().getStringExtra("teamNameStr"));
                        updateDoc.put("player_name", getIntent().getStringExtra("playerNameStr"));
//                        updateDoc.put("team_name", editTeamName.getText().toString().trim());
                        updateDoc.put("score", score);
                        return coll.updateOne(
                                null, updateDoc, new RemoteUpdateOptions().upsert(true)
                        );
                    }
                }
        ).continueWithTask(new Continuation<RemoteUpdateResult, Task<List<Document>>>() {
            @Override
            public Task<List<Document>> then(@NonNull Task<RemoteUpdateResult> task) throws Exception {
                if (!task.isSuccessful()) {
                    Toast.makeText(QuizQuestionListActivity.this, getString(R.string.checkintorserver), Toast.LENGTH_SHORT).show();

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
                    return;
                }
                Log.e("STITCH", "Error: " + task.getException().toString());
                task.getException().printStackTrace();
            }
        });
    }

}
