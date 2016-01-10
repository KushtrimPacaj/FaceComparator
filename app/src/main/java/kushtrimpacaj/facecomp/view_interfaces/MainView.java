package kushtrimpacaj.facecomp.view_interfaces;

import android.content.Context;
import android.content.Intent;

import com.hannesdorfmann.mosby.mvp.MvpView;

import java.io.File;

/**
 * Created by Kushtrim on 10.01.2016.
 */
public interface MainView extends MvpView {

    void showFirstImage(File photo);

    void showSecondImage(File photo);

    /**
     * @param percentage from 0 -> 1
     */
    void showResult(double percentage);

    Context getContext();

    void startActivityForResult(Intent takePictureIntent, int requestCode);
}
