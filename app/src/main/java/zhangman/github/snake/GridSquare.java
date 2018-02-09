package zhangman.github.snake;

import android.graphics.Color;

/**
 * Created by zhangman on 2018/1/8 14:43
 * Email: zhangman523@126.com
 */

public class GridSquare {
  private int mType;//元素类型

  public GridSquare(int type) {
    mType = type;
  }

  public int getColor() {
    switch (mType) {
      case GameType.GRID://空格子
        return Color.WHITE;
      case GameType.FOOD://食物
        return Color.BLUE;
      case GameType.SNAKE://蛇
        return Color.parseColor("#FF4081");
    }
    return Color.WHITE;
  }

  public void setType(int type) {
    mType = type;
  }
}
