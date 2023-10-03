package huds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.neymeha.kackthegiant.GameMain;
import helpers.GameInfo;
import helpers.GameManager;
import sceens.MainMenu;


public class OptionsButtons {
    private GameMain game;
    private Stage stage;
    private Viewport gameViewport;
    private ImageButton easy, medium, hard, backBtn;
    private Image sign;

    public OptionsButtons(GameMain game) {
        this.game = game;
        //не догоаняю зачем новая камера для вьюпорта, ведь в гейм есть камера, но видимо нам нужна новая на разных экранах
        gameViewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, new OrthographicCamera());

        stage = new Stage(gameViewport, game.getBatch());
        Gdx.input.setInputProcessor(stage);

        createAndPositionButtons();

        addAllListeners();
    }

    void createAndPositionButtons(){
        easy = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/Options Buttons/Easy.png"))));
        medium = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/Options Buttons/Medium.png"))));
        hard = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/Options Buttons/Hard.png"))));
        backBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/Options Buttons/Back.png"))));
        sign = new Image(new Texture("Buttons/Options Buttons/Check Sign.png"));

        easy.setPosition(GameInfo.WIDTH/2f, GameInfo.HEIGHT/2f+40, Align.center);
        medium.setPosition(GameInfo.WIDTH/2f, GameInfo.HEIGHT/2f-40, Align.center);
        hard.setPosition(GameInfo.WIDTH/2f, GameInfo.HEIGHT/2f-120, Align.center);
        backBtn.setPosition(17, 17, Align.bottomLeft);
        positionTheSign();

        stage.addActor(easy);
        stage.addActor(medium);
        stage.addActor(hard);
        stage.addActor(backBtn);
        stage.addActor(sign);
    }

    void addAllListeners(){
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenu(game));
            }
        });
        easy.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sign.setY(easy.getY()+13);
                changeDifficulty(0);
            }
        });
        medium.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sign.setY(medium.getY()+13);
                changeDifficulty(1);
            }
        });
        hard.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sign.setY(hard.getY()+13);
                changeDifficulty(2);
            }
        });
    }

    public void positionTheSign(){
        // размещаем нашу сложность согласно сохраненных данных
        if (GameManager.getInstance().gameData.isEasyDifficulty()) {
            sign.setPosition(GameInfo.WIDTH/2f+76, easy.getY()+13, Align.bottomLeft);
        }
        if (GameManager.getInstance().gameData.isMediumDifficulty()) {
            sign.setPosition(GameInfo.WIDTH/2f+76, medium.getY()+13, Align.bottomLeft);
        }
        if (GameManager.getInstance().gameData.isHardDifficulty()) {
            sign.setPosition(GameInfo.WIDTH/2f+76, hard.getY()+13, Align.bottomLeft);
        }

    }

    void changeDifficulty(int difficulty) {
        // метод для изменения сложности в кнопках
        switch (difficulty) {
            case 0:
                GameManager.getInstance().gameData.setEasyDifficulty(true);
                GameManager.getInstance().gameData.setMediumDifficulty(false);
                GameManager.getInstance().gameData.setHardDifficulty(false);
                break;
            case 1:
                GameManager.getInstance().gameData.setEasyDifficulty(false);
                GameManager.getInstance().gameData.setMediumDifficulty(true);
                GameManager.getInstance().gameData.setHardDifficulty(false);
                break;
            case 2:
                GameManager.getInstance().gameData.setEasyDifficulty(false);
                GameManager.getInstance().gameData.setMediumDifficulty(false);
                GameManager.getInstance().gameData.setHardDifficulty(true);
                break;
        }
        GameManager.getInstance().saveData();
    }

    public Stage getStage() {
        return stage;
    }
}
