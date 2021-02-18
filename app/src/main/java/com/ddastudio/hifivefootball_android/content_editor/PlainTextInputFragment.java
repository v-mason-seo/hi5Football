package com.ddastudio.hifivefootball_android.content_editor;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.event.EditorEvent;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlainTextInputFragment extends BottomSheetDialogFragment
        implements BaseContract.View {

    @BindView(R.id.plain_text_input_close) ImageView ivClose;
    @BindView(R.id.plain_text_input_complete) Button btnComplete;
    @BindView(R.id.text_input) EditText etTextInput;

    int mPosition;
    Unbinder unbinder;

    public static PlainTextInputFragment newInstance(int position, String text) {

        final PlainTextInputFragment fragment = new PlainTextInputFragment();
        final Bundle args = new Bundle();
        //args.putParcelable("ARGS_COMMENT_MODEL", Parcels.wrap(commentData));
        args.putInt("ARGS_POSITION", position);
        args.putString("ARGS_TEXT", text);
        fragment.setArguments(args);
        return fragment;
    }

    public PlainTextInputFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_plain_text_input, container, false);
        unbinder = ButterKnife.bind(this, view);

        mPosition = getArguments().getInt("ARGS_POSITION", -1);
        String editText = getArguments().getString("ARGS_TEXT", "");
        etTextInput.setText(editText);
        etTextInput.setSelection(editText.length());
        etTextInput.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;

                FrameLayout bottomSheet = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if ( unbinder != null )
            unbinder.unbind();
    }

    @OnClick(R.id.plain_text_input_complete)
    public void onCompleteClick() {
        CommonUtils.hideKeyboard(getActivity(), getView());

        App.getInstance().bus().send(new EditorEvent.EditTextEvent(true, mPosition, etTextInput.getText().toString()));
        dismiss();
    }

    @OnClick(R.id.plain_text_input_close)
    public void onCloseClick() {

        CommonUtils.hideKeyboard(getActivity(), getView());
        dismiss();
    }

}
