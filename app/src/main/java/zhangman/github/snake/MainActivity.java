package zhangman.github.snake;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private SnakePanelView mSnakePanelView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mSnakePanelView = findViewById(R.id.snake_view);

    findViewById(R.id.left_btn).setOnClickListener(this);
    findViewById(R.id.right_btn).setOnClickListener(this);
    findViewById(R.id.top_btn).setOnClickListener(this);
    findViewById(R.id.bottom_btn).setOnClickListener(this);
    findViewById(R.id.start_btn).setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.left_btn:
        mSnakePanelView.setSnakeDirection(GameType.LEFT);
        break;
      case R.id.right_btn:
        mSnakePanelView.setSnakeDirection(GameType.RIGHT);
        break;
      case R.id.top_btn:
        mSnakePanelView.setSnakeDirection(GameType.TOP);
        break;
      case R.id.bottom_btn:
        mSnakePanelView.setSnakeDirection(GameType.BOTTOM);
        break;
      case R.id.start_btn:
        mSnakePanelView.reStartGame();
        break;
    }
  }
}
