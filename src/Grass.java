import java.awt.*;

public class Food {

    public int type;
    public float x;
    public float y;
    public boolean toBeDeleted;
    public float size;

    public static Color[] COLOR = {
            new Color(255, 100, 100),
            new Color(200, 200, 100),
            new Color(50, 150, 50),
            new Color(100, 150, 200),
            new Color(50, 50, 255),
            new Color(150, 50, 150)
    };

    public Food(float x, float y, float size) {
        this.x = x;
        this.y = y;
        this.type = (int)(Math.random() * 6);
        this.toBeDeleted = false;
        this.size = size;
    }

}