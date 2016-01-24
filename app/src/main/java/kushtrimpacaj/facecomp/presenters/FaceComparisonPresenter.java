package kushtrimpacaj.facecomp.presenters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.File;
import java.util.UUID;

import javax.inject.Inject;

import kushtrimpacaj.facecomp.interactors.FaceApiInteractor;
import kushtrimpacaj.facecomp.views.view_interfaces.FaceComparisonView;

/**
 * Created by Kushtrim on 10.01.2016.
 */
@SuppressWarnings("ConstantConditions")
public class FaceComparisonPresenter extends MvpBasePresenter<FaceComparisonView> {
    public static final int FIRST_PHOTO_INDEX = 0;
    public static final int SECOND_PHOTO_INDEX = 1;

    public static final String photoNamePrefix = "faceCompPhoto_"; // + photoIndex.JPG

    FaceApiInteractor faceApiInteractor;
    Context applicationContext;

    private UUID[] imageUUIDs = new UUID[2];


    @Inject
    public FaceComparisonPresenter(FaceApiInteractor faceApiInteractor, Context applicationContext) {
        this.faceApiInteractor = faceApiInteractor;
        this.applicationContext = applicationContext;
    }


    public void onChooseFirstPhoto() {
        dispatchTakePictureIntent(FIRST_PHOTO_INDEX);
    }

    public void onChooseSecondPhoto() {
        dispatchTakePictureIntent(SECOND_PHOTO_INDEX);
    }

    public void onComparePhotos() {
        getView().showProgressDialog("Comparing photos");

        boolean validUUIDs = checkIfValidUUIDs();
        if (validUUIDs) {
            faceApiInteractor.compareFaces(imageUUIDs[0], imageUUIDs[1], new FaceApiInteractor.FaceComparisonCallback() {
                @Override
                public void onError(String message) {
                    if (isViewAttached()) {
                        getView().hideProgressDialog();
                        getView().showMessage(message);
                    }
                }

                @Override
                public void onFaceCompared(boolean isIdentical, double confidence) {
                    if (isViewAttached()) {
                        getView().hideProgressDialog();
                        getView().showResult(isIdentical, confidence);
                    }
                }
            });
        }

    }

    private boolean checkIfValidUUIDs() {
        boolean valid = true;
        if (imageUUIDs[0] == null && imageUUIDs[1] == null) {
            getView().showMessage("Cannot compare! No faces detected in either image");
            valid = false;
        } else if (imageUUIDs[0] == null) {
            getView().showMessage("Cannot compare! No faces detected in the first image");
            valid = false;
        } else if (imageUUIDs[0] == null) {
            getView().showMessage("Cannot compare! No faces detected in the second image");
            valid = false;
        }
        return valid;
    }

    public void onChoosePhotoIntentResult(int requestCode) {
        if (isViewAttached()) {
            File photoFile = getPhotoFile(requestCode);
            if (photoFile.exists()) {
                Bitmap photoBmp = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                switch (requestCode) {
                    case FIRST_PHOTO_INDEX:
                        getView().showFirstImage(photoBmp);
                        break;

                    case SECOND_PHOTO_INDEX:
                        getView().showSecondImage(photoBmp);
                        break;
                }
                uploadAndDetectFaces(photoBmp, requestCode);
            } else {
                Log.d("photo", "doesnt exist");
            }
        }
    }

    private void uploadAndDetectFaces(Bitmap photoBmp, final int photoIndex) {
        getView().showProgressDialog("Uploading photo to FaceApi servers");
        imageUUIDs[photoIndex] = null; // clear the id previously stored for this photoIndex
        faceApiInteractor.detectFaces(photoBmp, new FaceApiInteractor.FaceDetectionCallback() {
            @Override
            public void onFaceDetected(Face[] faces) {
                if (isViewAttached()) {
                    getView().hideProgressDialog();
                    boolean error = false;
                    switch (faces.length) {
                        case 0:
                            getView().showMessage("No faces detected in picture " + (photoIndex+1));
                            error = true;
                            break;
                        case 1:
                            imageUUIDs[photoIndex] = faces[0].faceId;
                            break;
                        default:
                            getView().showMessage("Too many faces detected in picture " + (photoIndex+1) + ". Please select an image with only one face.");
                            error = true;
                            break;
                    }

                    if (error) {
                        removeImage(photoIndex);
                    }
                }
            }

            @Override
            public void onError(String message) {
                if (isViewAttached()) {
                    getView().hideProgressDialog();
                    getView().showMessage(message);
                    removeImage(photoIndex);
                }
            }
        });
    }

    private void removeImage(int photoIndex) {
        switch (photoIndex) {
            case FIRST_PHOTO_INDEX:
                getView().removeFirstImage();
                break;
            case SECOND_PHOTO_INDEX:
                getView().removeSecondImage();
        }
    }

    private void dispatchTakePictureIntent(int photoIndex) {
        if (isViewAttached()) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(applicationContext.getPackageManager()) != null) {

                File photoFile = getPhotoFile(photoIndex);
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    getView().startActivityForResult(takePictureIntent, photoIndex);
                }
            }
        }
    }

    private String generatePhotoName(int photoIndex) {
        return photoNamePrefix + photoIndex;
    }

    private File getPhotoFile(int photoIndex) {
        File storageDir = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(storageDir, generatePhotoName(photoIndex) + ".jpg");//File.createTempFile(photoFilename, ".jpg", storageDir);
    }
}
