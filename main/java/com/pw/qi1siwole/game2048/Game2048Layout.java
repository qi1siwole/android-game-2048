package com.pw.qi1siwole.game2048;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Random;


/**
 * Created by user on 2017/3/29.
 */

public class Game2048Layout extends RelativeLayout {

    private int mColumnNum;
    private int mMargin;
    private int mPadding;
    private boolean mOnce;
    private int mScore;

    private OnGame2048LayoutListener mOnGame2048LayoutListener;
    private GestureDetector mGestureDetector;
    private Game2048Item[] mItems;

    public Game2048Layout(Context context) {
        this(context, null);
    }

    public Game2048Layout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Game2048Layout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mColumnNum = 4;
        mMargin = 10;
        mOnce = false;
        mScore = 0;
        mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mMargin, getResources().getDisplayMetrics());
        mPadding = min(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());

        mGestureDetector = new GestureDetector(context, new MyGestureDetector());

        mItems = new Game2048Item[mColumnNum * mColumnNum];
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int length = Math.min(getMeasuredHeight(), getMeasuredWidth());
        int childWidth = (length - 2 * mPadding - (mColumnNum - 1) * mMargin) / mColumnNum;
        if (!mOnce) {
            for (int i = 0; i < mItems.length; ++i) {
                mItems[i] = new Game2048Item(getContext());
                mItems[i].setId(i + 1);
                RelativeLayout.LayoutParams lp = new LayoutParams(childWidth, childWidth);
                // 除了第一列
                if (i % mColumnNum != 0) {
                    lp.leftMargin = mMargin;
                    lp.addRule(RIGHT_OF, mItems[i - 1].getId());
                }
                // 除了第一行
                if (i >= mColumnNum) {
                    lp.topMargin = mMargin;
                    lp.addRule(BELOW, mItems[i - mColumnNum].getId());
                }

                addView(mItems[i], lp);
            }

            for (int i = 0; i < 2; ++i) {
                generateNum();
            }

            mOnce = true;
        }

        setMeasuredDimension(length, length);
    }

    private int min(int... params) {
        int res = params[0];
        for (int param: params) {
            if (param < res) {
                res = param;
            }
        }
        return res;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    private enum ACTION {
        LEFT, RIGHT, UP, DOWN
    }

    private void showTip(int id) {
        Toast.makeText(getContext(), id, Toast.LENGTH_SHORT).show();
    }

    private void action(ACTION action) {
        if (isGameOver()) {
            performGameOver();
            return;
        }
        int[] numbers = new int[mItems.length];
        int addScore = move(action, numbers);
        if (addScore > 0) {
            mScore += addScore;
            showScore();
        }
        if (updateItemsWith(numbers)) {
            generateNum();
        }
        if (isGameOver()) {
            performGameOver();
            return;
        }
    }

    /*
     * 修改有变化的Item
     */
    private boolean updateItemsWith(int[] numbers) {
        boolean hasChanged = false;
        for (int i = 0; i < numbers.length; ++i) {
            if (mItems[i].getNumber() != numbers[i]) {
                mItems[i].setNumber(numbers[i]);
                hasChanged = true;
            }
        }
        return hasChanged;
    }

    /*
     * 得到移动后的结果数组
     */
    private int move(ACTION action, int[] outNumbers) {
        int addScore = 0;
        for (int i = 0; i < mColumnNum; ++i) {
            int[] indexes = new int[mColumnNum];
            int[] oriNumbers = new int[mColumnNum];
            for (int j = 0; j < mColumnNum; ++j) {
                int index = getIndex(action, i, j);
                if (index < 0 || index >= mItems.length) {
                    return 0;
                }
                indexes[j] = index;
                oriNumbers[j] = mItems[index].getNumber();
            }

            int[] newNumbers = new int[mColumnNum];
            int next = 0;
            boolean canMerge = true;
            for (int num: oriNumbers) {
                if (0 != num) {
                    if (0 == next || num != newNumbers[next - 1] || !canMerge) {
                        newNumbers[next] = num;
                        ++next;
                        canMerge = true;
                    }
                    else {
                        newNumbers[next - 1] <<= 1;
                        canMerge = false;

                        addScore += newNumbers[next - 1];
                    }
                }
            }
            for (; next < mColumnNum; ++next) {
                newNumbers[next] = 0;
            }

            for (int j = 0; j < mColumnNum; ++j) {
                outNumbers[indexes[j]] = newNumbers[j];
            }
        }

        return addScore;
    }

    private int getIndex(ACTION action, int i, int j) {
        int index = -1;
        switch (action) {
            case UP:
                index = j * mColumnNum + i;
                break;
            case DOWN:
                index = (mColumnNum - 1 - j) * mColumnNum + i;
                break;
            case LEFT:
                index = i * mColumnNum + j;
                break;
            case RIGHT:
                index = (i + 1) * mColumnNum - (j + 1);
                break;
            default:
                break;
        }
        return index;
    }

    /*
     * 获得空的Item的个数
     */
    private int getEmptyItemCount() {
        int emptyItemCount = 0;
        for (Game2048Item item: mItems) {
            if (0 == item.getNumber()) {
                ++emptyItemCount;
            }
        }
        return emptyItemCount;
    }

    /*
     * 获得第N个空的Item
     */
    private Game2048Item getEmptyItemAt(int index) {
        for (Game2048Item item: mItems) {
            if (0 == item.getNumber()) {
                if (0 == index) {
                    return item;
                }
                --index;
            }
        }
        return null;
    }

    /*
     * 在随机的空位上产生随机数（2 Or 4）
     */
    private void generateNum() {
        int emptyItemCount = getEmptyItemCount();
        if (0 == emptyItemCount) {
            return;
        }
        Random random = new Random();
        int index = random.nextInt(emptyItemCount);
        Game2048Item item = getEmptyItemAt(index);
        if (null == item) {
            return;
        }
        item.setNumber(random.nextDouble() < 0.875 ? 2 : 4);
    }

    /*
     * 检查游戏是否结束
     */
    private boolean isGameOver() {
        if (getEmptyItemCount() > 0) {
            return false;
        }

        for (int i = 0; i < mItems.length; ++i) {
            if (i % mColumnNum != 0 && mItems[i].getNumber() == mItems[i - 1].getNumber()
                    || i >= mColumnNum && mItems[i].getNumber() == mItems[i - mColumnNum].getNumber()) {
                return false;
            }
        }

        return true;
    }

    /*
     * 重新开始游戏
     */
    public void restartGame() {
        for (Game2048Item item: mItems) {
            item.setNumber(0);
        }
        for (int i = 0; i < 2; ++i) {
            generateNum();
        }
        mScore = 0;
        showScore();
    }

    private void showScore() {
        if (null != mOnGame2048LayoutListener) {
            mOnGame2048LayoutListener.onScoreChanged(mScore);
        }
    }

    private void performGameOver() {
        if (null != mOnGame2048LayoutListener) {
            mOnGame2048LayoutListener.onGameOver();
        }
    }

    public interface OnGame2048LayoutListener {
        void onScoreChanged(int score);
        void onGameOver();
    }

    public void setOnGame2048LayoutListener(OnGame2048LayoutListener onGame2048LayoutListener) {
        mOnGame2048LayoutListener = onGame2048LayoutListener;
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        final int FLING_MIN_DISTANCE = 50;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float deltaX = e2.getX() - e1.getX();
            float deltaY = e2.getY() - e1.getY();

            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                if (deltaX > FLING_MIN_DISTANCE) {
                    action(ACTION.RIGHT);
                }
                else if (deltaX < -FLING_MIN_DISTANCE) {
                    action(ACTION.LEFT);
                }
            }
            else {
                if (deltaY > FLING_MIN_DISTANCE) {
                    action(ACTION.DOWN);
                }
                else if (deltaY < -FLING_MIN_DISTANCE) {
                    action(ACTION.UP);
                }
            }

            return true;
        }
    }
}
