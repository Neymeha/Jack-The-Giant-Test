package sceens;

import clouds.CloudsController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.neymeha.kackthegiant.GameMain;
import helpers.GameData;
import helpers.GameInfo;
import helpers.GameManager;
import huds.UIHud;
import player.Player;

public class GamePlay implements Screen, ContactListener /* интерфейс нужный для реагирования на контакты наших обьектов */{

    private GameMain game;
    private OrthographicCamera mainCamera; // а это основная камера игры
    private Viewport gameViewport; // вьюпор это можно сказать часть камеры, только камера более большой класс, а viewport это что то вроде линзы в которую мы смотрим
    private OrthographicCamera box2DCamera; // если я правильно понял то эта камера чисто для дебаг рендера
    private Box2DDebugRenderer debugRenderer;
    private World world;
    private CloudsController cloudsController;
    private Player player;
    private float lastPlayerY; // будет содержать последнее местоположение игрока по игрику
    private Sprite[] bgs; // массив для множественных задних фонов что бы сделать длинное полотно
    private float lastYPosition; // для отслеживания позицию по Y нашего последнего фона
    private Sound coinSound, lifeSound;
    private float cameraSpeed = 10; // скорость камеры по дефолту для сложности
    private float maxSpeed = 10; // максимальная скорость камеры
    private float acceleration = 10; // ускорение камеры
    private boolean touchedForTheFirstTime;
    private UIHud hud;

    public GamePlay(GameMain game) {

        this.game = game;

        mainCamera = new OrthographicCamera(GameInfo.WIDTH, GameInfo.HEIGHT);
        mainCamera.position.set(GameInfo.WIDTH/2f, GameInfo.HEIGHT/2f, 0);

        gameViewport = new StretchViewport(GameInfo.WIDTH, GameInfo.HEIGHT, mainCamera); // надо побольше разобрать в этих вьюпортах и камерах, на пальцах понятно но читаю вики и не понятно, вроде как это условно когда мы в камеру смотрим вот это и есть вьюпорт

        box2DCamera = new OrthographicCamera();
        box2DCamera.setToOrtho(false, GameInfo.WIDTH/GameInfo.PPM, GameInfo.HEIGHT/GameInfo.PPM);
        box2DCamera.position.set(GameInfo.WIDTH/2f, GameInfo.HEIGHT/2f, 0);

        debugRenderer = new Box2DDebugRenderer();

        hud = new UIHud(game);

        world = new World(new Vector2(0, -9.8f), true);
        world.setContactListener(this); // рассказали миру про наш контакт листенер, иначе ничего работать не будет

        cloudsController = new CloudsController(world);
        player = cloudsController.positionThePlayer(player); // замысловатое создание игрока не совсем понятно зачем передавать в метод игрока но в принципе ничего страшного нету

        createBackgrounds(); // инициализоировали весь массив спрайтов и создали в нем все спрайты задав их место

        setCameraSpeed();

        coinSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/Coin Sound.wav"));
        lifeSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/Life Sound.wav"));

    }

