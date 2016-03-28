package com.huilan.refreshableview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 阴影View
 * Created by Liu Denghui on 16/3/25.
 */
class ShadowView extends ImageView {
    private static final String TAG = "ShadowView";
    private int mDeepColor = 0x33bbbbbb;
    private int mLightColor = 0x00bbbbbb;
    private int elevation = 15;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Direction mDirection = Direction.Bottom;

    public ShadowView(Context context) {
        super(context);
    }

    public ShadowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShadowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ShadowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 控制阴影颜色
     *
     * @param lightColor 浅色,阴影的结束颜色
     * @param deepColor  深色,阴影的开始颜色
     */
    public void setShadowColor(int lightColor, int deepColor) {
        mDeepColor = deepColor;
        mLightColor = lightColor;
    }

    public float getElevation() {
        return elevation;
    }

    /**
     * 设置View的高度,阴影效果随高度改变而改变
     *
     * @param elevation View高度
     */
    @Override
    public void setElevation(float elevation) {
        this.elevation = (int) elevation;
    }

    public Direction getDirection() {
        return mDirection;
    }

    /**
     * 设置阴影方向
     *
     * @param direction 阴影方向
     */
    public void setDirection(Direction direction) {
        mDirection = direction;
    }

    /**
     * 颜色插值器
     *
     * @param c1    开始颜色
     * @param c2    结束颜色
     * @param ratio 进度
     * @return 颜色
     */
    private int getInterpolationColor(int c1, int c2, int ratio) {
        ratio = ratio < 0 ? 0 : ratio;
        ratio = ratio > 255 ? 255 : ratio;
        int r1 = Color.red(c1);
        int g1 = Color.green(c1);
        int b1 = Color.blue(c1);
        int a1 = Color.alpha(c1);
        int r2 = Color.red(c2);
        int g2 = Color.green(c2);
        int b2 = Color.blue(c2);
        int a2 = Color.alpha(c2);
        int r = (r1 * (255 - ratio) + r2 * ratio) >> 8;
        int g = (g1 * (255 - ratio) + g2 * ratio) >> 8;
        int b = (b1 * (255 - ratio) + b2 * ratio) >> 8;
        int a = (a1 * (255 - ratio) + a2 * ratio) >> 8;
        return Color.argb(a, r, g, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (elevation == 0) {
            return;
        }
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        // 阴影
        int step = 255 / elevation;
        if (mDirection == Direction.Top) {
            for (int i = 0; i < elevation; i++) {
                int shadowColor = getInterpolationColor(mDeepColor, mLightColor, step * (elevation - i));
                paint.setColor(shadowColor);
                canvas.drawRect(0, i, width, elevation, paint);
            }
        } else if (mDirection == Direction.Bottom) {
            for (int i = 0; i < elevation; i++) {
                int shadowColor = getInterpolationColor(mLightColor, mDeepColor, step * (elevation - i));
                paint.setColor(shadowColor);
                canvas.drawRect(0, 0, width, i, paint);
            }
        } else if (mDirection == Direction.Left) {
            for (int i = 0; i < elevation; i++) {
                int shadowColor = getInterpolationColor(mDeepColor, mLightColor, step * (elevation - i));
                paint.setColor(shadowColor);
                canvas.drawRect(i, 0, elevation, height, paint);
            }
        } else if (mDirection == Direction.Right) {
            for (int i = 0; i < elevation; i++) {
                int shadowColor = getInterpolationColor(mLightColor, mDeepColor, step * (elevation - i));
                paint.setColor(shadowColor);
                canvas.drawRect(0, 0, i, height, paint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mDirection == Direction.Top || mDirection == Direction.Bottom) {
            setMeasuredDimension(widthMeasureSpec, elevation);
        } else if (mDirection == Direction.Left || mDirection == Direction.Right) {
            setMeasuredDimension(elevation, heightMeasureSpec);
        }
    }

    /**
     * 阴影方向
     */
    public enum Direction {
        Top,
        Left,
        Right,
        Bottom
    }
}
