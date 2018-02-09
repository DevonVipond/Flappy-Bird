package com.devon.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.Random;

import sun.rmi.runtime.Log;

public class FlappyBird extends ApplicationAdapter {
    SpriteBatch batch;
    Texture gameover;
    Texture restart;
    Texture getReady;
    Texture background;
    ShapeRenderer shapeRenderer;
    Texture[] edwin1;
    int flapState = 0;
    float birdY = 0;
    float velocity = 0;
    Circle birdCircle;
    Rectangle[] botRect;
    Rectangle[] topRect;

    int gameState = 0;
    float gravity = 2.2f;

    Texture toptube;
    Texture bottomtube;
    float gap = 500;
    float maxTubeOffset;
    Random randomGenerator;
    float tubeVelocity = 4;
    int numberOfTubes = 4;
    float distanceBetweenTubes;
    float[] tubeX = new float[numberOfTubes];
    float[] tubeOffset = new float[numberOfTubes];
    int score = 0;
    int scoringTube = 0;
    BitmapFont font;
    BitmapFont smallerFont;

    int highScore = 0;
    int countflap = 0;



    @Override
    public void create() {

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(15);
        smallerFont = new BitmapFont();
        smallerFont.getData().setScale(5);
        shapeRenderer = new ShapeRenderer();
        gameover = new Texture("gameover.png");
        restart = new Texture("restarttwo.png");
        getReady = new Texture("getready.png");

        birdCircle = new Circle();
        botRect = new Rectangle[numberOfTubes];
        topRect = new Rectangle[numberOfTubes];

        background = new Texture("bg.png");
        edwin1 = new Texture[2];
        edwin1[0] = new Texture("bird.png");
        edwin1[1] = new Texture("bird2.png");
        toptube = new Texture("toptube.png");
        bottomtube = new Texture("bottomtube.png");
        manageHighScore(-1);
        startGame();
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        if (gameState == 2 && Gdx.input.justTouched()) {
            velocity = 0;
            gameState = 0;
            startGame();
        }
        flapState = flip(flapState);
        if (gameState == 1) {
            checkForScoringTube();
            if (Gdx.input.justTouched()) {
                velocity = -background.getHeight() / 14; // raises bird
            }
            for (int i = 0; i < numberOfTubes; i++) {
                if (tubeX[i] < -toptube.getWidth()) {

                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
                } else {
                    tubeX[i] = tubeX[i] - tubeVelocity;


                }
                batch.begin();
                batch.draw(toptube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
                batch.draw(bottomtube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() / 2 - 800 + tubeOffset[i]);
                batch.end();

                topRect[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], toptube.getWidth(), toptube.getHeight());
                botRect[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() / 2 - 800 + tubeOffset[i], bottomtube.getWidth(), bottomtube.getHeight());

            }
            if (birdY > 0) {
                velocity += gravity;
                birdY -= velocity;
            } else
                gameState = 2;      //--------------------------------------------

        } else if (gameState == 0) {

            batch.begin();
            batch.draw(getReady, Gdx.graphics.getWidth() / 2 - getReady.getWidth() / 2, Gdx.graphics.getHeight() * 3/4 - getReady.getHeight() / 2);
            batch.end();


            if (Gdx.input.justTouched()) {
                flapState = flip(flapState);
                gameState = 1;
            }
        }

        if (gameState != 2) {
            batch.begin();
            batch.draw(edwin1[flapState], Gdx.graphics.getWidth() / 2 - edwin1[flapState].getWidth() / 2, birdY);
            font.setColor(Color.GRAY);
            font.draw(batch, String.valueOf(score), 300, 300);
            smallerFont.setColor(Color.GRAY);
            smallerFont.draw(batch, "High Score: " + highScore, 50, Gdx.graphics.getHeight() - 50);
            batch.end();
            birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + edwin1[flapState].getHeight() / 2, edwin1[1].getWidth() / 2);
            countflap++;


        } else {
            gameOver();
        }


        for (int i = 0; i < numberOfTubes; i++) {
            if (Intersector.overlaps(birdCircle, topRect[i]) || Intersector.overlaps(birdCircle, botRect[i])) {
                Gdx.app.log("collision", "yep!!!!");
                gameState = 2;

            }

        }
    }

    private int flip(int flapState) {
        if (countflap % 9 == 0) {
            countflap = 0;
            if (flapState == 1)
                flapState = 0;
            else
                flapState = 1;
        }

        return flapState;
    }


    public void startGame() {

        birdY = Gdx.graphics.getHeight() / 2;
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
        randomGenerator = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() / 2;

        for (int i = 0; i < numberOfTubes; i++) {

            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200) / 4;
            tubeX[i] = Gdx.graphics.getWidth() - toptube.getWidth() / 2 + i * distanceBetweenTubes;
            topRect[i] = new Rectangle();
            botRect[i] = new Rectangle();

        }
        scoringTube = 0;
        score = 0;
    }

    public void gameOver() {
        batch.begin();
        batch.draw(gameover, Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameover.getHeight() / 2);
        smallerFont.setColor(Color.GRAY);
        smallerFont.draw(batch, "Score: " + String.valueOf(score), Gdx.graphics.getWidth() / 2 - 155, Gdx.graphics.getHeight() / 2 - gameover.getWidth() / 2 - 25);
        smallerFont.setColor(Color.BLACK);
        smallerFont.draw(batch, "High Score: " + highScore, Gdx.graphics.getWidth() / 2 - 155, Gdx.graphics.getHeight() / 2 - gameover.getWidth() / 2 - 25 - 75);
        batch.end();
    }

    private void checkForScoringTube() {
        if (tubeX[scoringTube] < (Gdx.graphics.getWidth() / 2 - edwin1[flapState].getWidth() / 2)) {
            score++;
            if (score > highScore) {
                highScore = score;
                manageHighScore(highScore);
            }

            if (scoringTube < numberOfTubes - 1)
                scoringTube++;
            else
                scoringTube = 0;
        }
    }


    public void manageHighScore(int score) {
        if (score == -1) {
            Preferences preferences = Gdx.app.getPreferences("com.devon.flappybird");

            if(preferences.contains("HighScore")){
                String high = preferences.getString("HighScore");
                highScore = Integer.valueOf(high);
            }


        } else {
            Preferences preferences = Gdx.app.getPreferences("com.devon.flappybird");
            preferences.putString("HighScore", Integer.toString(highScore));
            preferences.flush();
        }
    }
}

