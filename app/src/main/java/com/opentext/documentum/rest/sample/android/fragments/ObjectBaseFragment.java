/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.opentext.documentum.rest.sample.android.MainActivity;
import com.opentext.documentum.rest.sample.android.R;
import com.opentext.documentum.rest.sample.android.adapters.ObjectDetailAdapter;
import com.opentext.documentum.rest.sample.android.items.ObjectDetailItem;
import com.opentext.documentum.rest.sample.android.util.MimeIconHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class ObjectBaseFragment extends Fragment {
    private static final String TAG = "ObjectBaseFragment";
    public static String KEY_ID = "OBJECT_ID";
    protected String objectId;
    protected ObjectDetailAdapter adapter;
    @BindView(R.id.object_title)
    TextView titleView;
    @BindView(R.id.object_content_title)
    TextView contentTitleView;
    @BindView(R.id.object_detail_list)
    ListView objectDetailListView;
    @BindView(R.id.object_detail_scroll_view)
    NestedScrollView scrollView;

    @BindView(R.id.object_image)
    ImageView imageView;
    @BindView(R.id.object_text)
    EditText textView;
    @BindView(R.id.object_error_text)
    TextView errorTextView;
    @BindView(R.id.object_default_text)
    TextView defaultTextView;
    @BindView(R.id.object_text_button)
    Button txtButton;
    @BindView(R.id.object_file_button)
    Button fileButton;
    @BindView(R.id.object_content)
    View contentView;

    private Unbinder unbinder;
    private byte[] contentBytes;

    public static ObjectBaseFragment newIntance(String id) {
        ObjectBaseFragment instance = new ObjectBaseFragment();
        Bundle args = new Bundle();
        args.putString(KEY_ID, id);
        instance.setArguments(args);
        return instance;
    }

    public String getObjectId() {
        return objectId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_object_detail, container, false);
        unbinder = ButterKnife.bind(this, v);
        setHasOptionsMenu(true);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ObjectBaseFragment.this.setContentBytes(s.toString().getBytes());
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).removeTmpFragment(ObjectBaseFragment.this);
            }
        });
        adapter = new ObjectDetailAdapter(getContext(), R.layout.item_object_detail_list);
        objectDetailListView.setAdapter(adapter);
        scrollView.requestDisallowInterceptTouchEvent(true);
        scrollView.setNestedScrollingEnabled(true);
        getActivity().findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        titleView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.object_detail_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @OnClick(R.id.object_text_button)
    void onClickTxtButton() {
        if (textView.getVisibility() != View.VISIBLE)
            setEditTextContent(null);

    }

    @OnClick(R.id.object_file_button)
    void onClickFileButton() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(Intent.createChooser(intent, "SELECT A FILE"), 110);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedUri = data.getData();
            Cursor returnCursor =
                    this.getActivity().getContentResolver().query(selectedUri, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String fileNameString = returnCursor.getString(nameIndex);
            //todo: check format other than object name
            if (MimeIconHelper.isTxt(fileNameString)) {
                getContentBytes(selectedUri);
                setEditTextContent(this.contentBytes);
            } else if (MimeIconHelper.isImage(fileNameString)) {
                getContentBytes(selectedUri);
                setImageContent(this.contentBytes);
            } else {
                setUnsupportedContent(fileNameString);
            }
        }
    }

    private void getContentBytes(Uri selectedUri) {
        try {
            this.contentBytes = getBytesTool(getActivity().getContentResolver().openInputStream(selectedUri));
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            Toast.makeText(getContext(), "can't open the file", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateAdapterItems(ObjectDetailItem[] items, boolean onCreation) {
        this.adapter.updateItems(items, onCreation);
        this.adapter.notifyDataSetChanged();
        this.setListViewHeightBasedOnChildren();
    }

    public Map<String, String> getEditableProperties() {
        return adapter.filterEditable();
    }

    public Map<String, String> getProperties() {
        return adapter.getProperties();
    }

    public void setViewTextContent(byte[] content) {
        textView.setVisibility(View.GONE);
        errorTextView.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        this.setContentBytes(content);
        defaultTextView.setVisibility(View.VISIBLE);
        defaultTextView.setText(new String(content));
    }

    public void setEditTextContent(byte[] content) {
        defaultTextView.setVisibility(View.GONE);
        errorTextView.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        this.setContentBytes(content);
        textView.setVisibility(View.VISIBLE);
        textView.getText().clear();
        if (null != content)
            textView.getText().append(new String(content));
    }

    public void setImageContent(byte[] content) {
        defaultTextView.setVisibility(View.GONE);
        errorTextView.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        this.setContentBytes(content);
        imageView.setVisibility(View.VISIBLE);
        Bitmap bmp = BitmapFactory.decodeByteArray(content, 0, content.length);
        imageView.setImageBitmap(bmp);
    }

    public void setUnsupportedContent(String filename) {
        textView.setVisibility(View.GONE);
        defaultTextView.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        errorTextView.setVisibility(View.VISIBLE);
        if (filename != null) {
            errorTextView.setText("File chosen - " + filename);
            errorTextView.setTextColor(getContext().getColor(R.color.appAccent));
        }
    }

    public void setNullContent() {
        contentView.setVisibility(View.GONE);
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) scrollView.getLayoutParams();
        params.setBehavior(null);
    }

    public void setListViewHeightBasedOnChildren() {
        ListAdapter listAdapter = objectDetailListView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(objectDetailListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, objectDetailListView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = objectDetailListView.getLayoutParams();
        params.height = 100 + totalHeight + (objectDetailListView.getDividerHeight() * (listAdapter.getCount() - 1));
        objectDetailListView.setLayoutParams(params);
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        objectId = args.getString(KEY_ID, "");
    }

    public byte[] getContentBytes() {
        return contentBytes;
    }

    public void setContentBytes(byte[] contentBytes) {
        this.contentBytes = contentBytes;
    }

    public String getObjectName() {
        return adapter.getObjectName();
    }

    public byte[] getBytesTool(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
