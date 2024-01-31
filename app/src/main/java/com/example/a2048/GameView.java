package com.example.a2048;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.GridLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends GridLayout {
    private Card[][] Cards;
    private final Random random = new Random();
    protected int gridSize = 4, cardWidth, addRandomCardNum = 1;
    private List<Animator> newAnimatorList = new ArrayList<>();
    private int moveTime = getResources().getInteger(R.integer.moveTime), mergeTime = getResources().getInteger(R.integer.mergeTime), createTime = getResources().getInteger(R.integer.createTime);
    private int gameScore = 0, moveNum = 0;
    private GameActivity gameActivity;
    private SoundPool soundPool;
    private int sound_merge, sound_create, sound_warn;
    private int maxMergeNum;
    public boolean isInGame = true;
    private boolean isTrainingMode = false;
    private LimitedSizeStack<GameData> stack_CardsNum = new LimitedSizeStack<>();

    public void setGameActivity(GameActivity gameActivity) {
        this.gameActivity = gameActivity;
    }

    public GameView(Context context) {
        super(context);
        Cards = new Card[gridSize][gridSize];
        initGame(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Cards = new Card[gridSize][gridSize];
        initGame(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Cards = new Card[gridSize][gridSize];
        initGame(context);
    }

    public void initGame(Context context) {
        try {
            gameActivity = (GameActivity) context;
        } catch (Exception e) {
            isInGame = false;
        }
        // 设置列数
        setColumnCount(gridSize);
        // 初始化所有方块
        View view = this;
        final int[] width = new int[1];
        width[0] = 0;
        final ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (width[0] == 0) {
                    width[0] = view.getWidth();
                    if (viewTreeObserver.isAlive()) {
                        viewTreeObserver.removeOnGlobalLayoutListener(this);
                    }
                    cardWidth = width[0] / gridSize;
                    // Log.d("cardWidth", "cardWidth: " + cardWidth);
                    addCards(cardWidth, cardWidth);
                    if (isInGame)
                        addRandomCard(addRandomCardNum + 1);
                }
            }
        });

        if (isInGame) {
            // 设置滑动触发器
            setListener();
            // 初始化音乐音效
            AudioAttributes attr = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME) // 设置音效使用场景
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build(); // 设置音效的类型
            soundPool = new SoundPool.Builder().setAudioAttributes(attr) // 设置音效池的属性
                    .setMaxStreams(10) // 设置最多可容纳10个音频流
                    .build();
            sound_merge = soundPool.load(context, R.raw.merge, 1);
            sound_create = soundPool.load(context, R.raw.create, 1);
            sound_warn = soundPool.load(context, R.raw.warn, 1);
        }
    }

    public void resetGame(int gridSize) {
        this.gridSize = gridSize;
        setColumnCount(gridSize);
        Cards = new Card[gridSize][gridSize];
        removeAllViews(); //清除GridLayout原有元素
        // 初始化所有方块
        View view = this;
        int width = view.getWidth();
        cardWidth = width / gridSize;
        // Log.d("cardWidth", "cardWidth: " + cardWidth);
        addCards(cardWidth, cardWidth);
    }

    public void restartGame() {
        saveCurrentState();
        for (int i = 0; i < gridSize; ++i) {
            for (int j = 0; j < gridSize; ++j) {
                Cards[i][j].setNum(0);
            }
        }
        setGameScore(0);
        setMoveNum(0);
        addRandomCard(addRandomCardNum + 1);
        setOutTrainingMode();
    }

    private void addCards(int width, int height) {
        Card c;
        for (int i = 0; i < gridSize; ++i) {
            for (int j = 0; j < gridSize; ++j) {
                c = new Card(getContext(), width, isInGame);
                addView(c, width, height);
                Cards[i][j] = c;
                if (Cards[i][j] != null) {
                    Log.d("Cards", "Cards已经初始化: " + i + " " + j);
                }
            }
        }
    }

    private void addCards(int width, int height, int[][] cardsNum) {
        Card c;
        for (int i = 0; i < gridSize; ++i) {
            for (int j = 0; j < gridSize; ++j) {
                c = new Card(getContext(), width, isInGame);
                addView(c, width, height);
                Cards[i][j] = c;
            }
        }
        setCardsNum(cardsNum);
    }

    private void setListener() {
        setOnTouchListener(new OnTouchListener() {
            private float staX, staY, endX, endY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        staX = event.getX();
                        staY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        endX = event.getX();
                        endY = event.getY();
                        int sensitiveDistance = 50;
                        Direction dir = null;
                        if (Math.abs(endX - staX) > Math.abs(endY - staY)) {
                            if (endX - staX > sensitiveDistance) {
                                dir = Direction.RIGHT;
                                Log.d("move", "R");
                            } else if (endX - staX < -sensitiveDistance) {
                                dir = Direction.LEFT;
                                Log.d("move", "L");
                            }
                        } else {
                            if (endY - staY < -sensitiveDistance) {
                                dir = Direction.UP;
                                Log.d("move", "U");
                            } else if (endY - staY > sensitiveDistance) {
                                dir = Direction.DOWN;
                                Log.d("move", "D");
                            }
                        }
                        if (dir != null && moveCards(dir)) {
                            // 如果有动画需要执行
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.playTogether(newAnimatorList);
                            // 设置动画监听器
                            animatorSet.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {
                                    // 动画开始时的逻辑
                                    saveCurrentState(); // 为撤回保存数据
                                    if (maxMergeNum > 0)
                                        playSound(sound_merge, (float) Math.sqrt(Math.log(maxMergeNum) / Math.log(2) / 11));
                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    // 所有动画结束后的逻辑
                                    // 执行后续代码
                                    addMoveNum();
                                    playSound(sound_create, 1);
                                    addRandomCard(addRandomCardNum);
                                    if (isGameOver()) {
                                        gameOver();
                                    }
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {
                                    // 动画取消时的逻辑
                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {
                                    // 动画重复时的逻辑
                                }
                            });
                            // 启动动画
                            animatorSet.start();
                            newAnimatorList.clear();
                        } else if (dir != null) {
                            playSound(sound_warn, 2);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private boolean moveCards(Direction direction) {
        int[][] CardsNum = getCardsNum();
        maxMergeNum = 0;
        switch (direction) {
            case UP:
                return moveCardsUp(CardsNum);
            case DOWN:
                return moveCardsDown(CardsNum);
            case LEFT:
                return moveCardsLeft(CardsNum);
            case RIGHT:
                return moveCardsRight(CardsNum);
            default:
                return false;
        }
    }

    private boolean moveCardsUp(int[][] CardsNum) {
        boolean swiped = false;
        for (int col = 0; col < gridSize; col++) {
            int currentRow = 0;
            int mergeRow = -1;
            boolean lock = false;
            while (currentRow < gridSize && currentRow > -1) {
                if (CardsNum[currentRow][col] != 0) {
                    if (mergeRow != -1 && CardsNum[currentRow][col] == CardsNum[mergeRow][col] && !lock) {
                        swiped = true;
                        maxMergeNum = Math.max(maxMergeNum, CardsNum[mergeRow][col]);
                        lock = true;
                        CardsNum[mergeRow][col] = CardsNum[currentRow][col] * 2;
                        CardsNum[currentRow][col] = 0;
                        newAnimatorList.add(playMergeAnimation(currentRow, col, mergeRow, col));
                    } else {
                        mergeRow++;
                        lock = false;
                        if (currentRow != mergeRow) {
                            swiped = true;
                            CardsNum[mergeRow][col] = CardsNum[currentRow][col];
                            CardsNum[currentRow][col] = 0;
                            newAnimatorList.add(playMoveAnimation(currentRow, col, mergeRow, col));
                        }
                    }
                }
                currentRow++;
            }
        }
        return swiped;
    }

    private boolean moveCardsDown(int[][] CardsNum) {
        boolean swiped = false;
        for (int col = 0; col < gridSize; col++) {
            int currentRow = gridSize - 1;
            int mergeRow = gridSize;
            boolean lock = false;
            while (currentRow < gridSize && currentRow > -1) {
                if (CardsNum[currentRow][col] != 0) {
                    if (mergeRow != gridSize && CardsNum[currentRow][col] == CardsNum[mergeRow][col] && !lock) {
                        swiped = true;
                        maxMergeNum = Math.max(maxMergeNum, CardsNum[mergeRow][col]);
                        lock = true;
                        CardsNum[mergeRow][col] = CardsNum[currentRow][col] * 2;
                        CardsNum[currentRow][col] = 0;
                        newAnimatorList.add(playMergeAnimation(currentRow, col, mergeRow, col));
                    } else {
                        mergeRow--;
                        lock = false;
                        if (currentRow != mergeRow) {
                            swiped = true;
                            CardsNum[mergeRow][col] = CardsNum[currentRow][col];
                            CardsNum[currentRow][col] = 0;
                            newAnimatorList.add(playMoveAnimation(currentRow, col, mergeRow, col));
                        }
                    }
                }
                currentRow--;
            }
        }
        return swiped;
    }

    private boolean moveCardsLeft(int[][] CardsNum) {
        boolean swiped = false;
        for (int Row = 0; Row < gridSize; Row++) {
            int currentCol = 0;
            int mergeCol = -1;
            boolean lock = false;
            while (currentCol < gridSize && currentCol > -1) {
                if (CardsNum[Row][currentCol] != 0) {
                    if (mergeCol != -1 && CardsNum[Row][currentCol] == CardsNum[Row][mergeCol] && !lock) {
                        swiped = true;
                        maxMergeNum = Math.max(maxMergeNum, CardsNum[Row][currentCol]);
                        lock = true;
                        CardsNum[Row][mergeCol] = CardsNum[Row][currentCol] * 2;
                        CardsNum[Row][currentCol] = 0;
                        newAnimatorList.add(playMergeAnimation(Row, currentCol, Row, mergeCol));
                    } else {
                        mergeCol++;
                        lock = false;
                        if (currentCol != mergeCol) {
                            swiped = true;
                            CardsNum[Row][mergeCol] = CardsNum[Row][currentCol];
                            CardsNum[Row][currentCol] = 0;
                            newAnimatorList.add(playMoveAnimation(Row, currentCol, Row, mergeCol));
                        }
                    }
                }
                currentCol++;
            }
        }
        return swiped;
    }

    private boolean moveCardsRight(int[][] CardsNum) {
        boolean swiped = false;
        for (int Row = 0; Row < gridSize; Row++) {
            int currentCol = gridSize - 1;
            int mergeCol = gridSize;
            boolean lock = false;
            while (currentCol < gridSize && currentCol > -1) {
                if (CardsNum[Row][currentCol] != 0) {
                    if (mergeCol != gridSize && CardsNum[Row][currentCol] == CardsNum[Row][mergeCol] && !lock) {
                        swiped = true;
                        maxMergeNum = Math.max(maxMergeNum, CardsNum[Row][currentCol]);
                        lock = true;
                        CardsNum[Row][mergeCol] = CardsNum[Row][currentCol] * 2;
                        CardsNum[Row][currentCol] = 0;
                        newAnimatorList.add(playMergeAnimation(Row, currentCol, Row, mergeCol));
                    } else {
                        mergeCol--;
                        lock = false;
                        if (currentCol != mergeCol) {
                            swiped = true;
                            CardsNum[Row][mergeCol] = CardsNum[Row][currentCol];
                            CardsNum[Row][currentCol] = 0;
                            newAnimatorList.add(playMoveAnimation(Row, currentCol, Row, mergeCol));
                        }
                    }
                }
                currentCol--;
            }
        }
        return swiped;
    }

    private void addRandomCard() {
        List<Integer> emptyPositions = new ArrayList<>();
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (Cards[i][j].getNum() == 0) {
                    emptyPositions.add(i * gridSize + j);
                }
            }
        }
        if (emptyPositions.size() > 0) {
            int pos = emptyPositions.get(random.nextInt(emptyPositions.size()));
            int row = pos / gridSize;
            int col = pos % gridSize;
            int val = random.nextInt(10) == 0 ? 4 : 2;
            Cards[row][col].setNum(val);
            playCreateAnimation(row, col);
        }
    }

    private void addRandomCard(int num) {
        while ((num--) != 0) {
            addRandomCard();
        }
    }

    public void setAddRandomCardNum(int num) {
        addRandomCardNum = num;
    }

    private void playCreateAnimation(int r, int c) {
        AnimationSet animationSet = new AnimationSet(true);

        //旋转
        RotateAnimation anim = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(createTime / 2);
        anim.setRepeatCount(0);
        anim.setInterpolator(new LinearInterpolator());

        //缩放
        ScaleAnimation anim2 = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        anim2.setDuration(createTime / 2);
        anim2.setRepeatCount(0);

        animationSet.addAnimation(anim);
        animationSet.addAnimation(anim2);

        Cards[r][c].startAnimation(animationSet);
    }

    public void playSound(int soundConstant, float LR_balance, double weight) {
        soundPool.play(soundConstant, (float) Math.sqrt((1 - LR_balance) * weight / 11), (float) Math.sqrt(LR_balance * weight / 11), (int) weight, 0, 1);
    }

    public void playSound(int soundConstant, float weight) {
        soundPool.play(soundConstant, 0.6F * weight, 0.6F * weight, 0, 0, 1);
    }

    private void playMergeAnimation(int r, int c) {
        ScaleAnimation anim = new ScaleAnimation(1, 1.2f, 1, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        anim.setDuration(mergeTime);
        anim.setRepeatCount(0);

        anim.setRepeatMode(Animation.REVERSE);
        Cards[r][c].startAnimation(anim);
    }

    private ObjectAnimator playMergeAnimation(int startX, int startY, int endX, int endY) {
        float startTranslationX = 0;
        float startTranslationY = 0;
        float endTranslationX = (endY - startY) * cardWidth;
        float endTranslationY = (endX - startX) * cardWidth;

        final View cardView = Cards[startX][startY]; // 保存卡片视图的引用

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(cardView,
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, startTranslationX, endTranslationX),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, startTranslationY, endTranslationY));

        animator.setDuration(moveTime);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Animation end logic here
                Cards[endX][endY].setNum(Cards[startX][startY].getNum() * 2);
                Cards[startX][startY].setNum(0);

                addGameScore(Cards[endX][endY].getNum());

                cardView.setTranslationX(0);
                cardView.setTranslationY(0);

                playMergeAnimation(endX, endY);
            }
        });

        //animator.start();
        return animator;
    }

    private ObjectAnimator playMoveAnimation(int startX, int startY, int endX, int endY) {
        View cardView = Cards[startX][startY];
        float startXPosition = 0;
        float startYPosition = 0;
        float endXPosition = (endY - startY) * cardWidth;
        float endYPosition = (endX - startX) * cardWidth;

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(cardView,
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, startXPosition, endXPosition),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, startYPosition, endYPosition));

        animator.setDuration(moveTime);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Animation end logic here
                Cards[endX][endY].setNum(Cards[startX][startY].getNum());
                Cards[startX][startY].setNum(0);

                cardView.setTranslationX(0);
                cardView.setTranslationY(0);
            }
        });

        //animator.start();
        return animator;
    }

    private boolean isGameOver() {
        // 游戏结束的条件：没有空卡片且没有相邻的卡片具有相同的值
        return !hasEmptyCards() && !hasAdjacentCardsWithSameValue() && isInGame;
    }

    private boolean hasEmptyCards() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (Cards[i][j].getNum() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasAdjacentCardsWithSameValue() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (hasAdjacentCardWithSameValue(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasAdjacentCardWithSameValue(int row, int col) {
        int CardValue = Cards[row][col].getNum();
        if (row < gridSize - 1 && Cards[row + 1][col].getNum() == CardValue) {
            return true;
        }
        return col < gridSize - 1 && Cards[row][col + 1].getNum() == CardValue;
    }

    private void gameOver() {
        Toast.makeText(getContext(), "游戏结束", Toast.LENGTH_SHORT).show();
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private void addGameScore(int score) {
        gameScore += score;
        if (isInGame)
            gameActivity.updateScoreView();
    }

    public void setGameScore(int score) {
        gameScore = score;
        if (isInGame)
            gameActivity.updateScoreView();
    }

    public int getGameScore() {
        return gameScore;
    }

    private void addMoveNum() {
        moveNum++;
        if (isInGame)
            gameActivity.updateMoveView();
    }

    public void setMoveNum(int num) {
        moveNum = num;
        if (isInGame)
            gameActivity.updateMoveView();
    }

    public int getMoveNum() {
        return moveNum;
    }

    public void updatePreviousStatusNum() {
        if (isInGame)
            gameActivity.updatePreviousStatusNum(stack_CardsNum.size());
    }

    public int[][] getCardsNum() {
        int[][] CardsNum = new int[gridSize][gridSize];
        for (int col = 0; col < gridSize; col++)
            for (int row = 0; row < gridSize; row++)
                CardsNum[col][row] = Cards[col][row].getNum();
        return CardsNum;
    }

    private void setCardsNum(int[][] cardsNum) {
        for (int i = 0; i < gridSize; ++i) {
            for (int j = 0; j < gridSize; ++j) {
                try {
                    if (Cards[i][j] == null) {
                        // 对象尚未初始化，执行初始化操作
                        Toast.makeText(getContext(), "Cards未初始化！", Toast.LENGTH_SHORT).show();
                        Log.e("Cards", "Cards未初始化: " + i + " " + j);
                    }
                    Cards[i][j].setNum(cardsNum[i][j]);
                } catch (Exception e) {
                    Log.e("setNum", "调用setNum()时捕获到异常：" + e.getMessage());
                }
            }
        }
        if (isGameOver()) {
            gameOver();
        } else {
            setAsTrainingMode();
        }
    }

    public void setGameData(int gridSize, int[][] cardsNum, int gameScore, int moveNum) {
        this.gridSize = gridSize;
        setGameScore(gameScore);
        setMoveNum(moveNum);

        // 初始化组件
        setColumnCount(gridSize);

        // 初始化所有方块
        cardWidth = this.getWidth() / gridSize;
        addCards(cardWidth, cardWidth, cardsNum);
    }

    public void setGameData(int[][] cardsNum, int gameScore, int moveNum) {
        setCardsNum(cardsNum);
        setGameScore(gameScore);
        setMoveNum(moveNum);
    }

    public void setGameData(GameData gameData) {
        int[][] cardsNum = gameData.getArray();
        gameScore = gameData.getGameScore();
        moveNum = gameData.getMoveNum();
        if (gameData.getGridSize() > 0 && gameData.getGridSize() != gridSize) {
            gridSize = gameData.getGridSize();
            resetGame(gridSize);
        }
        setGameData(cardsNum, gameScore, moveNum);
    }

    public GameData getGameData() {
        int[][] array = getCardsNum();
        GameData data = new GameData(array, gameScore, moveNum, gridSize);
        return data;
    }

    private void saveCurrentState() {
        // 得到Data对象并将其压入栈中
        GameData data = getGameData();
        stack_CardsNum.push(data);
        updatePreviousStatusNum();
    }

    public float getCardCornerRadius(){
        try {
            Toast.makeText(getContext(), "getCardCornerRadius返回："+Cards[0][0].getCornerRadius(), Toast.LENGTH_SHORT).show();
            return Cards[0][0].getCornerRadius();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(), "getCardCornerRadius错误！", Toast.LENGTH_SHORT).show();
        }
        return 0;
    }

    public void undoToPreviousStatus() {
        if (!stack_CardsNum.isEmpty()) {
            // 从栈中取出上一个状态的数据对象
            GameData previousData = stack_CardsNum.pop();
            updatePreviousStatusNum();
            setGameData(previousData);
            setAsTrainingMode();
            updatePreviousStatusNum();
        } else {
            playSound(sound_warn, 3);
        }
    }

    public void setAsTrainingMode() {
        if (!isTrainingMode && isInGame) {
            isTrainingMode = true;
            gameActivity.exchangeMode(true);
            Toast.makeText(getContext(), "已切换为训练模式", Toast.LENGTH_SHORT).show();
        }
    }

    public void setOutTrainingMode() {
        if (isTrainingMode && isInGame) {
            isTrainingMode = false;
            gameActivity.exchangeMode(false);
            Toast.makeText(getContext(), "已切换为常规模式", Toast.LENGTH_SHORT).show();
        }
    }


}