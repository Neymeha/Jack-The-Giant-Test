package clouds;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;
import helpers.GameInfo;

public class Cloud extends Sprite {
    private World world;
    private Body body;
    private String cloudName;
    private boolean drawLeft; // чисто для того что бы исправлять наше положение нарисованных облаков относительно физических тел в контроллере

    public Cloud(World world, String cloudName){
        super(new Texture("Clouds/"+cloudName+".png"));
        this.world = world;
        this.cloudName = cloudName;
    }

    void createBody(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((getX() - 45/* поправка */)/GameInfo.PPM, getY()/GameInfo.PPM); // позицию центруем по середине нашего физического облачка относительно нашего спрайта облачка c поправкой на бокс2д
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox((getWidth()/2-25/* поправка */)/GameInfo.PPM, (getHeight()/2-10)/GameInfo.PPM);
        fixtureDef.shape = polygonShape;
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(cloudName);

        polygonShape.dispose(); // это важно не забывать
    }

    public void setSpritePosition(float x, float y){
        setPosition(x,y);
        createBody();
    }

    public String getCloudName() {
        return cloudName;
    }

    public boolean getDrawLeft() {
        return drawLeft;
    }

    public void setDrawLeft(boolean drawLeft) {
        this.drawLeft = drawLeft;
    }
}