    void handleInput(float dt){ // обрабатываем нажатие клави лево/право и двигаем игрока соответственно
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.movePlayer(-2);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.movePlayer(2);
        } else {
            player.setWalking(false);
        }
    }

    void handleInputMobileOrMouse() {
        if (Gdx.input.isTouched()) { // испльзуем из тачт потому что он фиксирует длительное нажатие
            if (Gdx.input.getX() > GameInfo.WIDTH/2) {
                player.movePlayer(2);
            } else if (Gdx.input.getX() < GameInfo.WIDTH/2){
                player.movePlayer(-2);
            } else {
                player.setWalking(false);
            }
        }
    }

    void checkForFirstTouch() {
        // замысловатое написание, суть, если мы только зашли в геймплей, игра на паузе висит, пока мы не каснемся
        // экрана, и только тогда она начнется и больше проблем с этой проверкой во время игры не будет
        if (!touchedForTheFirstTime) { // проверяем, мы не первый раз коснулись экрана?
            if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) || Gdx.input.justTouched()) { // при касании, или нажатии мыши, или любой кнопки, это я от себя добавил
                touchedForTheFirstTime = true; // меняем дефолтное значение на тру
                GameManager.getInstance().isPaused = false; // а в менеджере снимаем паузу
                lastPlayerY = player.getY(); // присваиваем при старте игры для счетчика игрового
            }
        }
    }

    void update(float delta) {

        checkForFirstTouch();

        if (!GameManager.getInstance().isPaused) { // если игра не на паузе то мы все это делаем и только так
            handleInput(delta); // обрабатываем нажатие лево/право на движения игрока
            handleInputMobileOrMouse(); // обрабатываем нажатие на экран пальцем или мышью
            moveCamera(delta); // дельта это время которое проходит между двумя кадрами
            checkBackgroundOutOfBounds(); // метод для бесконечных задних фонов
            cloudsController.setCameraY(mainCamera.position.y); // метод для ? не помню, помоему нужно в передать местоположение камеры по игрику для воссоздания дальнейшего облаков
            cloudsController.createAndArrangeNewClouds(); // создаем новые облака и делаем процесс бесконечным
            cloudsController.removeOffScreenCollectables(); // убираем итемы которые уже вне видимости камеры
            checkPlayersBounds();
            countScore();
        }
    }

    void moveCamera(float delta){// метод для передвижения камеры
        mainCamera.position.y -= cameraSpeed * delta; // статичная скорость с которой движется наша камера
//        box2DCamera.position.y -= cameraSpeed * delta;

        cameraSpeed += acceleration * delta; // скорость камеры растет с учетом ускорения

        if (cameraSpeed>maxSpeed) { // скорость камеры не должна превышать максимальную скорость а если привышает приравниваем к максимуму
            cameraSpeed = maxSpeed;
        }
    }

    void setCameraSpeed() {
        // метод калибровки состояний скорости относительно сложности игры
        if (GameManager.getInstance().gameData.isEasyDifficulty()) {
            cameraSpeed = 80;
            maxSpeed = 100;
        }
        if (GameManager.getInstance().gameData.isMediumDifficulty()) {
            cameraSpeed = 100;
            maxSpeed = 120;
        }
        if (GameManager.getInstance().gameData.isHardDifficulty()) {
            cameraSpeed = 120;
            maxSpeed = 140;
        }
    }

    void createBackgrounds(){
        bgs = new Sprite[3];
        for (int i = 0; i < bgs.length; i++){ // короче тут мы просто планируем одну за другой картинку разместить вниз
            bgs[i] = new Sprite(new Texture("Backgrounds/Game BG.png")); // это для того когда камера будет вниз ползти
            bgs[i].setPosition(0, -(i * bgs[i].getHeight())); // таким образом с каждой итерацией на 800 ниже начнется отрисовка
            lastYPosition = Math.abs(bgs[i].getY()); // тут мы фиксируем позицию по высоте последнего спрайта нашего фона, что бы потом перести первый спрайт под него, с помощью математического класса получаем абсолютное значение, положительное число
        }
    }

    void drawBackgrounds(){ // отрисовываем все задние фоны один за другим вниз
        for (int i = 0; i < bgs.length; i++){
            game.getBatch().draw(bgs[i], bgs[i].getX(), bgs[i].getY());
        }
    }

    void checkBackgroundOutOfBounds(){
        for (int i = 0; i<bgs.length; i++){
            if ((bgs[i].getY() - bgs[i].getHeight()/2f - 5 /* нужно что бы не было задержки между перестановкой фона */) > mainCamera.position.y) { // проверяем сместилась ли камера ниже какого то из наших задних фонов
                float newPosition = bgs[i].getHeight()+lastYPosition; //новая позиция на которой должен быть размещен наш смещенный бэкграунд
                bgs[i].setPosition(0, -newPosition); // задаем эту позицию со знаком минус естественно
                lastYPosition = Math.abs(newPosition); // и обновляем нашу последнюю позицию
            }
        }
    }

    void checkPlayersBounds() {
        // проверяем вышел ли наш игрок за границы экрана сверху
        if (player.getY() - GameInfo.HEIGHT/2f - player.getHeight()/2f > mainCamera.position.y) {
            if (!player.isDead()) {
                playerDied();
            }
        }
        // проверяем вышел ли наш игрок за границы экрана снизу
        if (player.getY() + GameInfo.HEIGHT/2f + player.getHeight()/2f < mainCamera.position.y) {
            if (!player.isDead()) {
                playerDied();
            }
        }
        // право и лево
        if (player.getX() - 25 > GameInfo.WIDTH || player.getX() + 60 < 0) {
            if (!player.isDead()) {
                playerDied();
            }
        }

    }

    void countScore() {
        // увеличиваем очки за каждый пиксель пройденный вниз получается
        if (lastPlayerY > player.getY()) {
            hud.incrementScore(1);
            lastPlayerY = player.getY();
        }
    }

    void playerDied() {
        GameManager.getInstance().isPaused = true;
        hud.decrementLife();
        player.setDead(true);
        player.setPosition(1000, 1000 );
        if (GameManager.getInstance().lifeScore < 0) {
            // у игрока нет больше жизней что бы продолжать игру
            // проверяем на хай скор и показываем текущий
            GameManager.getInstance().checkForNewHighscores(); // проверяем наши счетчики с счетчиками сохраненными и переприсваиваем
            hud.createGameOverPanel(); // показываем гейм овер панельку
            // и загружаем main menu
            RunnableAction run = new RunnableAction(); // создали действие (это отдельный тред что ли?)
            run.setRunnable(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new MainMenu(game)); // с таким кодом
                }
            });

            SequenceAction sa = new SequenceAction(); // создали последовательность действий

            sa.addAction(Actions.delay(3f)); // добавили действие задержки
            sa.addAction(Actions.fadeOut(1f)); // добавили действие затемнения
            sa.addAction(run); // запустили наше действие

            hud.getStage().addAction(sa);

        } else {

            RunnableAction run = new RunnableAction(); // создали действие (это отдельный тред что ли?)
            run.setRunnable(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new GamePlay(game)); // с таким кодом
                }
            });

            SequenceAction sa = new SequenceAction(); // создали последовательность действий

            sa.addAction(Actions.delay(3f)); // добавили действие задержки
            sa.addAction(Actions.fadeOut(1f)); // добавили действие затемнения
            sa.addAction(run); // запустили наше действие

            hud.getStage().addAction(sa);

        }

    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        update(delta);

        ScreenUtils.clear(0, 0, 0, 1);

        game.getBatch().begin();

        drawBackgrounds(); // отрисовали все задние фоны один под другим, три штуки

        cloudsController.drawClouds(game.getBatch());
        cloudsController.drawCollectables(game.getBatch());

        player.drawPlayerIdle(game.getBatch());
        player.drawPlayerAnimation(game.getBatch());

        game.getBatch().end();

