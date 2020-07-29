import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Form extends JFrame implements Runnable {

    private final int w = 1280;
    private final int h = 720;

    private final int FRAMES_TOTAL = 100000;
    private final int SKIP_FRAMES = 1;

    private final Color BG = new Color(200, 200, 200, 255);
    private final Color BLUE = new Color(150, 160, 255, 255);
    private final Color RED = new Color(255, 100, 120, 255);
    private final Color GREEN = new Color(150, 255, 160, 255);
    private BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    private BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    private BufferedImage graph = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    private BufferedImage sprites[] = new BufferedImage[3];
    private final AffineTransform IDENTITY = new AffineTransform();

    private ArrayList<Bacterium> bacteria = new ArrayList<>();
    private ArrayList<Grass> grass = new ArrayList<>();

    private final int GRASS_RADIUS = 5;
    private final int BACTERIA_RADIUS = 20;
    private int frame = 0;

    public Form() {
        for (int i = 0; i < sprites.length; i++) {
            try {
                sprites[i] = ImageIO.read(new File("img/m" + i + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.setSize(w + 16, h + 38);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(50, 50);
        this.add(new JLabel(new ImageIcon(img)));

        Bacterium a = new BlueBacterium(0, (float)(Math.random() * (w - 100) + 50), (float)(Math.random() * (h - 100) + 50));
        bacteria.add(a);

    }

    @Override
    public void run() {
        while(frame < FRAMES_TOTAL) {
            this.repaint();
        }
    }

    @Override
    public void paint(Graphics g) {
        try {
            drawScene(img);
            drawGraph(graph);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logic();
        Graphics2D g2 = buf.createGraphics();
        g2.drawImage(img, null, 0, 0);
        g2.drawImage(graph, null, 0, 0);
        ((Graphics2D)g).drawImage(buf, null, 8, 30);
    }

    private void drawGraph(BufferedImage image) throws IOException {
        Graphics2D g2 = image.createGraphics();
        if(bacteria.size() > 0) {
            int type0 = (int)bacteria.stream().filter(a -> a.type == 0).count();
            int type1 = (int)bacteria.stream().filter(a -> a.type == 1).count();
            int type2 = bacteria.size() - type0 - type1;
            int py = type0;
            if (py > h - 1) py = h - 1;
            int py1 = type1;
            if (py1 > h - 1) py1 = h - 1;
            int py2 = type2;
            if (py2 > h - 1) py2 = h - 1;
            int px = (int) ((float) frame / FRAMES_TOTAL * (w - 1));
            g2.setColor(BLUE);
            g2.fillRect(px, h - py - 1, 1, py);
            g2.setColor(RED);
            g2.fillRect(px, h - py1 - 225, 1, py1);
            g2.setColor(GREEN);
            g2.fillRect(px, h - py2 - 450, 1, py2);
        }
    }

    private void drawScene(BufferedImage image) throws IOException {
        Graphics2D g2 = image.createGraphics();
        g2.setColor(BG);
        g2.fillRect(0, 0, w, h);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (Grass a : grass) {
            g2.setColor(Grass.COLOR[a.type]);
            g2.fillOval((int) (a.x - GRASS_RADIUS * a.size), (int) (a.y - GRASS_RADIUS * a.size), (int)(GRASS_RADIUS * a.size), (int)(GRASS_RADIUS * a.size));
        }
        float bacteriaScale = BACTERIA_RADIUS * 0.01f;
        for (Bacterium a : bacteria) {
            float sw = sprites[a.type].getWidth() * 0.5f * bacteriaScale;
            float sh = sprites[a.type].getHeight() * 0.5f * bacteriaScale;
            AffineTransform trans = new AffineTransform();
            trans.setTransform(IDENTITY);
            trans.translate(a.x - sw, a.y - sh);
            trans.rotate(a.rotation + Math.PI / 2, sw, sh);
            trans.scale(a.food/20, a.food/20);
            g2.drawImage(sprites[a.type], trans, this);
        }
    }

    private void logic() {
        for (Bacterium a : bacteria) {
            a.x += a.sx;
            a.y += a.sy;
            a.sx *= a.slip;
            a.sy *= a.slip;
            if(a.x < 0) {
                a.sx += 5;
            }
            else if(a.x > w) {
                a.sx -= 5;
            }
            if(a.y < 0) {
                a.sy += 5;
            }
            else if(a.y > h) {
                a.sy -= 5;
            }
            double targetAngle = Math.atan2(a.ty, a.tx);
            float rotationForMotion;
            if (targetAngle < 0) targetAngle += (float)(Math.PI * 2.0);
            if ((Math.abs(a.rotation - targetAngle) < a.rotationSpeed) || (Math.abs(a.rotation - targetAngle) > Math.PI * 2 - a.rotationSpeed)) {
                a.rotation = (float)targetAngle;
            }
            else if (((a.rotation < targetAngle) && (a.rotation + 3.1415f > targetAngle)) || ((a.rotation > targetAngle) && (a.rotation - 3.1415f > targetAngle))) {
                a.rotation += a.rotationSpeed;
            }
            else {
                a.rotation -= a.rotationSpeed;
            }
            if(a.rotation < 0) a.rotation += (float)(Math.PI * 2.0);
            else if(a.rotation > Math.PI * 2.0) a.rotation -= (float)(Math.PI * 2.0);
            rotationForMotion = a.rotation;
            if(a.tx * a.tx + a.ty * a.ty > 1) {
                a.sx += (float)Math.cos(rotationForMotion) * a.speed;
                a.sy += (float)Math.sin(rotationForMotion) * a.speed;
            }
            boolean rand = Math.random() < 0.5;
            if (a.type == 0) {
                Grass closestGrass = null;
                float minGrassDist = a.sightDistance * a.sightDistance;
                for (Grass f : grass) {
                    if (f.toBeDeleted) continue;
                    float dist2 = (a.x - f.x) * (a.x - f.x) + (a.y - f.y) * (a.y - f.y);
                    if (dist2 < minGrassDist) {
                        minGrassDist = dist2;
                        closestGrass = f;
                    }
                }
                if (closestGrass != null) {
                    a.tx = closestGrass.x - a.x;
                    a.ty = closestGrass.y - a.y;
                    if (minGrassDist < GRASS_RADIUS * GRASS_RADIUS + BACTERIA_RADIUS * BACTERIA_RADIUS) {
                        closestGrass.toBeDeleted = true;
                        a.food+=closestGrass.size;
                    }
                }
                else {
                    if(Math.random() < a.directionChangeRate) {
                        double randomAngle = Math.random() * Math.PI * 2;
                        a.tx = (float)Math.cos(randomAngle) * 2;
                        a.ty = (float)Math.sin(randomAngle) * 2;
                    }
                }
            } else if (a.type == 1) {
                Bacterium closestBacterium = null;
                float minGrassDist = a.sightDistance * a.sightDistance;
                for (Bacterium b : bacteria) {
                    if (b.getToBeDeleted()) continue;
                    if (b.type == 1) continue;
                    float dist2 = (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
                    if (dist2 < minGrassDist) {
                        minGrassDist = dist2;
                        closestBacterium = b;
                    }
                }
                if (closestBacterium != null) {
                    a.tx = closestBacterium.x - a.x;
                    a.ty = closestBacterium.y - a.y;
                    if (minGrassDist < BACTERIA_RADIUS * BACTERIA_RADIUS + BACTERIA_RADIUS * BACTERIA_RADIUS) {
                        if (rand) {
                            closestBacterium.setToBeDeleted();
                            a.food += closestBacterium.food * 0.2f;
                        }
                    }
                }
                else {
                    if(Math.random() < a.directionChangeRate) {
                        double randomAngle = Math.random() * Math.PI * 2;
                        a.tx = (float)Math.cos(randomAngle) * 2;
                        a.ty = (float)Math.sin(randomAngle) * 2;
                    }
                }
            } else if (a.type == 2) {
                Grass closestGrass = null;
                Bacterium closestBacterium = null;
                float minGrassDist = a.sightDistance * a.sightDistance;
                int targetType = -1;
                for (Grass f : grass) {
                    if (f.toBeDeleted) continue;
                    float dist2 = (a.x - f.x) * (a.x - f.x) + (a.y - f.y) * (a.y - f.y);
                    if (dist2 < minGrassDist) {
                        minGrassDist = dist2;
                        closestGrass = f;
                        targetType = 0;
                    }
                }
                for (Bacterium b : bacteria) {
                    if (b.getToBeDeleted()) continue;
                    if (b.type == 2) continue;
                    float dist2 = (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
                    if (dist2 < minGrassDist) {
                        minGrassDist = dist2;
                        closestBacterium = b;
                        targetType = 1;
                    }
                }
                if (closestGrass != null || closestBacterium != null) {
                    if (targetType == 0) {
                        a.tx = closestGrass.x - a.x;
                        a.ty = closestGrass.y - a.y;
                        if (minGrassDist < GRASS_RADIUS * GRASS_RADIUS + BACTERIA_RADIUS * BACTERIA_RADIUS) {
                            closestGrass.toBeDeleted = true;
                            a.food+=closestGrass.size;
                        }
                    }
                    else {
                        a.tx = closestBacterium.x - a.x;
                        a.ty = closestBacterium.y - a.y;
                        if (minGrassDist < BACTERIA_RADIUS * BACTERIA_RADIUS + BACTERIA_RADIUS * BACTERIA_RADIUS) {
                            if (!rand) {
                                closestBacterium.setToBeDeleted();
                                a.food += closestBacterium.food * 0.2f;
                            }
                        }
                    }
                }
                else {
                    if(Math.random() < a.directionChangeRate) {
                        double randomAngle = Math.random() * Math.PI * 2;
                        a.tx = (float)Math.cos(randomAngle) * 2;
                        a.ty = (float)Math.sin(randomAngle) * 2;
                    }
                }
            }
        }
        for (int i = 0; i < bacteria.size(); i++) {
            Bacterium a = bacteria.get(i);
            a.create(bacteria);
            if(a.food <= 0) {
                a.setToBeDeleted();
            }
            else {
                if(a.age % 200 == 199) {
                    a.food -= 0.2f;
                }
                a.age++;
            }
            if(a.getToBeDeleted()) {
                bacteria.remove(i);
                i--;
            }
        }
        for (int i = 0; i < grass.size(); i++) {
            if(grass.get(i).toBeDeleted) {
                grass.remove(i);
                i--;
            }
        }
        if(frame % 15 == 0) {
            Grass a = new Grass((float)(Math.random() * (w - 100) + 50), (float)(Math.random() * (h - 100) + 50), (float)(4*Math.random() + 0.5));
            grass.add(a);
        }
        frame++;
    }

}