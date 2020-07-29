import java.util.ArrayList;

public class Bacterium {

    public int type;
    public float x;
    public float y;
    public float sx;
    public float sy;
    public float rotation;
    private boolean toBeDeleted;
    public int age;

    public float tx;
    public float ty;
    public float food;

    public float rotationSpeed = 0.2f;

    public float speed;
    public float slip = 0.8f;

    public float sightDistance = 100f;
    public float directionChangeRate = 0.05f;

    public Bacterium(int type, float x, float y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.sx = 0f;
        this.sy = 0f;
        this.rotation = 0f;
        this.toBeDeleted = false;
        this.age = 0;
        this.tx = 0;
        this.ty = 0;
        this.food = 3f;
    }
    void create(ArrayList<Bacterium> bacteria) {
        if(this.food >= 6) {
            this.food -= 3;
            int type = this.type;
            if(Math.random() < 0.05) {
                type = (int)(Math.random() * 3);
            }
            Bacterium b;
            if (type == 0) {
                b = new BlueBacterium(type, this.x + (float)Math.random() * 10 - 5, this.y + (float)Math.random() * 10 - 5);
            } else if (type == 1) {
                b = new RedBacterium(type, this.x + (float)Math.random() * 10 - 5, this.y + (float)Math.random() * 10 - 5);
            } else {
                b = new GreenBacterium(type, this.x + (float)Math.random() * 10 - 5, this.y + (float)Math.random() * 10 - 5);
            }
            b.slip = this.slip;
            bacteria.add(b);
        }
    }
    void setToBeDeleted() {
        toBeDeleted = true;
    }
    boolean getToBeDeleted() {
        return this.toBeDeleted;
    }
    //void die(ArrayList<Bacterium> bacteria) { bacteria.remove(this); }

}