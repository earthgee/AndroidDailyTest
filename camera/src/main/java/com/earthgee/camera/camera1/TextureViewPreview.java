package com.earthgee.camera.camera1;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.earthgee.camera.R;
import com.earthgee.camera.base.PreviewImpl;

/**
 * Created by zhaoruixuan1 on 2023/10/9
 * CopyRight (c) haodf.com
 * 功能：
 */
public class TextureViewPreview extends PreviewImpl {

    private final TextureView mTextureView;

    private int mDisplayOrientation;

    private String TAG = "TextureViewPreview";

    public TextureViewPreview(Context context, ViewGroup parent) {
        View view = View.inflate(context, R.layout.texture_view, parent);
        mTextureView = view.findViewById(R.id.texture_view);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                Log.d(TAG, "TextureViewPreview onSurfaceTextureAvailable, width=" + width + ",height=" + height);
                setSize(width, height);
                configureTransform();
                dispatchSurfaceChanged();
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
                Log.d(TAG, "TextureViewPreview onSurfaceTextureSizeChanged");
                setSize(width, height);
                configureTransform();
                dispatchSurfaceChanged();
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                setSize(0, 0);
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
            }
        });
    }

    @Override
    public Surface getSurface() {
        return new Surface(mTextureView.getSurfaceTexture());
    }

    @Override
    public SurfaceTexture getSurfaceTexture() {
        return mTextureView.getSurfaceTexture();
    }

    @Override
    public View getView() {
        return mTextureView;
    }

    @Override
    public Class getOutputClass() {
        return SurfaceTexture.class;
    }

    @Override
    public void setDisplayOrientation(int displayOrientation) {
        mDisplayOrientation = displayOrientation;
        configureTransform();
    }

    @Override
    public boolean isReady() {
        return mTextureView.getSurfaceTexture() != null;
    }

    //为什么还要旋转？？？
    private void configureTransform() {
//        Matrix matrix = new Matrix();
//        if (mDisplayOrientation % 180 == 90) {
//            final int width = getWidth();
//            final int height = getHeight();
//            // Rotate the camera preview when the screen is landscape.
//            matrix.setPolyToPoly(
//                    new float[]{
//                            0.f, 0.f, // top left
//                            width, 0.f, // top right
//                            0.f, height, // bottom left
//                            width, height, // bottom right
//                    }, 0,
//                    mDisplayOrientation == 90 ?
//                            // Clockwise
//                            new float[]{
//                                    0.f, height, // top left
//                                    0.f, 0.f, // top right
//                                    width, height, // bottom left
//                                    width, 0.f, // bottom right
//                            } : // mDisplayOrientation == 270
//                            // Counter-clockwise
//                            new float[]{
//                                    width, 0.f, // top left
//                                    width, height, // top right
//                                    0.f, 0.f, // bottom left
//                                    0.f, height, // bottom right
//                            }, 0,
//                    4);
//        } else if (mDisplayOrientation == 180) {
//            matrix.postRotate(180, getWidth() / 2, getHeight() / 2);
//        }
//        mTextureView.setTransform(matrix);
    }

}
