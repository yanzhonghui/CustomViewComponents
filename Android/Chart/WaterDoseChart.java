package cn.yanzhonghui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import cn.yanzhonghui.util.DensityUtils;

public class WaterDoseChart extends View {

    private Context mContext;

    private static final int CHART_LINE_COLOR = 0xFFCCCCCC;
    private static final int TEXT_COLOR = 0xFF959595;
    private static final int BAR_COLOR = 0xFF438ADF;

    /**
     * 图表线线宽
     */
    private int lineStrokeWidth = 0;
    /**
     * 柱状线宽
     */
    private int barStrokeWidth = 0;
    /**
     * 图表线（XY轴线、数值线）
     */
    private Paint mPaintChartLine;
    /**
     * 柱状
     */
    private Paint mPaintChartBar;
    /**
     * 文本
     */
    private Paint mPaintText;

    private Rect xAxisScaleTextRect;
    private Rect yAxisScaleTextRect;

    ///private int xTotalParts = 12;
    ///private int xAxisStep = 1;
    private int xTotalParts = 31;
    private int xAxisStep = 2;

    private int yTotalParts = 5;
    private int yAxisStep = 50;

    /**
     * X轴最大值刻度值
     */
    ///private int maxXAxisValue = 12;
    private int maxXAxisValue = 31;
    /**
     * Y轴最大值刻度值字符
     */
    private int maxYAxisValue = yTotalParts * yAxisStep;
    /**
     * X轴刻度文本最大宽度
     */
    private int xMaxAxisTextWidth;
    /**
     * X轴刻度文本最大高度
     */
    private int xMaxAxisTextHeight;
    /**
     * Y轴刻度文本最大宽度
     */
    private int yMaxAxisTextWidth;
    /**
     * Y轴刻度文本最大高度
     */
    private int yMaxAxisTextHeight;

    private int xAxisTextPadding = 0;
    private int yAxisTextPadding = 0;

    private int maxHeight = 0;
    private int maxWidth = 0;

    private int paddingLeft;
    private int paddingRight;
    private int paddingTop;
    private int paddingBottom;


    public WaterDoseChart(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public WaterDoseChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public WaterDoseChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WaterDoseChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init();
    }

    private void init() {
        lineStrokeWidth = DensityUtils.sp2px(mContext, 1);
        barStrokeWidth = DensityUtils.sp2px(mContext, 4);
        xAxisTextPadding = DensityUtils.sp2px(mContext, 5);
        yAxisTextPadding = DensityUtils.sp2px(mContext, 5);

        // 图表线（XY轴线、数值线）
        mPaintChartLine = new Paint();
        mPaintChartLine.setColor(CHART_LINE_COLOR);
        mPaintChartLine.setStyle(Paint.Style.STROKE);
        mPaintChartLine.setAntiAlias(true);
        mPaintChartLine.setStrokeWidth(lineStrokeWidth);
        mPaintChartLine.setPathEffect(new DashPathEffect(new float[]{
                DensityUtils.sp2px(mContext, 2),
                DensityUtils.sp2px(mContext, 1)},
                0));

        // 柱状
        mPaintChartBar = new Paint();
        mPaintChartBar.setColor(BAR_COLOR);
        mPaintChartBar.setStyle(Paint.Style.STROKE);
        mPaintChartBar.setAntiAlias(true);
        mPaintChartBar.setStrokeWidth(barStrokeWidth);

        // 文本
        mPaintText = new Paint();
        mPaintText.setColor(TEXT_COLOR);
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setAntiAlias(true);
        mPaintText.setTextSize(DensityUtils.sp2px(mContext, 12));

        xAxisScaleTextRect = new Rect();
        yAxisScaleTextRect = new Rect();
        mPaintText.getTextBounds(String.valueOf(maxXAxisValue), 0, String.valueOf(maxXAxisValue).length(), xAxisScaleTextRect);
        mPaintText.getTextBounds(String.valueOf(maxYAxisValue), 0, String.valueOf(maxYAxisValue).length(), yAxisScaleTextRect);


        xMaxAxisTextWidth = xAxisScaleTextRect.width();
        xMaxAxisTextHeight = xAxisScaleTextRect.height();

        yMaxAxisTextWidth = yAxisScaleTextRect.width();
        yMaxAxisTextHeight = yAxisScaleTextRect.height();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();

        maxWidth = this.getWidth() - paddingLeft - paddingRight;
        maxHeight = this.getHeight() - paddingTop - paddingBottom;

        // X轴长度 = 最大宽度 - 左右内间距 - Y轴刻度文本宽度 - Y轴文本距离Y轴间距
        int xLength = maxWidth - yMaxAxisTextWidth - yAxisTextPadding;
        // Y轴长度= 最大宽度 - 上下内间距 - X轴刻度文本高度 - X轴文本距离X轴间距
        int yLength = maxHeight - xMaxAxisTextHeight - xAxisTextPadding;

        // 左上角(0,0)
        int xZeroPoint = paddingLeft + yMaxAxisTextWidth + yAxisTextPadding;
        int xMaxPoint = maxWidth;
        int yZeroPoint = paddingTop;
        int yMaxPoint = yLength + paddingTop;

        int xOneScale = xLength / (xTotalParts + 2);// 多加2份，避免X轴坐标出界
        int yOneScale = yLength / yTotalParts;

        Path path = new Path();
        // XY轴
        path.moveTo(xZeroPoint, yZeroPoint);
        path.lineTo(xZeroPoint, yMaxPoint);
        path.lineTo(xMaxPoint, yMaxPoint);
        // 数值线（自上而下绘制）
        for (int i = 0; i < yTotalParts; i++) {
            path.moveTo(xZeroPoint, yZeroPoint + (yOneScale * (i)));
            path.lineTo(xMaxPoint, yZeroPoint + (yOneScale * (i)));
        }
        //
        canvas.drawPath(path, mPaintChartLine);

        mPaintText.setColor(TEXT_COLOR);
        // 绘制X轴刻度
        mPaintText.setTextAlign(Paint.Align.CENTER);
        for (int i = 1; i <= maxXAxisValue; i += xAxisStep) {
            canvas.drawText(
                    String.valueOf(i),
                    xOneScale * i + xZeroPoint,
                    yLength + paddingTop + xAxisTextPadding + xMaxAxisTextHeight,
                    mPaintText);
        }

        // 绘制Y轴刻度
        mPaintText.setTextAlign(Paint.Align.RIGHT);
        for (int i = yTotalParts; i >= 0; i--) {
            canvas.drawText(
                    String.valueOf(i * yAxisStep),
                    paddingLeft + yMaxAxisTextWidth,
                    yZeroPoint + (yOneScale * (yTotalParts - i) + yMaxAxisTextHeight / 2),
                    mPaintText);
        }

        // 绘制柱状图
        Path pathData = new Path();
        for (int i = 1; i <= maxXAxisValue; i++) {
            pathData.moveTo(xOneScale * i + xZeroPoint, yLength + paddingTop);
            pathData.lineTo(xOneScale * i + xZeroPoint, paddingTop+i);
        }
        canvas.drawPath(pathData, mPaintChartBar);

    }

}
