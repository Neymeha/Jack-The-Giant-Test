package sceens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.neymeha.kackthegiant.GameMain;
import helpers.GameInfo;
import huds.MainMenuButtons;

public class MainMenu implements Screen {

    private GameMain game;
    private OrthographicCamera mainCamera;
    private Viewport gameViewport;
    private Texture bg;
    private MainMenuButtons btns;

    public MainMenu(GameMain game){
        this.game = game;

        bg = new Texture("Backgrounds/Menu BG.png");

        mainCamera = new OrthographicCamera();
        mainCamera.setToOrtho(false,GameInfo.WIDTH, GameInfo.HEIGHT);
        mainCamera.position.set(GameInfo.WIDTH/2f, GameInfo.HEIGHT/2f, 0);

        gameViewport = new StretchViewport(GameInfo.WIDTH, GameInfo.HEIGHT, mainCamera);

        btns = new MainMenuButtons(game);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
//        btns.getStage().act(delta);
        game.getBatch().begin();

        game.getBatch().draw(bg, 0, 0);

        game.getBatch().end();

        game.getBatch().setProjectionMatrix(btns.getStage().getCamera().combined);
        btns.getStage().draw();
        btns.getStage().act(); // сказали нашему обработчику запускать действия
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height); // смотри хайскор
        // эта проблема с красными полосами появляется из за того что мы камеру из стейдж комбайним и отрисовываем а потом ресайс
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        bg.dispose();
        btns.getStage().dispose();
    }
}
