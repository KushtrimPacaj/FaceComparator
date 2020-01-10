package kushtrimpacaj.facecomp.views.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kushtrimpacaj.facecomp.FaceCompApp;
import kushtrimpacaj.facecomp.R;
import kushtrimpacaj.facecomp.presenters.FaceComparisonPresenter;
import kushtrimpacaj.facecomp.views.view_interfaces.FaceComparisonView;

public class FaceComparisonActivity extends MvpActivity<FaceComparisonView, FaceComparisonPresenter> implements FaceComparisonView {

    @BindView(R.id.firstImageView)
    ImageView firstImageView;

    @BindView(R.id.firstImageLabel)
    TextView firstImageLabel;

    @BindView(R.id.secondImageView)
    ImageView secondImageView;

    @BindView(R.id.secondImageLabel)
    TextView secondImageLabel;

    @BindView(R.id.result)
    TextView result;

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    @ColorInt
    int imageBackgroundColor;
    @ColorInt
    int transparentColor;

    @ColorInt
            int matchColor;

    @ColorInt
            int mismatchColor;

    NumberFormat percentageFormater = new DecimalFormat("00.00");


    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        progressDialog = getProgressDialogInstance();
        imageBackgroundColor = ContextCompat.getColor(this, R.color.image_placeholder);
        transparentColor = ContextCompat.getColor(this, android.R.color.transparent);
        matchColor = ContextCompat.getColor(this, R.color.green);
        mismatchColor = ContextCompat.getColor(this, R.color.red);

    }

    private ProgressDialog getProgressDialogInstance() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait.");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        return progressDialog;
    }

    @NonNull
    @Override
    public FaceComparisonPresenter createPresenter() {
        return ((FaceCompApp) getApplication()).getApplicationComponent().faceComparisonPresenter();
    }


    @OnClick(R.id.imageOneContainer)
    public void chooseFirstPhoto() {
        presenter.onChooseFirstPhoto();
    }

    @OnClick(R.id.imageTwoContainer)
    public void chooseSecondPhoto() {
        presenter.onChooseSecondPhoto();
    }

    @OnClick(R.id.comparePhotos)
    public void comparePhotos() {
        presenter.onComparePhotos();
    }

    @Override
    public void showFirstImage(Bitmap photo) {
        showImage(firstImageView, firstImageLabel, photo);
    }

    @Override
    public void showSecondImage(Bitmap photo) {
        showImage(secondImageView, secondImageLabel, photo);
    }

    private void showImage(ImageView imageView, TextView label, Bitmap photo) {
        label.setVisibility(View.INVISIBLE);
        imageView.setImageBitmap(photo);
        imageView.setBackgroundColor(transparentColor);
        result.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showResult(boolean isIdentical, double percentage) {
        if (isIdentical) {
            result.setBackgroundColor(matchColor);
            result.setText(String.format(getString(R.string.samePersonResult), percentageFormater.format(percentage * 100)));
        } else {
            result.setBackgroundColor(mismatchColor);
            result.setText(String.format(getString(R.string.differentPersonResult), percentageFormater.format((1 - percentage) * 100)));
        }
        result.setVisibility(View.VISIBLE);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public void showProgressDialog(String title) {
        progressDialog.setTitle(title);
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        progressDialog.hide();
    }


    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void removeFirstImage() {
        removeImage(firstImageView, firstImageLabel);
    }

    @Override
    public void removeSecondImage() {
        removeImage(secondImageView, secondImageLabel);
    }

    private void removeImage(ImageView imageView, TextView label) {
        label.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(null);
        imageView.setBackgroundColor(imageBackgroundColor);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            presenter.onChoosePhotoIntentResult(requestCode);
        }
    }
}
