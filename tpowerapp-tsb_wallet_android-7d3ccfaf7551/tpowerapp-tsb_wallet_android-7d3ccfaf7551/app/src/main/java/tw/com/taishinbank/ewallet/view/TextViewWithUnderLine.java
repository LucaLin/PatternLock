package tw.com.taishinbank.ewallet.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewWithUnderLine extends TextView {

    private int lineColor = Color.BLACK;// 線的顏色
    private float lineWidth = 2f; // 線的寬度
    private int numberOfLines = 4; // 畫幾條線

    public TextViewWithUnderLine(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewWithUnderLine(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        //创建画笔
        Paint mPaint = new Paint();
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(lineColor);

        // 取得高度、寬度、左右下方的留白
        int width = this.getWidth();
        int height = this.getHeight();
        int padB = this.getPaddingBottom();
        int padL = this.getPaddingLeft();
        int padR = this.getPaddingRight();
        float sizeExtra = this.getTextSize();

        sizeExtra = (getLineHeight() - sizeExtra) / 2;

        // 從上往下畫
        for (int i = 0; i < numberOfLines; i++)
            canvas.drawLine(padL //startX
                    , getLineHeight() * (i + 1) - lineWidth - sizeExtra //startY
                    , width - padR //endX
                    , getLineHeight() * (i + 1) - lineWidth - sizeExtra //endY
                    , mPaint);
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int color) {
        this.lineColor = color;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float width) {
        this.lineWidth = width;
    }


    public int getNumberOfLines() {
        return numberOfLines;
    }

    public void setNumberOfLines(int numberOfLines) {
        this.numberOfLines = numberOfLines;
    }

}