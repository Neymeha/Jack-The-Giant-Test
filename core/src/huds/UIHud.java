package huds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.neymeha.kackthegiant.GameMain;
import helpers.GameInfo;
import helpers.GameManager;
import sceens.MainMenu;

public class UIHud {
    private GameMain game;
    private Stage stage;
    private Viewport gameViewport;
    private Image coinImg, scoreImg, liveImg, pausePanel;
    private Label coinLabel, scoreLabel, liveLabel;
    private ImageButton pauseBtn, resumeBtn, quitBtn;

    public UIHud(GameMain game) {
        this.game = game;

        gameViewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, new OrthographicCamera());

        stage = new Stage(gameViewport, game.getBatch());
        Gdx.input.setInputProcessor(stage);

        if (GameManager.getInstance().gameStartedFromMainMenu) {
            // это первый запуск игры так что надо выставить первоначальные значения
            GameManager.getInstance().lifeScore = 2; // дефолтные значения для счетчиков жизни монет счёта
            GameManager.getInstance().coinScore = 0;
            GameManager.getInstance().score = 0;
            GameManager.getInstance().gameStartedFromMainMenu = false; // больше это не первый запуск
        }

        createLabels();
        createImages();
        createBtnAndAddListener();

        Table lifeAndCoinTable = new Table(); // это условно "маска" такая в виде списка где можно размещать наши элементы
        lifeAndCoinTable.top().left(); // задали нашей таблице верхний левый угол как ее место
        lifeAndCoinTable.setFillParent(true); // подгоняет размеры наших актеров к их супер классу, не совсем понятно чо да как ну посмотрим
        // или скорее дает возможность их размещать вот таким способом как я понял, иначе наши таблицы не будут в границах наших камер
        lifeAndCoinTable.add(liveImg).padLeft(10).padTop(10);// отступили слево и сверху по 10 пикселей
        lifeAndCoinTable.add(liveLabel).padLeft(5); // добавили вторую ячейку в строчку
        lifeAndCoinTable.row(); // закончили с этой строчкой пошли к следующей
        lifeAndCoinTable.add(coinImg).padLeft(10).padTop(10);// отступаем мы от виртуальной полосы нашей колонки или строчки
        lifeAndCoinTable.add(coinLabel).padLeft(5);

        Table scoreTable = new Table();
        scoreTable.top().right();
        scoreTable.setFillParent(true); // что делает так и не понял но без них наших актеров видно не будет 100%
        scoreTable.add(scoreImg).padRight(10).padTop(10);
        scoreTable.row();
        scoreTable.add(scoreLabel).padRight(20).padTop(15);

        stage.addActor(lifeAndCoinTable); // удобство таблицы
        stage.addActor(scoreTable); // нам не надо каждый элемент по отдельности доабвлять
        stage.addActor(pauseBtn);
    }

    void createLabels() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/blow.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40;
        BitmapFont font = generator.generateFont(parameter);

        coinLabel = new Label("x"+GameManager.getInstance().coinScore, new Label.LabelStyle(font, Color.WHITE));
        liveLabel = new Label("x"+GameManager.getInstance().lifeScore, new Label.LabelStyle(font, Color.WHITE));
        scoreLabel = new Label(""+GameManager.getInstance().score, new Label.LabelStyle(font, Color.WHITE));
    }

    void createImages() {
        coinImg = new Image(new Texture("Collectables/Coin.png"));
        liveImg = new Image(new Texture("Collectables/Life.png"));
        scoreImg = new Image(new Texture("Buttons/Gameplay Buttons/Score.png"));
    }

    void createBtnAndAddListener(){
        pauseBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/Gameplay Buttons/Pause.png"))));
        pauseBtn.setPosition(460, 17, Align.bottomRight);
        pauseBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!GameManager.getInstance().isPaused) { // если игра не на паузе уже
                    GameManager.getInstance().isPaused = true; // тогда ставим ее на паузу булевым значением, ибо тогда update  в GamePlay не будет ничего делать
                    createPausePanel(); // и выводим нашу панель паузы
                }
            }
        });
    }

    void createPausePanel(){

        pausePanel = new Image(new Texture("Pause Panel And Buttons/Pause Panel.png"));
        resumeBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Pause Panel And Buttons/Resume.png"))));
        quitBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Pause Panel And Buttons/Quit 2.png"))));

        pausePanel.setPosition(GameInfo.WIDTH/2f, GameInfo.HEIGHT/2f, Align.center); // без Aling по центру ничего не будет
        resumeBtn.setPosition(GameInfo.WIDTH/2f, GameInfo.HEIGHT/2f + 50, Align.center); // хотя координаты мы задали
        quitBtn.setPosition(GameInfo.WIDTH/2f, GameInfo.HEIGHT/2f - 80, Align.center); // верно хз почему так

        resumeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                removePausePanel(); // при резьюм убераем панель паузы
                GameManager.getInstance().isPaused = false; // и с помощью изменения булевого значения запускаем update в GamePlay
            }
        });

        quitBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenu(game));
            }
        });

        stage.addActor(pausePanel);
        stage.addActor(resumeBtn);
        stage.addActor(quitBtn);
    }

    void removePausePanel(){
        // уберет их как актеров из stage
        pausePanel.remove();
        resumeBtn.remove();
        quitBtn.remove();
    }

    public void createGameOverPanel() {
        Image gameOverPanel = new Image(new Texture("Pause Panel And Buttons/Show Score.png")); // показали путь к изображению энд скора
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/blow.ttf")); // делаем шрифт указывая путь
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter(); // создаем ему параметры
        parameter.size = 70; // задаем размер в параметрах
        BitmapFont font = generator.generateFont(parameter); // генерируем наш шрифт

        Label endScore = new Label(""+GameManager.getInstance().score, new Label.LabelStyle(font, Color.WHITE)); // создали надпись
        Label endCoinScore = new Label(""+GameManager.getInstance().coinScore, new Label.LabelStyle(font, Color.WHITE)); // создали надпись

        // дальше задаем позицию для нашей панеле и ее элементов
        gameOverPanel.setPosition(GameInfo.WIDTH/2f, GameInfo.HEIGHT/2f, Align.center);
        endScore.setPosition(GameInfo.WIDTH/2f - 30, GameInfo.HEIGHT/2f + 20, Align.center);
        endCoinScore.setPosition(GameInfo.WIDTH/2f - 30, GameInfo.HEIGHT/2f - 90, Align.center);

        // добавляем наши элементы как актеров в стейдж для отрисовки
        stage.addActor(gameOverPanel);
        stage.addActor(endScore);
        stage.addActor(endCoinScore);
    }

    public void incrementScore(int score) {
        GameManager.getInstance().score += score;
        scoreLabel.setText(""+GameManager.getInstance().score);
    }

    public void incrementCoins() {
        GameManager.getInstance().coinScore++;
        coinLabel.setText("x"+GameManager.getInstance().coinScore);
        incrementScore(200);
    }

    public void incrementLifes() {
        GameManager.getInstance().lifeScore++;
        liveLabel.setText("x"+GameManager.getInstance().lifeScore);
        incrementScore(300);
    }

    public void decrementLife () {
        GameManager.getInstance().lifeScore--;
        if (GameManager.getInstance().lifeScore >= 0) {
            liveLabel.setText("x" + GameManager.getInstance().lifeScore);
        }
    }

    public Stage getStage() {
        return stage;
    }
}
