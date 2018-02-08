package zhangman.github.snake;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by zhangman on 2018/1/10 20:40.
 * Email: zhangman523@126.com
 */

public interface GameType {
  int GRID = 0;
  int FOOD = 1;
  int SNAKE = 2;

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({ GRID, FOOD, SNAKE })
  @interface SquareType {

  }

  int LEFT = 1;
  int TOP = 2;
  int RIGHT = 3;
  int BOTTOM = 4;

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({ LEFT, TOP, RIGHT, BOTTOM })
  @interface DirectionType {

  }
}