//        debugRenderer.render(world, box2DCamera.combined);

        game.getBatch().setProjectionMatrix(hud.getStage().getCamera().combined);
        hud.getStage().draw();
        hud.getStage().act();

        game.getBatch().setProjectionMatrix(mainCamera.combined); // говорим нашего батчу отрисовывать все в рамках матрицы нашей камеры?Оо не совсем понял, но помоему все верно я понял
        mainCamera.update();

        player.updatePlayer();

        world.step(Gdx.graphics.getDeltaTime(), 6,2);

    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height);
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
        world.dispose();
        for (int i=0; i< bgs.length; i++) {
            bgs[i].getTexture().dispose();
        }
        player.getTexture().dispose();
        debugRenderer.dispose();
        hud.getStage().dispose();
        lifeSound.dispose();
        coinSound.dispose();
    }

    @Override
    public void beginContact(Contact contact) {
        // обьявили две фиксчуры к которым привяжем оба тела которые соприкоснулись
        Fixture body1, body2;
        // дальше код который присвоит первому телу - тело игрока, а второму - тело с которым он столкнулся
        if (contact.getFixtureA().getUserData() == "Player") {
            body1 = contact.getFixtureA();
            body2 = contact.getFixtureB();
        } else {
            body1 = contact.getFixtureB();
            body2 = contact.getFixtureA();
        }
        // далее проверяем с кем же конкретно столкнулся игрок и запускаем определенный код в каждом конкретном случае
        if (body1.getUserData() == "Player" && body2.getUserData() == "Coin") {
            hud.incrementCoins(); // увеличиваем счетчик моент
            coinSound.play();
            body2.setUserData("Remove"); // ставим на удаление
            cloudsController.removeCollectables(); // убираем ненужные итемы
        }
        if (body1.getUserData() == "Player" && body2.getUserData() == "Life") {
            hud.incrementLifes();
            lifeSound.play();
            body2.setUserData("Remove");
            cloudsController.removeCollectables();
        }
        if (body1.getUserData() == "Player" && body2.getUserData() == "Dark Cloud") {
            if (!player.isDead()) {
                playerDied();
            }
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
