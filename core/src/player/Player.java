package player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import helpers.GameInfo;

public class Player extends Sprite {

    private World world;
    private Body body;
    private TextureAtlas playerAtlas; // текстурный атлас для нашей анимации
    private Animation <TextureAtlas.AtlasRegion> animation; // сама наша анимация
    private float elapsedTime; // время через которое будут меняться картинки в анимации
    private boolean isWalking, dead; // переменная - идет ли куда то наше тело или нет

    public Player(World world, float x, float y) {
        super(new Texture("Player/Player 1.png"));
        this.world = world;
        setPosition(x,y);
        createBody();
        playerAtlas = new TextureAtlas("Player Animation/Player Animation.atlas"); // инициализировали атлас
        dead = false;
    }

    private void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(getX() / GameInfo.PPM, getY() / GameInfo.PPM);
        body = world.createBody(bodyDef);
        body.setFixedRotation(true); // запретит нашему телу перекатываться как шарик, а он ведь коробочка

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((getWidth()/2f-20f)/GameInfo.PPM, (getHeight()/2f)/GameInfo.PPM);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 0f; // это масса тела
        fixtureDef.friction = 2f;// это скольжение усовно
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = GameInfo.PLAYER; // определяет категорию body но не совсем понятно что именно, по дефолту если не определяем это 1
        fixtureDef.filter.maskBits = GameInfo.DEFAULT | GameInfo.COLLECTABLE; // определяет с какими другими категориями тел это может сталкиваться. с дефолтом (1) и с итемами (4)

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData("Player"); // установили название тела для регистратора столкновений

        shape.dispose();
    }

    public void updatePlayer(){
        if (body.getLinearVelocity().x>0) {
            // если идем вправо
            setPosition(body.getPosition().x*GameInfo.PPM*1.1f-15, body.getPosition().y*GameInfo.PPM);
        } else if (body.getLinearVelocity().x<0) {
            //если идем влево
            setPosition((body.getPosition().x-0.3f)*GameInfo.PPM/0.95f+15, body.getPosition().y*GameInfo.PPM);
            // это нужно для рассинхрона хотьбы спрайта с физ телом, хотя не сказать что помогло 100%
            // ну да ладно, если проблема повториться надо поиграть со всей этой хренью, поиграл. Пока это лучшее
        } else {
            setPosition((body.getPosition().x-0.3f)*GameInfo.PPM/0.95f+15, body.getPosition().y*GameInfo.PPM);
            // без этого не будет отрисовки падающего но не двигающегося по оси икс игрока
            // я не сильно корректировал позицию тела
        }
    }

    public void drawPlayerIdle(SpriteBatch batch){
        if (!isWalking) { // если игрок не двигается то отрисовываем единственную текстуру
            batch.draw(this, getX()+getWidth()/2f - 20, getY()-getHeight()/2f);
        }
    }

    public void drawPlayerAnimation(SpriteBatch batch) {
        if (isWalking) {
            elapsedTime += Gdx.graphics.getDeltaTime();

            Array<TextureAtlas.AtlasRegion> frames = playerAtlas.getRegions();
            for (TextureRegion frame:frames) { // переворачиваем текстуру если есть необходимость
                if (body.getLinearVelocity().x<0 && !frame.isFlipX()) {
                    frame.flip(true, false);
                } else if (body.getLinearVelocity().x>0 && frame.isFlipX()) {
                    frame.flip(true, false);
                }
            }

            animation = new Animation<>(1f/10f, playerAtlas.getRegions());
            batch.draw(animation.getKeyFrame(elapsedTime,true), getX()+getWidth()/2f - 20f, getY()-getHeight()/2f);
        }
    }

    public void movePlayer(float x){ // метод для движения игрока
        if (x<0 && !this.isFlipX()) { // переворачиваем текстуру для текстуры без движения
            this.flip(true, false);
        } else if (x>0 && this.isFlipX()) {
            this.flip(true, false);
        }

        setWalking(true);
        body.setLinearVelocity(x, body.getLinearVelocity().y); // но мы хотим его двигать только по оси икс, по игрику будет работать гравитация
    }

    public void setWalking(boolean walking) {
        isWalking = walking;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isDead() {
        return dead;
    }
}
