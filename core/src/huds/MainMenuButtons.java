package huds;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.neymeha.kackthegiant.GameMain;
import helpers.GameInfo;
import helpers.GameManager;
import sceens.GamePlay;
import sceens.Highscore;
import sceens.MainMenu;
import sceens.Options;

public class MainMenuButtons {
    private GameMain game;
    private Stage stage;
    private Viewport gameViewport;
    private ImageButton playBtn, highScoreBtn, optionsBtn, quitBtn, musicBtn;

    public MainMenuButtons(GameMain game) {
        this.game = game; // зависимость от основного класса

        gameViewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, new OrthographicCamera());

        stage = new Stage(gameViewport, game.getBatch()); // создаем стейдж передавая вьюпорт и батч
        Gdx.input.setInputProcessor(stage); // говорим гдх кто у нас управлять будет процессами на взаимодействие с игрой , без этого нажатие на кнопку никак не откликнется

        createAndPositionButtons(); // в конструкторе создаем, задаем позицию, добавляем в стейдж наши кнопки

        addAllListeners(); // добавляем функционал нашим кнопкам при нажатии

        checkMusic(); // проверяем настройку по музыке и запускаем ее если она должна быть включена
    }

    void createAndPositionButtons(){ // создаем кнопки ниже, хз почему такая сложная иерархия в конструкторах но пусть будет так
        playBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/Main Menu Buttons/Start Game.png"))));
        highScoreBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/Main Menu Buttons/Highscore.png"))));
        optionsBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/Main Menu Buttons/Options.png"))));
        quitBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/Main Menu Buttons/Quit.png"))));
        musicBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/Main Menu Buttons/Music On.png"))));
        // задаем положение кнопкам относительно позиционрирования их по центру
        playBtn.setPosition(GameInfo.WIDTH/2f-80, GameInfo.HEIGHT/2f+50, Align.center);
        highScoreBtn.setPosition(GameInfo.WIDTH/2f-60, GameInfo.HEIGHT/2f-20, Align.center);
        optionsBtn.setPosition(GameInfo.WIDTH/2f-40, GameInfo.HEIGHT/2f-90, Align.center);
        quitBtn.setPosition(GameInfo.WIDTH/2f-20, GameInfo.HEIGHT/2f-160, Align.center);
        musicBtn.setPosition(GameInfo.WIDTH-17, 17, Align.bottomRight);
        // добавляем наши кнопки в качестве актеров на наш stage
        stage.addActor(playBtn);
        stage.addActor(highScoreBtn);
        stage.addActor(optionsBtn);
        stage.addActor(quitBtn);
        stage.addActor(musicBtn);
    }

    void addAllListeners() { // добавляем действие на нажатие кнопки
        playBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // любой код тут написанный сработает при нажатии на кнопку
                GameManager.getInstance().gameStartedFromMainMenu = true; // поскольку пошел запуск игры значит игра началась и мэйн меню что мы и выставили через булевое значение менеджера

                RunnableAction run = new RunnableAction(); // создали действие (это отдельный тред что ли?)
                run.setRunnable(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new GamePlay(game)); // с таким кодом
                    }
                });

                SequenceAction sa = new SequenceAction(); // создали последовательность действий

                sa.addAction(Actions.fadeOut(1f)); // добавили действие затемнения
                sa.addAction(run); // запустили наше действие

                stage.addAction(sa); // передали наши действия в обработчик

            }
        });
        highScoreBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new Highscore(game)); // при нажатии запускается экран с хай скором
            }
        });
        optionsBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new Options(game));
            }
        });
        quitBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        musicBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (GameManager.getInstance().gameData.isMusicOn()) {
                    GameManager.getInstance().gameData.setMusicOn(false); // устанавливаем флажок для памяти что музыка выключине
                    GameManager.getInstance().stopMusic(); // выключаем музыку
                } else {
                    GameManager.getInstance().gameData.setMusicOn(true); // устанавливаем флажок для памяти что музыка включена
                    GameManager.getInstance().playMusic(); // запускаем музыку
                }
                GameManager.getInstance().saveData(); // сохраняем наш флажок в памяти
            }
        });
    }

    void checkMusic() { // метод для стартоовой загрузки настроек по музыке
        if (GameManager.getInstance().gameData.isMusicOn()) {
            GameManager.getInstance().playMusic();
        }
    }

    public Stage getStage() {
        return stage;
    }
}
