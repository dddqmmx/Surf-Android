package com.dd.surf.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;


public class ClipPathCircleView extends AppCompatImageView {

    private Paint mPaint;
    private int mRadius;// 圆形图片的半径

    private Path mPath;
    private RectF mRect;
    private Bitmap mBitmap;


    public ClipPathCircleView(Context context) {
        super(context);
        init();
    }

    public ClipPathCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClipPathCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init(){
        init(drawableToBitmap(getDrawable()));
    }

    private void init(Bitmap mBitmap){
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);

        mPath = new Path();
        mRect = new RectF();
        this.mBitmap = mBitmap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = Math.min(getMeasuredWidth(),getMeasuredHeight());
        mRadius = size / 2;
        setMeasuredDimension(size,size);
    }

    //canvas.save();
    //Path path = new Path();
    //path.addCircle(300, 300, 200, Path.Direction.CCW); // 画一个圆形的path
    //canvas.clipPath(path); // 裁剪画布的区域为圆形
    //RectF rectF = new RectF(100, 100, 500, 500);
    //canvas.drawBitmap(photo, null, rectF, paint); // 在区域之外的部分不会被渲染出来
    //canvas.restore();
    @Override
    protected void onDraw(Canvas canvas) {
        // 注意如果这行不注释调，ImageView会把原图画在底部
        //super.onDraw(canvas);
        mRect.set(0,0,mRadius*2,mRadius*2);
        mBitmap = thumbImageWithMatrix(mRadius*2,mRadius*2,mBitmap);

        mPath.addCircle(mRadius,mRadius,mRadius, Path.Direction.CCW);// 逆时针
        canvas.clipPath(mPath);
        // 第一个rect是要画图片的哪个区域，第一个rect是画到哪里
        canvas.drawBitmap(mBitmap,null,mRect,mPaint);

    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        init(bm);
    }

    //写一个drawble转BitMap的方法
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }



    // https://www.jianshu.com/p/abcfa74c967b
    /**
     * 此方法对图片进行缩小，无法放大
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    private Bitmap zoomBitmap(int targetWidth,int targetHeight, int bitmapRes){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(),bitmapRes,options);
        float scaleW = options.outWidth / (targetWidth*1f);
        float scaleH = options.outHeight / (targetHeight*1f);
        int size = (int)Math.max(scaleW,scaleH);
        if (size <= 1){
            options.inSampleSize = 1;
        }else {
            options.inSampleSize = size;
        }
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(getResources(),bitmapRes,options);
    }

    private Bitmap zoomBitmapV1(int targetWidth,int targetHeight, int bitmapRes){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), bitmapRes, options);
        options.inJustDecodeBounds = false;
        //设置位图的屏幕密度,即每英寸有多少个像素
        options.inDensity = options.outWidth;
        //设置位图被画出来时的目标像素密度
        //与options.inDensity配合使用,可对图片进行缩放
        options.inTargetDensity = targetWidth;
        return BitmapFactory.decodeResource(getResources(),bitmapRes,options);
    }

    /**
     * 对图片进行缩放
     * 图片始终都会被加载到内存中，注意OOM
     * @param destWidth
     * @param destHeight
     * @param bitmapOrg
     * @return
     */
    public Bitmap thumbImageWithMatrix(float destWidth, float destHeight,Bitmap bitmapOrg) {
        float bitmapOrgW = bitmapOrg.getWidth();
        float bitmapOrgH = bitmapOrg.getHeight();

        float bitmapNewW = (int) destWidth;
        float bitmapNewH = (int) destHeight;

        Matrix matrix = new Matrix();
        matrix.postScale(bitmapNewW / bitmapOrgW, bitmapNewH / bitmapOrgH);
        Bitmap destBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, (int) bitmapOrgW, (int) bitmapOrgH, matrix, true);
        //bitmapOrg.recycle();
        return destBitmap;
    }

}
