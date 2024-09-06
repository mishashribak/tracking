package com.blazma.logistics.custom;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.blazma.logistics.R;

public class CustomConfirmDialog extends Dialog {
    private ItemClickInterface mItemClickInterface;
    private Context mContext;

    public CustomConfirmDialog(Context paramContext, String description, ItemClickInterface itemClickInterface) {
        super(paramContext, R.style.dialog);

        setContentView(R.layout.dialog_no_sample_confirm);
        mContext = paramContext;
        mItemClickInterface = itemClickInterface;

        CustomMaterialButton btOK = findViewById(R.id.btnYes);
        CustomMaterialButton btCancel = findViewById(R.id.btnNo);
        CustomTextView txtDlgTitle = findViewById(R.id.txtDescription);
        txtDlgTitle.setText(description);

        btOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickInterface.onOK();
                disMissDialog();
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disMissDialog();
            }
        });
    }

    public CustomConfirmDialog(Context paramContext, String question, String okTitle, String cancelTitle, ItemClickInterface itemClickInterface) {
        super(paramContext, R.style.dialog);

        setContentView(R.layout.dialog_no_sample_confirm);
        mContext = paramContext;
        mItemClickInterface = itemClickInterface;

        CustomMaterialButton btOK = findViewById(R.id.btnYes);
        CustomMaterialButton btCancel = findViewById(R.id.btnNo);
        btOK.setText(okTitle);
        btCancel.setText(cancelTitle);

        CustomTextView txtDlgTitle = findViewById(R.id.txtDescription);
        txtDlgTitle.setText(question);
        btOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickInterface.onOK();
                disMissDialog();
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disMissDialog();
            }
        });
    }

    public void disMissDialog() {
        if (isShowing()) {
            dismiss();
        }
    }

    public void showDialog() {
        if (isShowing())
            dismiss();
        this.show();
    }

    public interface ItemClickInterface{
        void onOK();
    }
}
