package com.handeoktay.pollyfysunum.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.handeoktay.pollyfysunum.R;

import java.util.List;
import java.util.Locale;

/**
 * Created by HANDE OKTAY on 1.05.2018.
 */

public class Graph extends View {
    // Constant Dimensions
    private static final float GRAPH_LINE_THICKNESS = 7f;
    private static final float GRAPH_NODE_RADIUS = 10f;
    private static final float GRAPH_NODE_CONNECTION_THICKNESS = 5f;
    private static final float GRAPH_TARGET_THICKNESS = 3f;
    private static final float GRAPH_INDICATOR_THICKNESS = 3f;
    private static final float GRAPH_BACK_LINES_THICKNESS = 2f;

    private static final int GRAPTH_FONT_SIZE_HORIZONTAL_VALUES = 12;
    private static final int GRAPTH_FONT_SIZE_VERTICAL_VALUES = 10;

    // Constant Colors
    private static final int VIEW_BACKGROUND_COLOR = Color.parseColor("#f0f0f0");
    private static final int GRAPH_LINE_COLOR = Color.parseColor("#37474F");
    private static final int GRAPH_NODE_COLOR = Color.parseColor("#FFA000");
    private static final int GRAPH_NODE_CONNECTION_COLOR = Color.parseColor("#FFE082");
    private static final int GRAPH_TARGET_LINE_COLOR = Color.parseColor("#0277BD");
    private static final int GRAPH_INDICATOR_COLOR = Color.parseColor("#607D8B");
    private static final int GRAPH_BACK_LINES_COLOR = Color.parseColor("#e0e0e0");
    public int currentIndicatedIndex = -1;
    // List of Exams
    List<GraphNode> nodeList;
    // General Variables
    private float viewWidth;
    private float viewHeight;
    private Rectangle graphRect;
    // View Paddings
    private float paddingX = 0;
    private float paddingY = 0;
    // Graph Paddings
    private float graphPaddingStartX = 0;
    private float graphPaddingEndX = 0;
    private float graphPaddingStartY = 0;
    private float graphPaddingEndY = 0;
    // Indicator
    private float indicatorX = -1;
    // Canvas
    private Paint mainPaint;
    private Paint targetPaint;
    // Boundaries
    private float maxValue = 0;
    private float minValue = 0;
    private float gapBetweenNodes = 0;
    private float nodeScaleFactor = 0;
    private int nodeSize = 0;
    private boolean isValuesInteger = false;
    // Target Path
    private float targetValue = -1;
    private Path targetPath = null;
    // Listeners
    private GraphIndicatorListener graphIndicatorListener;
    private float actionDownX;
    private float actionDownIndicatorX;

    public Graph(Context context) {
        super(context);
        init();
    }

