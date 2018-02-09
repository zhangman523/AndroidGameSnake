package zhangman.github.snake;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by zhangman on 2018/1/8 14:43.
 * Email: zhangman523@126.com
 */

public class SnakePanelView extends View {
  private final static String TAG = SnakePanelView.class.getSimpleName();
  public static boolean DEBUG = true;

  private List<List<GridSquare>> mGridSquare = new ArrayList<>();
  private List<GridPosition> mSnakePositions = new ArrayList<>();

  private GridPosition mSnakeHeader;//蛇头部位置
  private GridPosition mFoodPosition;//食物的位置
  private int mSnakeLength = 3;
  private long mSpeed = 8;
  private int mSnakeDirection = GameType.RIGHT;
  private boolean mIsEndGame = false;
  private int mGridSize = 20;
  private Paint mGridPaint = new Paint();
  private Paint mStrokePaint = new Paint();
  private int mRectSize = dp2px(getContext(), 15);
  private int mStartX, mStartY;

  public SnakePanelView(Context context) {
    this(context, null);
  }

  public SnakePanelView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SnakePanelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    List<GridSquare> squares;
    for (int i = 0; i < mGridSize; i++) {
      squares = new ArrayList<>();
      for (int j = 0; j < mGridSize; j++) {
        squares.add(new GridSquare(GameType.GRID));
      }
      mGridSquare.add(squares);
    }
    mSnakeHeader = new GridPosition(10, 10);
    mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
    mFoodPosition = new GridPosition(0, 0);
    mIsEndGame = true;
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mStartX = w / 2 - mGridSize * mRectSize / 2;
    mStartY = dp2px(getContext(), 40);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int height = mStartY * 2 + mGridSize * mRectSize;
    setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), height);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawColor(Color.WHITE);
    //格子画笔
    mGridPaint.reset();
    mGridPaint.setAntiAlias(true);
    mGridPaint.setStyle(Paint.Style.FILL);
    mGridPaint.setAntiAlias(true);

    mStrokePaint.reset();
    mStrokePaint.setColor(Color.BLACK);
    mStrokePaint.setStyle(Paint.Style.STROKE);
    mStrokePaint.setAntiAlias(true);

    for (int i = 0; i < mGridSize; i++) {
      for (int j = 0; j < mGridSize; j++) {
        int left = mStartX + i * mRectSize;
        int top = mStartY + j * mRectSize;
        int right = left + mRectSize;
        int bottom = top + mRectSize;
        canvas.drawRect(left, top, right, bottom, mStrokePaint);
        mGridPaint.setColor(mGridSquare.get(i).get(j).getColor());
        canvas.drawRect(left, top, right, bottom, mGridPaint);
      }
    }
  }

  private void refreshFood(GridPosition foodPosition) {
    mGridSquare.get(foodPosition.getX()).get(foodPosition.getY()).setType(GameType.FOOD);
  }

  public void setSpeed(long speed) {
    mSpeed = speed;
  }

  public void setGridSize(int gridSize) {
    mGridSize = gridSize;
  }

  public void setSnakeDirection(int snakeDirection) {
    if (mSnakeDirection == GameType.RIGHT && snakeDirection == GameType.LEFT) return;
    if (mSnakeDirection == GameType.LEFT && snakeDirection == GameType.RIGHT) return;
    if (mSnakeDirection == GameType.TOP && snakeDirection == GameType.BOTTOM) return;
    if (mSnakeDirection == GameType.BOTTOM && snakeDirection == GameType.TOP) return;
    mSnakeDirection = snakeDirection;
  }

  private class GameMainThread extends Thread {

    @Override
    public void run() {
      while (!mIsEndGame) {
        moveSnake(mSnakeDirection);
        checkCollision();
        refreshGridSquare();
        handleSnakeTail();
        postInvalidate();//重绘界面
        handleSpeed();
      }
    }

    private void handleSpeed() {
      try {
        sleep(1000 / mSpeed);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  //检测碰撞
  private void checkCollision() {
    //检测是否咬到自己
    GridPosition headerPosition = mSnakePositions.get(mSnakePositions.size() - 1);
    for (int i = 0; i < mSnakePositions.size() - 2; i++) {
      GridPosition position = mSnakePositions.get(i);
      if (headerPosition.getX() == position.getX() && headerPosition.getY() == position.getY()) {
        //咬到自己 停止游戏
        mIsEndGame = true;
        showMessageDialog();
        return;
      }
    }

    //判断是否吃到食物
    if (headerPosition.getX() == mFoodPosition.getX()
        && headerPosition.getY() == mFoodPosition.getY()) {
      mSnakeLength++;
      generateFood();
    }
  }

  private void showMessageDialog() {
    post(new Runnable() {
      @Override
      public void run() {
        new AlertDialog.Builder(getContext()).setMessage("Game " + "Over!")
            .setCancelable(false)
            .setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                reStartGame();
              }
            })
            .setNegativeButton("退出", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
              }
            })
            .create()
            .show();
      }
    });
  }

  public void reStartGame() {
    if (!mIsEndGame) return;
    for (List<GridSquare> squares : mGridSquare) {
      for (GridSquare square : squares) {
        square.setType(GameType.GRID);
      }
    }
    if (mSnakeHeader != null) {
      mSnakeHeader.setX(10);
      mSnakeHeader.setY(10);
    } else {
      mSnakeHeader = new GridPosition(10, 10);//蛇的初始位置
    }
    mSnakePositions.clear();
    mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
    mSnakeLength = 3;//蛇的长度
    mSnakeDirection = GameType.RIGHT;
    mSpeed = 8;//速度
    if (mFoodPosition != null) {
      mFoodPosition.setX(0);
      mFoodPosition.setY(0);
    } else {
      mFoodPosition = new GridPosition(0, 0);
    }
    refreshFood(mFoodPosition);
    mIsEndGame = false;
    GameMainThread thread = new GameMainThread();
    thread.start();
  }

  //生成food
  private void generateFood() {
    Random random = new Random();
    int foodX = random.nextInt(mGridSize - 1);
    int foodY = random.nextInt(mGridSize - 1);
    for (int i = 0; i < mSnakePositions.size() - 1; i++) {
      if (foodX == mSnakePositions.get(i).getX() && foodY == mSnakePositions.get(i).getY()) {
        //不能生成在蛇身上
        foodX = random.nextInt(mGridSize - 1);
        foodY = random.nextInt(mGridSize - 1);
        //重新循环
        i = 0;
      }
    }
    mFoodPosition.setX(foodX);
    mFoodPosition.setY(foodY);
    refreshFood(mFoodPosition);
  }

  private void moveSnake(int snakeDirection) {
    switch (snakeDirection) {
      case GameType.LEFT:
        if (mSnakeHeader.getX() - 1 < 0) {//边界判断：如果到了最左边 让他穿过屏幕到最右边
          mSnakeHeader.setX(mGridSize - 1);
        } else {
          mSnakeHeader.setX(mSnakeHeader.getX() - 1);
        }
        mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
        break;
      case GameType.TOP:
        if (mSnakeHeader.getY() - 1 < 0) {
          mSnakeHeader.setY(mGridSize - 1);
        } else {
          mSnakeHeader.setY(mSnakeHeader.getY() - 1);
        }
        mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
        break;
      case GameType.RIGHT:
        if (mSnakeHeader.getX() + 1 >= mGridSize) {
          mSnakeHeader.setX(0);
        } else {
          mSnakeHeader.setX(mSnakeHeader.getX() + 1);
        }
        mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
        break;
      case GameType.BOTTOM:
        if (mSnakeHeader.getY() + 1 >= mGridSize) {
          mSnakeHeader.setY(0);
        } else {
          mSnakeHeader.setY(mSnakeHeader.getY() + 1);
        }
        mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
        break;
    }
  }

  private void refreshGridSquare() {
    for (GridPosition position : mSnakePositions) {
      mGridSquare.get(position.getX()).get(position.getY()).setType(GameType.SNAKE);
    }
  }

  private void handleSnakeTail() {
    int snakeLength = mSnakeLength;
    for (int i = mSnakePositions.size() - 1; i >= 0; i--) {
      if (snakeLength > 0) {
        snakeLength--;
      } else {//将超过长度的格子 置为 GameType.GRID
        GridPosition position = mSnakePositions.get(i);
        mGridSquare.get(position.getX()).get(position.getY()).setType(GameType.GRID);
      }
    }
    snakeLength = mSnakeLength;
    for (int i = mSnakePositions.size() - 1; i >= 0; i--) {
      if (snakeLength > 0) {
        snakeLength--;
      } else {
        mSnakePositions.remove(i);
      }
    }
  }

  /**
   * dp转px
   */
  public static int dp2px(Context context, float dpVal) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
        context.getResources().getDisplayMetrics());
  }
}
