package collectables;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;
import helpers.GameInfo;

public class Collectable extends Sprite {
    private World world;
    private Body body;
    private Fixture fixture;
    private String name;

    public Collectable (World world, String name) {
        super(new Texture("Collectables/"+name+".png"));
        this.world = world;
        this.name = name;



    }

    void createCollectableBody(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set( (getX()-getWidth() / 2 - 20) / GameInfo.PPM, (getY()+getWidth()/2)/GameInfo.PPM);
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((getWidth()/2)/GameInfo.PPM, (getHeight()/2)/GameInfo.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = GameInfo.COLLECTABLE; // установили категорию тела
        fixtureDef.isSensor = true; // что бы мы могли проходить сквозь обьекты, но при этом регистрировались столкновения

        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(name); // передали название итема, что бы знать наверняка с чем именно столкнулся игрок

        shape.dispose();
    }

    public void setCollectablePosition(float x, float y){
        setPosition(x, y);
        createCollectableBody();
    }

    public void updateCollectable() { // метод нужный для обновления местоптложения текстуры перед отрисовкой, что бы совпадала с местом тела
        setPosition(body.getPosition().x * GameInfo.PPM, (body.getPosition().y - 0.2f) * GameInfo.PPM);
    }

    public void changeFilter(){
        // код для изменения категории тела, меняем категорию с итема на уничтожено и вносим ее в нашу фиксчуру что бы изменить.
        // после этого столкновения не буду регистрироваться между игроком и уничтоженным итемом
        Filter filter = new Filter(); // создаем фильтр
        filter.categoryBits = GameInfo.DESTROYED; // присваиваем ему котегорию
        fixture.setFilterData(filter); // с помощью сеттера меняем фильтр
    }

    public Fixture getFixture() {
        return fixture;
    }
}











