package kushtrimpacaj.facecomp.views.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kushtrimpacaj.facecomp.R;
import kushtrimpacaj.facecomp.presenters.MainPresenter;
import kushtrimpacaj.facecomp.view_interfaces.MainView;

public class MainActivity extends MvpActivity<MainView, MainPresenter> implements MainView {

    @Bind(R.id.firstImageView)
    ImageView firstImageView;

    @Bind(R.id.firstImageLabel)
    TextView firstImageLabel;

    @Bind(R.id.secondImageView)
    ImageView secondImageView;

    @Bind(R.id.secondImageLabel)
    TextView secondImageLabel;

    @Bind(R.id.result)
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter();
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
    public void showFirstImage(File photo) {
        firstImageLabel.setVisibility(View.INVISIBLE);
        showPhotoInImageView(photo, firstImageView);
    }

    @Override
    public void showSecondImage(File photo) {
        secondImageLabel.setVisibility(View.INVISIBLE);
        showPhotoInImageView(photo, secondImageView);
    }

    private void showPhotoInImageView(File photo, ImageView imageView) {
        if (photo.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(photo.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        } else {
            Log.d("photo", "doesnt exist");
        }
    }


    @Override
    public void showResult(double percentage) {
        result.setText(String.format("Result: %s", percentage));
        result.setVisibility(View.VISIBLE);
    }

    @Override
    public Context getContext() {
        return this;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            presenter.onIntentResult(requestCode);
        }
    }
}
