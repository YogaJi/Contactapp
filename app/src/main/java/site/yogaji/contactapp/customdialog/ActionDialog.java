package site.yogaji.contactapp.customdialog;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import java.lang.ref.WeakReference;

import site.yogaji.contactapp.DatabaseHelper;
import site.yogaji.contactapp.IGenerateContactListener;
import site.yogaji.contactapp.OperationTypeEnum;
import site.yogaji.contactapp.R;
import site.yogaji.contactapp.model.Contact;


public class ActionDialog implements View.OnClickListener {
    private Context mContext;
    private AlertDialog mDialog;
    private IGenerateContactListener mListener;
    private Contact contact;

    public ActionDialog(Context context) {
        mContext = context;
    }

    public void createDialogAndShow(Contact contact, IGenerateContactListener listener) {
        this.mListener = listener;
        this.contact = contact;
        View actionDialog = LayoutInflater.from(mContext).inflate(R.layout.action_dialog, null);
        Button editBtn = actionDialog.findViewById(R.id.edit_btn);
        Button deleteBtn = actionDialog.findViewById(R.id.delete_btn);
        editBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(actionDialog);
        mDialog = builder.create();
        mDialog.show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_btn:
                mDialog.dismiss();
                new ContactDialog(mContext,
                        OperationTypeEnum.UPDATE,
                        contact,
                        mListener);
                break;
            case R.id.delete_btn:
                mDialog.dismiss();
                DeleteContactAsyncTask deleteContactAsyncTask = new DeleteContactAsyncTask(mContext);
                deleteContactAsyncTask.execute(contact.getId());
                mListener.getContact(null);
                break;
            default:
                break;
        }
    }

    private static class DeleteContactAsyncTask extends AsyncTask<Integer, Void, Void> {

        private WeakReference<Context> contextWeakReference;

        DeleteContactAsyncTask(Context context) {
            contextWeakReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            Context context = contextWeakReference.get();
            if (context != null) {
                new DatabaseHelper(context).deleteContact(integers[0]);
            }
            return null;
        }
    }
}

