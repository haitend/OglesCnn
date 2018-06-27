package com.example.cnnlib.layer;

import android.content.Context;

import com.example.cnnlib.utils.DataUtils;

import java.util.List;

public abstract class Layer {

    protected Context mContext;
    protected Layer mPreLayer;
    protected int[] mOutputShape;

    protected int mOutTex;
    protected int mAttachID;
    private List<float[]> mResult;

    public Layer(Context context, int[] shape, Layer preLayer) {
        this.mContext = context;
        this.mPreLayer = preLayer;
        if (this instanceof FlatLayer) {
            this.mOutputShape = calculateFlatShape();
        } else {
            this.mOutputShape = shape;
        }
    }

    private int[] calculateFlatShape() {
        int[] inputShape = mPreLayer.getOutputShape();
        if (inputShape[2] % 4!=0) {
            try {
                throw new Exception("通道数必须为4的倍数");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int channel = 4;
        int height = inputShape[0] * inputShape[1];
        int width = inputShape[2] / 4;
        return new int[]{width, height, channel};
    }

    public int getAttachID() {
        return mAttachID;
    }

    public int getOutTex() {
        return mOutTex;
    }

    public int[] getOutputShape() {
        return mOutputShape;
    }

    public abstract void initialize();

    protected abstract void bindTextureAndBuffer();

    protected abstract void actualForwardProc();

    public void forwardProc(boolean restore) {
        bindTextureAndBuffer();
        actualForwardProc();
        if (restore) {
            restoreResult();
        }
    }

    private void restoreResult() {
        mResult = DataUtils.readOutput(this);
    }

}
