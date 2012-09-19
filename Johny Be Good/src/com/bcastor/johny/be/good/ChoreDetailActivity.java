package com.bcastor.johny.be.good;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.bcastor.johny.be.good.database.ChoreContentProvider;
import com.bcastor.johny.be.good.database.ChoreTable;

/*
 * TodoDetailActivity allows to enter a new todo item 
 * or to change an existing
 */
public class ChoreDetailActivity extends Activity {
  private Spinner mCategory;
  private EditText mTitleText;
  private EditText mBodyText;
  private TextView mElapsedTime;

  private Uri todoUri;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setContentView(R.layout.chore_edit);

    mCategory = (Spinner) findViewById(R.id.category);
    mTitleText = (EditText) findViewById(R.id.chore_edit_summary);
    mBodyText = (EditText) findViewById(R.id.chore_edit_description);
    mElapsedTime = (TextView)findViewById(R.id.chore_edit_elapsed);
    Button confirmButton = (Button) findViewById(R.id.chore_edit_button);

    Bundle extras = getIntent().getExtras();

    // Check from the saved Instance
    todoUri = (bundle == null) ? null : (Uri) bundle
        .getParcelable(ChoreContentProvider.CONTENT_ITEM_TYPE);

    // Or passed from the other activity
    if (extras != null) {
      todoUri = extras
          .getParcelable(ChoreContentProvider.CONTENT_ITEM_TYPE);

      fillData(todoUri);
    }

    confirmButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        if (TextUtils.isEmpty(mTitleText.getText().toString())) {
          makeToast();
        } else {
          setResult(RESULT_OK);
          finish();
        }
      }

    });
  }

  private void fillData(Uri uri) {
    String[] projection = { ChoreTable.COLUMN_SUMMARY,
        ChoreTable.COLUMN_DESCRIPTION, ChoreTable.COLUMN_CATEGORY};
    Cursor cursor = getContentResolver().query(uri, projection, null, null,
        null);
    if (cursor != null) {
      cursor.moveToFirst();
      String category = cursor.getString(cursor
          .getColumnIndexOrThrow(ChoreTable.COLUMN_CATEGORY));

      for (int i = 0; i < mCategory.getCount(); i++) {

        String s = (String) mCategory.getItemAtPosition(i);
        if (s.equalsIgnoreCase(category)) {
          mCategory.setSelection(i);
        }
      }

      mTitleText.setText(cursor.getString(cursor
          .getColumnIndexOrThrow(ChoreTable.COLUMN_SUMMARY)));
      mBodyText.setText(cursor.getString(cursor
          .getColumnIndexOrThrow(ChoreTable.COLUMN_DESCRIPTION)));

      // Always close the cursor
      cursor.close();
    }
  }

  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    saveState();
    outState.putParcelable(ChoreContentProvider.CONTENT_ITEM_TYPE, todoUri);
  }

  @Override
  protected void onPause() {
    super.onPause();
    saveState();
  }

  private void saveState() {
    String category = (String) mCategory.getSelectedItem();
    String summary = mTitleText.getText().toString();
    String description = mBodyText.getText().toString();

    // Only save if either summary or description
    // is available

    if (description.length() == 0 && summary.length() == 0) {
      return;
    }

    ContentValues values = new ContentValues();
    values.put(ChoreTable.COLUMN_CATEGORY, category);
    values.put(ChoreTable.COLUMN_SUMMARY, summary);
    values.put(ChoreTable.COLUMN_DESCRIPTION, description);
    if (todoUri == null) {
      // New todo
      todoUri = getContentResolver().insert(ChoreContentProvider.CONTENT_URI, values);
    } else {
      // Update todo
      getContentResolver().update(todoUri, values, null, null);
    }
  }

  private void makeToast() {
    Toast.makeText(ChoreDetailActivity.this, "Please maintain a summary",
        Toast.LENGTH_LONG).show();
  }
} 