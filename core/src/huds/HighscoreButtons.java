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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.neymeha.kackthegiant.GameMain;
import helpers.GameInfo;
import helpers.GameManager;
import sceens.MainMenu;

public class HighscoreButtons {
    private GameMain game; // основной игровой класс
    private Stage stage; // нащ обработчик процессов для наших будующих кнопочек
    private Viewport gameViewport; // вьюпорь который понадобится для инициализации стейдж
    private ImageButton backBtn; // кнопка для возврата в мейн меню
    private Label scoreLabel, coinLabel; // класс для надписей

    public HighscoreButtons(GameMain game) {
        this.game = game;
        //viewport который подгоняется под экран сам?
        gameViewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT, new OrthographicCamera());

        //инициализируем обработчик
        stage = new Stage(gameViewport, game.getBatch());

        //говорим гдх о нашем обработчике ввода
        Gdx.input.setInputProcessor(stage);

        //создаем и размещаем наши елементы (кнопки надписи)
        createAndPositionUIElements();

        // расскажем стейдж про нашу кнопку что бы он с ней работал, и про наши надписи что бы они отрисовывались
        stage.addActor(backBtn);
        stage.addActor(scoreLabel);
        stage.addActor(coinLabel);
    }

    void createAndPositionUIElements() {
        backBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/Options Buttons/Back.png"))));

        //ниже загружаем стиль для текста с помощью freetype библиотеки, передаем путь к файлу
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/blow.ttf"));
        // создаем класс для параметров нашего текста
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40; // размер в пикселях
        // создаем наш стиль для текста с помощью генератора и передачей в него обьекта с параметрами
        BitmapFont scoreFont = generator.generateFont(parameter); // он генерирует наш фонт который мы загрузили с переданными параметрами
        BitmapFont coinFont = generator.generateFont(parameter);
        // далее создаем обьект с текстом(надпись) передавая сам текст, новый обект с уже созданам стилем + цвет
        scoreLabel = new Label("" + GameManager.getInstance().gameData.getHighscore(), new Label.LabelStyle(scoreFont, Color.WHITE));
        coinLabel = new Label("" + GameManager.getInstance().gameData.getCoinHighscore(), new Label.LabelStyle(coinFont, Color.WHITE));

        //теперь поставим их на места
        backBtn.setPosition(17,17, Align.bottomLeft);
        scoreLabel.setPosition(GameInfo.WIDTH/2f - 40, GameInfo.HEIGHT/2 - 120);
        coinLabel.setPosition(GameInfo.WIDTH/2f - 40, GameInfo.HEIGHT/2 - 215);

        // создадим слушателя для кнопки возврата
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenu(game));
            }
        });
    }

    // геттер для стейдж, он нам понадопится на хай скор экране
    public Stage getStage() {
        return stage;
    }
}
