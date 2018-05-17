package com.swift.sectionsupport;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by swift_gan on 2018/4/20.
 */

public class MyDividerItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    private static final String TAG = "DividerItem";
    private static final int[] ATTRS = new int[]{ android.R.attr.listDivider };

    private Drawable mDivider;

    /**
     * Current orientation. Either {@link #HORIZONTAL} or {@link #VERTICAL}.
     */
    private int mOrientation;

    private final Rect mBounds = new Rect();

    private int paddingStart, paddingEnd, dividerHeight;

    private CanDrawDividerCallback canDrawDividerCallback;

    /**
     * Creates a divider {@link RecyclerView.ItemDecoration} that can be used with a
     * {@link LinearLayoutManager}.
     *
     * @param context Current context, it will be used to access resources.
     * @param orientation Divider orientation. Should be {@link #HORIZONTAL} or {@link #VERTICAL}.
     */
    public MyDividerItemDecoration(Context context, int orientation) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        dividerHeight = mDivider.getIntrinsicHeight();
        if (mDivider == null) {
            Log.w(TAG, "@android:attr/listDivider was not set in the theme used for this "
                    + "DividerItemDecoration. Please set that attribute all call setDrawable()");
        }
        a.recycle();
        setOrientation(orientation);
    }

    /**
     * Sets the orientation for this divider. This should be called if
     * {@link RecyclerView.LayoutManager} changes orientation.
     *
     * @param orientation {@link #HORIZONTAL} or {@link #VERTICAL}
     */
    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException(
                    "Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        }
        mOrientation = orientation;
    }

    public MyDividerItemDecoration setCanDrawDividerCallback(CanDrawDividerCallback canDrawDividerCallback) {
        this.canDrawDividerCallback = canDrawDividerCallback;
        return this;
    }

    public MyDividerItemDecoration setPadding(int paddingStart, int paddingEnd) {
        this.paddingStart = paddingStart;
        this.paddingEnd = paddingEnd;
        return this;
    }

    public MyDividerItemDecoration setDividerHeight(int dividerHeight) {
        this.dividerHeight = dividerHeight;
        return this;
    }

    /**
     * Sets the {@link Drawable} for this divider.
     *
     * @param drawable Drawable that should be used as a divider.
     */
    public void setDrawable(@NonNull Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("Drawable cannot be null.");
        }
        mDivider = drawable;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() == null || mDivider == null) {
            return;
        }
        if (mOrientation == VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int left;
        final int right;
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft() + paddingStart;
            right = parent.getWidth() - parent.getPaddingRight() - paddingEnd;
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0 + paddingStart;
            right = parent.getWidth() - paddingEnd;
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            if (canDraw(parent, child)) {
                parent.getDecoratedBoundsWithMargins(child, mBounds);
                final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
                final int top = bottom - dividerHeight;
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
        }
        canvas.restore();
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        canvas.save();
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            if (canDraw(parent, child)) {
                parent.getLayoutManager().getDecoratedBoundsWithMargins(child, mBounds);
                int top = mBounds.top + paddingStart;
                int bottom = mBounds.bottom - paddingEnd;
                final int right = mBounds.right + Math.round(child.getTranslationX());
                final int left = right - dividerHeight;
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
        }
        canvas.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (mDivider == null || !canDraw(parent, view)) {
            outRect.set(0, 0, 0, 0);
            return;
        }
        if (mOrientation == VERTICAL) {
            outRect.set(0, 0, 0, dividerHeight);
        } else {
            outRect.set(0, 0, dividerHeight, 0);
        }
    }

    private boolean canDraw(RecyclerView parent, View child) {
        int adapterPos = parent.getChildAdapterPosition(child);
        if (child.getVisibility() != View.VISIBLE)
            return false;
        return canDrawDividerCallback == null || (canDrawDividerCallback != null && canDrawDividerCallback.canDrawDivider(adapterPos, child));
    }

    @FunctionalInterface
    public interface CanDrawDividerCallback {
        boolean canDrawDivider(int position, View itemView);
    }

}
