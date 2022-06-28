package com.ramble.ramblewallet.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.ramble.ramblewallet.R;
import com.ramble.ramblewallet.utils.DisplayHelper;

public class RecyclerGridDecoration extends RecyclerView.ItemDecoration {

    private int mColumn;
    private Context mContext;
    private Paint paint;

    public RecyclerGridDecoration(Context context, int column) {
        super();
        mContext = context;
        mColumn = column;
        this.paint = new Paint();
        paint.setColor(mContext.getResources().getColor(R.color.color_FFFFFF));
        paint.setStrokeWidth(DisplayHelper.dp2px(mContext, 2));
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        drawLine(parent, c);
    }

    private void drawLine(RecyclerView parent, Canvas c) {
        //获得RecyclerView中总条目数量
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            //先获得子View在屏幕上的位置和它自身的宽高，利用这些参数去画线
            float x = childView.getX();
            float y = childView.getY();
            float width = childView.getWidth();
            float height = childView.getHeight();

            if (i % mColumn == 0) {
                View view = parent.getChildAt(i);
                float x1 = view.getX();
                float y1 = view.getY();
                //top
                c.drawLine(x1, y1, DisplayHelper.getScreenWidth(mContext), y1, paint);
            }

            if ((i + 1) % mColumn != 0) {
                //right
                c.drawLine(x + width, y, x + width, y + height, paint);
            }

        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;
        outRect.set(left, top, right, bottom);
    }
}
