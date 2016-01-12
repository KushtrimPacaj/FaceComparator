package kushtrimpacaj.facecomp.views.view_interfaces;

import android.content.Intent;
import android.graphics.Bitmap;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by Kushtrim on 10.01.2016.
 */
public interface MainView extends MvpView {

    void showFirstImage(Bitmap photo);

    void showSecondImage(Bitmap photo);

    /**
     * @param isIdentical whether the same person is in both photos
     * @param percentage  from 0 -> 1
     */
    void showResult(boolean isIdentical, double percentage);

    void showProgressDialog(String title);

    void hideProgressDialog();

    void startActivityForResult(Intent takePictureIntent, int requestCode);

    void showMessage(String message);

    void removeFirstImage();

    void removeSecondImage();
}
