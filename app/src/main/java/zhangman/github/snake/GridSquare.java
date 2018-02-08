package zhangman.github.snake;

import android.graphics.Color;

/**
 * Created by zhangman on 2018/1/8 14:43
 * Email: zhangman523@126.com
 */

public class GridSquare {
  private int mType;

  public GridSquare(int type) {
    mType = type;
  }

  public int getColor() {
    switch (mType) {
      case GameType.GRID:
        return Color.WHITE;
      case GameType.FOOD:
        return Color.BLUE;
      case GameType.SNAKE:
        return Color.parseColor("#FF4081");
    }
    return Color.WHITE;
  }

  public void setType(int type) {
    mType = type;
  }
}
