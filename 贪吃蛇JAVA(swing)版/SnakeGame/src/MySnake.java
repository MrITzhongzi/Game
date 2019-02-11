import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Random;

public class MySnake {
    public MySnake() {

    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        MySnake mySnake = new MySnake();
        mySnake.init(jFrame, 900, 700);

        final MySnakePanel mySnakePanel = new MySnakePanel();
        mySnakePanel.initMap();
        mySnakePanel.initSnake();
        mySnakePanel.createFood();
        jFrame.add(mySnakePanel);
        jFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                int keycode = e.getKeyCode();

                switch (keycode) {
                    case KeyEvent.VK_LEFT:
                        mySnakePanel.setDirection(MySnakePanel.LEFT);
                        break;
                    case KeyEvent.VK_RIGHT:
                        mySnakePanel.setDirection(MySnakePanel.RIGHT);
                        break;
                    case KeyEvent.VK_UP:
                        mySnakePanel.setDirection(MySnakePanel.UP);
                        break;
                    case KeyEvent.VK_DOWN:
                        mySnakePanel.setDirection(MySnakePanel.DOWN);
                        break;
                    default:
                        break;
                }

            }
        });

        new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkCrash(mySnakePanel);
            }
        }).start();
    }

    /**
     * 碰撞检测
     */
    public static void checkCrash(MySnakePanel mySnakePanel) {
        Point snakeHead = mySnakePanel.snake.getFirst();
        final int X_WIDTH = 40;
        final int Y_HEIGHT = 30;

        if (snakeHead.x == X_WIDTH - 2 || snakeHead.x == 1 || snakeHead.y == Y_HEIGHT - 2 || snakeHead.y == 1) {
            String message = "GameOver!";
            JOptionPane.showMessageDialog(mySnakePanel, message);
            System.exit(0);
        } else if(checkCrashSelf(mySnakePanel)){
            String message = "GameOver!";
            JOptionPane.showMessageDialog(mySnakePanel, message);
            System.exit(0);
        } else {
            mySnakePanel.move();
        }
    }

    public static boolean checkCrashSelf(MySnakePanel mySnakePanel) {
        Point snakeHead = mySnakePanel.snake.getFirst();
        LinkedList snake = mySnakePanel.snake;
        if (mySnakePanel.currentDirection == MySnakePanel.LEFT && snake.contains(new Point(snakeHead.x - 1, snakeHead.y))) {
            return true;
        }
        if (mySnakePanel.currentDirection == MySnakePanel.RIGHT && snake.contains(new Point(snakeHead.x + 1, snakeHead.y))) {
            return true;
        }
        if (mySnakePanel.currentDirection == MySnakePanel.UP && snake.contains(new Point(snakeHead.x, snakeHead.y - 1))) {
            return true;
        }
        if (mySnakePanel.currentDirection == MySnakePanel.DOWN && snake.contains(new Point(snakeHead.x, snakeHead.y + 1))) {
            return true;
        }
        return false;
    }

    /***
     * 设置窗口的属性，并在屏幕中居中显示
     * @param jf
     * @param formWidth
     * @param formHeight
     */
    public void init(JFrame jf, int formWidth, int formHeight) {
        jf.setVisible(true);
        jf.setSize(formWidth, formHeight);
        jf.setTitle("我的贪吃蛇");
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = dimension.width;
        int screenHeight = dimension.height;

        System.out.println("当前屏幕的分辨率为：" + screenWidth + "*" + screenHeight);

        int x = (screenWidth - formWidth) / 2;
        int y = (screenHeight - formHeight) / 2;
        jf.setLocation(x, y);

        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class MySnakePanel extends JPanel {

    public static final long serialVersionUID = 1L;

    public static final int X_WIDTH = 40;
    public static final int Y_HEIGHT = 30;
    boolean[][] map = new boolean[Y_HEIGHT][X_WIDTH];
    LinkedList<Point> snake = new LinkedList<>();
    Point food; // 食物

    //四个方向
    public static final int LEFT = 1;
    public static final int RIGHT = -1;
    public static final int UP = 2;
    public static final int DOWN = -2;

    int currentDirection = RIGHT;

    //单元格的宽高
    int cell_width = 20;
    int cell_height = 20;

    void initMap() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (i == 0 || i == map.length - 1 || j == 0 || j == map[i].length - 1) {
                    map[i][j] = true;
                }
            }
        }
    }

    void initSnake() {
        int x = X_WIDTH / 2 - 1;
        int y = Y_HEIGHT / 2 - 1;
        for (int i = 0; i < 3; i++) {
            snake.add(new Point(x - i, y));
        }
    }

    public void createFood() {
        Random random = new Random();
        while (true) {
            int x = random.nextInt(X_WIDTH);
            int y = random.nextInt(Y_HEIGHT);
            System.out.println("x: " + x + "y: " + y);
            if (!map[y][x]) {
                food = new Point(x, y);
                break;
            }
        }
    }

    public void setDirection(int newDirection) {
        System.out.println("new Direction" + newDirection);
        if (currentDirection + newDirection == 0) {
            String messge = "初级玩家不能玩180度掉头";
            System.out.println(messge);
            JOptionPane.showMessageDialog(this, messge, "警告", JOptionPane.WARNING_MESSAGE);
        } else {
            currentDirection = newDirection;
        }
    }

    public void move() {
        System.out.println("———move———");
        Point snakeHead = snake.getFirst();
        switch (currentDirection) {
            case LEFT:
                snake.addFirst(new Point(snakeHead.x - 1, snakeHead.y));
                break;
            case RIGHT:
                snake.addFirst(new Point(snakeHead.x + 1, snakeHead.y));
                break;
            case UP:
                snake.addFirst(new Point(snakeHead.x, snakeHead.y - 1));
                break;
            case DOWN:
                snake.addFirst(new Point(snakeHead.x, snakeHead.y + 1));
                break;
            default:
                break;
        }
        if (eatFood()) {
            createFood();
        } else {
            snake.removeLast();
        }

        repaint();
    }

    public boolean eatFood() {
        Point snakeHead = snake.getFirst();
        boolean isEat = false;
        if (snakeHead.x == food.x && snakeHead.y == food.y) {
            isEat = true;
        }

        return isEat;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j]) {
                    g.setColor(Color.DARK_GRAY);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.fill3DRect(j * cell_width, i * cell_height, cell_width, cell_height, true);
            }
        }

        Point snakeHead = snake.getFirst();
        g.setColor(Color.RED);
        g.fill3DRect(snakeHead.x * cell_width, snakeHead.y * cell_height, cell_width, cell_height, true);

        g.setColor(Color.green);
        for (int i = 1; i < snake.size(); i++) {
            Point snakeBody = snake.get(i);
            g.fill3DRect(snakeBody.x * cell_width, snakeBody.y * cell_height, cell_width, cell_height, true);
        }

        g.setColor(Color.PINK);
        g.fill3DRect(food.x * cell_width, food.y * cell_height, cell_width, cell_height, true);
    }
}
