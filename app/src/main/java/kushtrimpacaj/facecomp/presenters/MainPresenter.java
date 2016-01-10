package kushtrimpacaj.facecomp.presenters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.io.File;

import kushtrimpacaj.facecomp.view_interfaces.MainView;

/**
 * Created by Kushtrim on 10.01.2016.
 */
@SuppressWarnings("ConstantConditions")
public class MainPresenter extends MvpBasePresenter<MainView> {
    public static final int FIRST_PHOTO_REQUEST_CODE = 1;
    public static final int SECOND_PHOTO_REQUEST_CODE = 2;

    public static final String photoNamePrefix = "faceCompPhoto_";


    public void onChooseFirstPhoto() {
        dispatchTakePictureIntent(FIRST_PHOTO_REQUEST_CODE, photoNamePrefix + FIRST_PHOTO_REQUEST_CODE);

    }

    public void onChooseSecondPhoto() {
        dispatchTakePictureIntent(SECOND_PHOTO_REQUEST_CODE, photoNamePrefix + SECOND_PHOTO_REQUEST_CODE);

    }

    public void onComparePhotos() {

    }

    public void onIntentResult(int requestCode) {
        if (isViewAttached()) {
            File photoFile = getPhotoFile(photoNamePrefix + requestCode);
            switch (requestCode) {
                case FIRST_PHOTO_REQUEST_CODE:
                    getView().showFirstImage(photoFile);
                    break;

                case SECOND_PHOTO_REQUEST_CODE:
                    getView().showSecondImage(photoFile);
                    break;

            }
        }
    }


    private void dispatchTakePictureIntent(int requestCode, String photoFilename) {
        if (isViewAttached()) {
            Context context = getView().getContext();
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {

                File photoFile = getPhotoFile(photoFilename);
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    getView().startActivityForResult(takePictureIntent, requestCode);
                }
            }
        }
    }

    private File getPhotoFile(String photoFilename) {
        File file = null;
        if (isViewAttached()) {
            File storageDir = getView().getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            file = new File(storageDir, photoFilename + ".jpg");//File.createTempFile(photoFilename, ".jpg", storageDir);
        }
        return file;
    }
}