    public Graph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Graph(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Graph(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        targetPaint = new Paint();
        graphRect = new Rectangle();
        targetPath = new Path();


        targetPaint.setStyle(Paint.Style.STROKE);

        float scaledSizeInPixels = GRAPTH_FONT_SIZE_HORIZONTAL_VALUES * getResources().getDisplayMetrics().scaledDensity;
        mainPaint.setTextSize(scaledSizeInPixels);

        paddingX = 10;
        paddingY = 10;

        graphPaddingStartX = 50;
        graphPaddingEndX = 50;
        graphPaddingStartY = 50;
        graphPaddingEndY = scaledSizeInPixels * 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBackground(canvas);
        drawBackLines(canvas);
        drawIndicator(canvas);
        drawConnections(canvas);
        drawTargetLine(canvas);
        drawGraphLines(canvas);
        drawHorizontalValues(canvas);
        drawVerticalValues(canvas);
        drawNodes(canvas);

    }

    private void drawBackLines(Canvas canvas) {

        targetPaint.setColor(GRAPH_BACK_LINES_COLOR);
        targetPaint.setStrokeWidth(GRAPH_BACK_LINES_THICKNESS);
        targetPaint.setPathEffect(new DashPathEffect(new float[]{10, 0, 10, 30}, 0));

        for (int i = 0; i < nodeSize + 1; i++) {
            System.out.println(gapBetweenNodes);
            float currentX = gapBetweenNodes + (graphRect.left + i * gapBetweenNodes);
            canvas.drawLine(currentX, graphRect.top, currentX, graphRect.bottom, targetPaint);
        }


        for (int i = 0; i < nodeSize; i++) {
            float currentY = paddingY + graphPaddingStartY + i * (graphRect.height() / nodeSize);
            canvas.drawLine(graphRect.left, currentY, graphRect.right, currentY, targetPaint);
        }


    }

    private void drawBackground(Canvas canvas) {
        mainPaint.setColor(VIEW_BACKGROUND_COLOR);
        canvas.drawRect(paddingX, paddingY, viewWidth - paddingX, viewHeight - paddingY, mainPaint);


    }

    private void drawGraphLines(Canvas canvas) {
        mainPaint.setColor(GRAPH_LINE_COLOR);
        mainPaint.setStrokeWidth(GRAPH_LINE_THICKNESS);
        float verticalLineX = paddingX + graphPaddingStartX;
        float verticalLineY = paddingY + graphPaddingStartY;
        float horizontalLineY = viewHeight - paddingY - graphPaddingEndY;
        float horizontalLineX = viewWidth - paddingX - graphPaddingEndX;
        canvas.drawLine(verticalLineX, paddingY + graphPaddingStartY, verticalLineX, viewHeight - paddingY - graphPaddingEndY, mainPaint);
        canvas.drawLine(-GRAPH_LINE_THICKNESS / 2 + paddingX + graphPaddingStartX, horizontalLineY, viewWidth - paddingX - graphPaddingEndX, horizontalLineY, mainPaint);

        float widthOfTriangle = GRAPH_LINE_THICKNESS * 5;
        float heightOfTriangle = widthOfTriangle * 1f;

        float verticalLineTriangleLeftCornerX = verticalLineX - widthOfTriangle / 2;
        float verticalLineTriangleRightCornerX = verticalLineX + widthOfTriangle / 2;
        float verticalLineTriangleBottomCornerY = verticalLineY;
        float verticalLineTriangleTopCornerY = verticalLineTriangleBottomCornerY - heightOfTriangle;

        Path verticalLineTrianglePath = new Path();
        verticalLineTrianglePath.setFillType(Path.FillType.EVEN_ODD);

        verticalLineTrianglePath.moveTo(verticalLineTriangleLeftCornerX, verticalLineTriangleBottomCornerY);
        verticalLineTrianglePath.lineTo(verticalLineX, verticalLineTriangleTopCornerY);
        verticalLineTrianglePath.lineTo(verticalLineTriangleRightCornerX, verticalLineTriangleBottomCornerY);

        verticalLineTrianglePath.close();

        canvas.drawPath(verticalLineTrianglePath, mainPaint);

        float horizontalLineTriangleTopCornerY = horizontalLineY - widthOfTriangle / 2;
        float horizontalLineTriangleBottomCornerY = horizontalLineY + widthOfTriangle / 2;
        float horizontalLineTriangleLeftCornerX = horizontalLineX;
        float horizontalLineTriangleRightCornerX = horizontalLineX + heightOfTriangle;

        Path horizontalLineTrianglePath = new Path();
        horizontalLineTrianglePath.setFillType(Path.FillType.EVEN_ODD);
        horizontalLineTrianglePath.moveTo(horizontalLineTriangleLeftCornerX, horizontalLineTriangleTopCornerY);
        horizontalLineTrianglePath.lineTo(horizontalLineTriangleRightCornerX, horizontalLineY);
        horizontalLineTrianglePath.lineTo(horizontalLineTriangleLeftCornerX, horizontalLineTriangleBottomCornerY);
        horizontalLineTrianglePath.close();

        canvas.drawPath(horizontalLineTrianglePath, mainPaint);


    }

    private void drawNodes(Canvas canvas) {

        for (int i = 0; i < nodeSize; i++) {
            System.out.println(gapBetweenNodes);
            float currentX = gapBetweenNodes + (graphRect.left + i * gapBetweenNodes);
            float currentY = graphRect.top + (graphRect.height() - ((nodeList.get(i).getValue() - minValue) * nodeScaleFactor));

            if (graphIndicatorListener != null && indicatorX < currentX + gapBetweenNodes / 2 && indicatorX > currentX - gapBetweenNodes / 2) {
                if (currentIndicatedIndex != i) {
                    currentIndicatedIndex = i;
                    graphIndicatorListener.onIndicatedValueChanged(i);
                }
            }

            if (currentIndicatedIndex == i) {
                String text = String.format(Locale.getDefault(), "%.2f", nodeList.get(i).value);
                float width = mainPaint.measureText(text);
                mainPaint.setColor(getResources().getColor(R.color.colorPrimary));
                canvas.drawRect(currentX - width / 2 - 10, currentY - 70, currentX + width / 2 + 10, currentY - 25, mainPaint);
                mainPaint.setColor(Color.parseColor("#FFFFFF"));
                canvas.drawText(text, currentX - width / 2, currentY - 40, mainPaint);
            }

            mainPaint.setColor(GRAPH_NODE_COLOR);
            canvas.drawCircle(currentX, currentY, GRAPH_NODE_RADIUS, mainPaint);
        }
    }

    private void drawHorizontalValues(Canvas canvas) {
        mainPaint.setStrokeWidth(1);

        float scaledSizeInPixels = GRAPTH_FONT_SIZE_HORIZONTAL_VALUES * getResources().getDisplayMetrics().scaledDensity;
        mainPaint.setTextSize(scaledSizeInPixels);
        mainPaint.setColor(GRAPH_LINE_COLOR);
        for (int i = 0; i < nodeSize; i++) {
            float currentX = gapBetweenNodes + (graphRect.left + i * gapBetweenNodes);
            float currentY = graphRect.bottom + (graphPaddingEndY + paddingY) / 2;

            canvas.drawLine(currentX, graphRect.bottom - GRAPH_LINE_THICKNESS, currentX, graphRect.bottom + GRAPH_LINE_THICKNESS, mainPaint);

            String text = (i + 1) + "";
            float textWidth = mainPaint.measureText(text);

            if (i == currentIndicatedIndex) {
                mainPaint.setColor(GRAPH_LINE_COLOR);
            } else {
                mainPaint.setColor(GRAPH_LINE_COLOR / 2);
            }
            canvas.drawText(text, currentX - textWidth / 2, currentY, mainPaint);
        }
    }

    private void drawVerticalValues(Canvas canvas) {
        mainPaint.setStrokeWidth(1);

        float scaledSizeInPixels = GRAPTH_FONT_SIZE_VERTICAL_VALUES * getResources().getDisplayMetrics().scaledDensity;
        mainPaint.setTextSize(scaledSizeInPixels);
        mainPaint.setColor(GRAPH_LINE_COLOR / 2);


        for (int i = 0; i < nodeSize; i++) {
            float currentX = graphRect.left - graphPaddingStartX / 2;
            float currentY = paddingY + graphPaddingStartY + i * (graphRect.height() / nodeSize);

            float value = ((graphRect.height() + +graphRect.top - currentY) / nodeScaleFactor) + minValue;

            String text;
            if (isValuesInteger) {
                text = ((int) value) + "";
            } else {
                text = String.format(Locale.getDefault(), "%.1f", value);
            }
            float textWidth = mainPaint.measureText(text);


            mainPaint.setColor(GRAPH_LINE_COLOR / 2);


            canvas.drawText(text, currentX - textWidth / 2, currentY + mainPaint.getTextSize() / 2, mainPaint);
        }
        mainPaint.setColor(GRAPH_TARGET_LINE_COLOR);
        float currentX = graphRect.left - graphPaddingStartX / 2;
        float currentY = graphRect.top + (graphRect.height() - ((targetValue - minValue) * nodeScaleFactor));
        String text;
        if (isValuesInteger) {
            text = ((int) targetValue) + "";
        } else {
            text = targetValue + "";
        }
        float textWidth = mainPaint.measureText(text);
        if (targetValue != -1) {
            canvas.drawText(text, currentX - textWidth / 2, currentY + targetPaint.getTextSize() / 2, mainPaint);
        }
    }

    private void drawConnections(Canvas canvas) {
        mainPaint.setColor(GRAPH_NODE_CONNECTION_COLOR);
        mainPaint.setStrokeWidth(GRAPH_NODE_CONNECTION_THICKNESS);

        float lastX = -1;
        float lastY = -1;

        for (int i = 0; i < nodeSize; i++) {
            float currentX = gapBetweenNodes + (graphRect.left + i * gapBetweenNodes);
            float currentY = graphRect.top + (graphRect.height() - ((nodeList.get(i).getValue() - minValue) * nodeScaleFactor));

            if (!(lastX == -1 && lastY == -1)) {
                canvas.drawLine(lastX, lastY, currentX, currentY, mainPaint);
            }

            lastX = currentX;
            lastY = currentY;
        }
    }

    private void drawIndicator(Canvas canvas) {
        mainPaint.setColor(GRAPH_INDICATOR_COLOR);
        mainPaint.setStrokeWidth(GRAPH_INDICATOR_THICKNESS);

        canvas.drawLine(indicatorX, graphRect.top, indicatorX, graphRect.bottom, mainPaint);
    }

    private void drawTargetLine(Canvas canvas) {
        targetPaint.setColor(GRAPH_TARGET_LINE_COLOR);
        targetPaint.setPathEffect(new DashPathEffect(new float[]{10, 15, 20, 25}, 0));
        targetPaint.setStrokeWidth(GRAPH_TARGET_THICKNESS);
        if (targetValue != -1) {
            targetPath.moveTo(graphRect.left, graphRect.top + (graphRect.height() - ((targetValue - minValue) * nodeScaleFactor)));
            targetPath.lineTo(gapBetweenNodes + (graphRect.left + nodeSize * gapBetweenNodes), graphRect.top + (graphRect.height() - ((targetValue - minValue) * nodeScaleFactor)));
            canvas.drawPath(targetPath, targetPaint);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getMeasuredWidth();
        viewHeight = viewWidth / 1.5f;

        graphRect.set(
                paddingX + graphPaddingStartX, //left
                paddingY + graphPaddingStartY,  // top
                viewWidth - paddingX - graphPaddingEndX, // right
                viewHeight - paddingY - graphPaddingEndY // bottom
        );

        setMeasuredDimension(measureDimension((int) viewWidth, widthMeasureSpec), measureDimension((int) viewHeight, heightMeasureSpec));

        maxValue = maxValue + (maxValue * 0.1f);
        if (minValue < 0) {
            minValue = minValue + (minValue * 0.1f);
        } else {
            minValue = minValue - (minValue * 0.1f);
        }

        this.gapBetweenNodes = graphRect.width() / (nodeSize + 1);
        this.nodeScaleFactor = (graphRect.height() / ((maxValue - minValue)));

        indicatorX = gapBetweenNodes + (graphRect.left + 0 * gapBetweenNodes);
    }

    private int measureDimension(int desiredSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = desiredSize;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        if (result < desiredSize) {
            Log.e("Graph", "The graph is too small");
        }
        return result;
    }

    public void next() {
        if (currentIndicatedIndex < nodeList.size() - 1)
            indicatorX = gapBetweenNodes + (graphRect.left + ++currentIndicatedIndex * gapBetweenNodes);
        graphIndicatorListener.onIndicatedValueChanged(currentIndicatedIndex);
        invalidate();
    }

    public void previous() {
        if (currentIndicatedIndex > 0)
            indicatorX = gapBetweenNodes + (graphRect.left + --currentIndicatedIndex * gapBetweenNodes);
        graphIndicatorListener.onIndicatedValueChanged(currentIndicatedIndex);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("VIEW/EXAM_GRAPH");

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                actionDownX = event.getX();
                actionDownIndicatorX = indicatorX;
                break;

            case MotionEvent.ACTION_MOVE:
                indicatorX = actionDownIndicatorX + (event.getX() - actionDownX);
                if (indicatorX <= graphRect.left + gapBetweenNodes) {
                    indicatorX = graphRect.left + gapBetweenNodes;
                }
                if (indicatorX >= graphRect.right - gapBetweenNodes) {
                    indicatorX = graphRect.right - gapBetweenNodes;
                }
                postInvalidate();
                break;
        }


        return true;
    }

    public Graph setTargetValue(float value) {
        this.targetValue = value;
        return this;
    }

    public Graph setValuesAreInteger(boolean valuesInteger) {
        isValuesInteger = valuesInteger;
        return this;
    }

    public Graph setNodeList(List<GraphNode> nodeList) {
        this.nodeList = nodeList;
        this.nodeSize = nodeList.size();
        this.maxValue = Float.MIN_VALUE;
        this.minValue = Float.MAX_VALUE;
        float maxWidthOfValues = 0;
        for (GraphNode graphNode : nodeList) {
            if (graphNode.value > maxValue) {
                this.maxValue = graphNode.value;
            }
            if (graphNode.value < minValue) {
                this.minValue = graphNode.value;
            }

        }

        if (targetValue != -1) {
            if (targetValue > maxValue) {
                maxValue = targetValue;
            }

            String text;
            if (isValuesInteger) {
                text = ((int) targetValue) + "";
            } else {
                text = targetValue + "";
            }
            float valueWidth = mainPaint.measureText(text);
            if (valueWidth > maxWidthOfValues) {
                maxWidthOfValues = valueWidth;
            }
        } else {
            maxWidthOfValues = 15;
        }

        maxWidthOfValues = mainPaint.measureText("500.3");
        this.graphPaddingStartX = maxWidthOfValues * 1.5f;

        return this;
    }

    public void build() {
        requestLayout();
        invalidate();
    }

    public void setGraphIndicatorListener(GraphIndicatorListener graphIndicatorListener) {
        this.graphIndicatorListener = graphIndicatorListener;
    }

    public interface GraphIndicatorListener {
        void onIndicatedValueChanged(int changedIndex);
    }

    public static class GraphNode {
        //        private Exam exam;
        private float value;

        public GraphNode(/*Exam exam,*/ float value) {
            this.value = value;
        }

        public float getValue() {
            return value;
        }
    }

    public class Rectangle {
        private float left, top, right, bottom;

        public Rectangle(float left, float top, float right, float bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        public Rectangle() {

        }

        public void set(float left, float top, float right, float bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        public float height() {
            return bottom - top;
        }

        public float width() {
            return right - left;
        }
    }

}
