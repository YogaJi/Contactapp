package site.yogaji.contactapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import android.widget.TextView;
import android.widget.Toast;
import site.yogaji.contactapp.model.Contact;
import site.yogaji.contactapp.customdialog.ContactDialog;
import site.yogaji.contactapp.customdialog.ActionDialog;
import site.yogaji.contactapp.IGenerateContactListener;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button addPersonBtn;
    //    private PopupWindow mPopupWindow;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Contact> contactArrayList = new ArrayList<>();
    private DatabaseHelper databaseHelper;
    private EditText inputSearchNameEt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //set up recycler view
        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //set up database
        databaseHelper = new DatabaseHelper(this);
        mAdapter = new MyRecyclerViewAdapter(contactArrayList);
        recyclerView.setAdapter(mAdapter);
        //set up asyncTask
        GetAllContactAsyncTask getAllContactAsyncTask = new GetAllContactAsyncTask(this);
        getAllContactAsyncTask.execute();

        //set recycler view item touch listener
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        ActionDialog actionDialog = new ActionDialog(MainActivity.this);
                        Contact contact = contactArrayList.get(position);
                        actionDialog.createDialogAndShow(contact, new IGenerateContactListener(){
                            @Override
                            public void getContact(Contact contact) {
                                if (contact != null) {
                                    contactArrayList.set(position, contact);
                                } else {
                                    contactArrayList.remove(position);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }));
        //set up popupWindow
//        View popupView = getLayoutInflater().inflate(R.layout.add_contact, null);
//        mPopupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
//        mPopupWindow.setTouchable(true);
//        mPopupWindow.setOutsideTouchable(true);
//        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
//        //set add person button listener
//        addPersonBtn = (Button) findViewById(R.id.add_person_btn);
//        addPersonBtn.setOnClickListener(this);
//
//        addPersonBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Perform action on click
//                mPopupWindow.showAsDropDown(v);
//            }
//        });

        //set search button listener
        Button startSearchBtn = findViewById(R.id.start_search_btn);
        startSearchBtn.setOnClickListener(this);

        Button addBtn = findViewById(R.id.add_person_btn);
        addBtn.setOnClickListener(this);

        inputSearchNameEt = findViewById(R.id.search_input_name_et);
        inputSearchNameEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                //handle user input enter action on inputSearchNameEt
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    startQueryDatabase();
                }
                return false;
            }
        });
        //popup display
//        mPopupWindow.getContentView().setFocusableInTouchMode(true);
//        mPopupWindow.getContentView().setFocusable(true);
//        mPopupWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0
//                        && event.getAction() == KeyEvent.ACTION_DOWN) {
//                    if (mPopupWindow != null && mPopupWindow.isShowing()) {
//                        mPopupWindow.dismiss();
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });

    }//end of onCreate

//    //set popup window display
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
//            if (mPopupWindow != null && !mPopupWindow.isShowing()) {
//                mPopupWindow.showAtLocation(findViewById(R.id.layout_main), Gravity.CENTER, 0, 0);
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    //set on click
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_person_btn:

                //commit the new contact
                new ContactDialog(MainActivity.this, OperationTypeEnum.INSERT,
                        new IGenerateContactListener() {
                            @Override
                            public void getContact(Contact contact) {
                                contactArrayList.add(contact);
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                break;
            case R.id.start_search_btn:
                //query database by the name input by user
                startQueryDatabase();
                break;
            default:
                break;
        }
    }
    //set query database func
    private void startQueryDatabase() {
        QueryByContactNameAsyncTask queryByContactNameAsyncTask = new QueryByContactNameAsyncTask(this);
        queryByContactNameAsyncTask.execute(inputSearchNameEt.getText().toString());
    }
    //hide keyboard func
    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    //life cycle to set data onResume
    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    //life cycle to close database on destroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }

    //set async task
    private static class GetAllContactAsyncTask extends AsyncTask<Void, Void, ArrayList<Contact>> {

        private WeakReference<MainActivity> activityWeakReference;

        GetAllContactAsyncTask(MainActivity context) {
            activityWeakReference = new WeakReference<>(context);
        }

        @Override
        protected ArrayList<Contact> doInBackground(Void... voids) {
            ArrayList<Contact> contacts = new ArrayList<>();

            MainActivity mainActivity = activityWeakReference.get();
            if (mainActivity != null) {
                contacts = new DatabaseHelper(mainActivity).getAllContact();
            }
            return contacts;
        }

        @Override
        protected void onPostExecute(ArrayList<Contact> contacts) {
            super.onPostExecute(contacts);
            MainActivity mainActivity = activityWeakReference.get();
            if (mainActivity != null && !mainActivity.isFinishing()) {
                mainActivity.contactArrayList = contacts;
//                mainActivity.mAdapter.notifyDataSetChanged();

                RecyclerView recyclerView = mainActivity.findViewById(R.id.my_recycler_view);
                mainActivity.mAdapter = new MyRecyclerViewAdapter(contacts);
                recyclerView.setAdapter(mainActivity.mAdapter);
            }
        }
    }//end of async task
    //set query
    private static class QueryByContactNameAsyncTask extends AsyncTask<String, Void, Contact> {

        private WeakReference<MainActivity> activityWeakReference;

        QueryByContactNameAsyncTask(MainActivity context) {
            activityWeakReference = new WeakReference<>(context);
        }

        @Override
        protected Contact doInBackground(String... strings) {
            String inputSearchNameString = strings[0];
            MainActivity mainActivity = activityWeakReference.get();
            if (mainActivity != null && !mainActivity.isFinishing()) {
                return new DatabaseHelper(mainActivity).getContactQueryByName(inputSearchNameString);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Contact contact) {
            super.onPostExecute(contact);
            MainActivity mainActivity = activityWeakReference.get();
            if (mainActivity != null && !mainActivity.isFinishing()) {
                if (contact != null) {
                    new ContactDialog(mainActivity, OperationTypeEnum.QUERY, contact);
                    mainActivity.inputSearchNameEt.setText("");
                } else {
                    Toast.makeText(mainActivity,
                            "The people not survive in your list",
                            Toast.LENGTH_SHORT)
                            .show();
                }
                mainActivity.hideSoftKeyboard();
            }
        }
    }//end of query

    //make menu:
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }
    //select menu
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item){
//        switch (item.getItemId()){
////            case R.id.menu_settings:
////                startActivity(new Intent(
////                        getApplicationContext(),
////                        SettingsActivity.class
////                ));
//////                        Toast.makeText(this, "Settings Item", Toast.LENGTH_SHORT).show();
////                break;
//            case R.id.menu_add:
//                break;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
        return true;
    }

}//end of the main class
