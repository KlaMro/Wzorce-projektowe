import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class MyApp7 extends JFrame {
    private MazePanel mazePanel;
    private Image mazeImage;
    private MazeFactoryGame mazeGame = new MazeFactoryGame();

    public MyApp7() {
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        mazePanel = new MazePanel();

        JButton generateButton = new JButton("Generuj");
        generateButton.addActionListener(e -> {
            drawMaze();
            mazePanel.repaint();
        });

        JPanel menuPanel = new JPanel(new GridLayout(1, 1));
        menuPanel.add(generateButton);

        add(menuPanel, BorderLayout.NORTH);
        add(mazePanel, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                drawMaze();
                mazePanel.repaint();
            }
        });
    }

    public void drawMaze() {
        int panelWidth = mazePanel.getWidth();
        int panelHeight = mazePanel.getHeight();
        if (panelWidth <= 0 || panelHeight <= 0) return;

        mazeImage = mazePanel.createImage(panelWidth, panelHeight);
        Graphics g = mazeImage.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, panelWidth, panelHeight);

        int margin = 10;
        int cols = Math.max(4, (panelWidth - 2 * margin) / MapSide.LENGTH);
        int rows = Math.max(4, (panelHeight - 2 * margin) / MapSide.LENGTH);

        Maze maze = mazeGame.createMaze(cols, rows);
        maze.draw(g, margin, margin);

        mazePanel.setImage(mazeImage);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MyApp7 app = new MyApp7();
            app.setVisible(true);
            app.drawMaze();
        });
    }
}

class MazePanel extends JPanel {
    private Image image;
    public void setImage(Image image) { this.image = image; }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (image != null) g.drawImage(image, 0, 0, this);
    }
}

enum Direction { North, East, South, West }

abstract class MapSide {
    private int x, y;
    public static int LENGTH = 30;
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public abstract void draw(Graphics g);
}

class Wall extends MapSide {
    private Direction direction;

    public Wall(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        int x = getX();
        int y = getY();
        int L = LENGTH;

        switch (direction) {
            case North -> g.drawLine(x, y, x + L, y);
            case South -> g.drawLine(x, y + L, x + L, y + L);
            case West  -> g.drawLine(x, y, x, y + L);
            case East  -> g.drawLine(x + L, y, x + L, y + L);
        }
    }
}

class Door extends MapSide {
    private Room r1, r2;
    private Direction direction;

    public Door(Room r1, Room r2, Direction direction) {
        this.r1 = r1;
        this.r2 = r2;
        this.direction = direction;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.RED);

        int x = getX();
        int y = getY();
        int L = LENGTH;

        int a = L / 3;
        int b = 2 * L / 3;

        switch (direction) {
            case North -> {
                g.drawLine(x, y, x + a, y);
                g.drawLine(x + b, y, x + L, y);
            }
            case South -> {
                g.drawLine(x, y + L, x + a, y + L);
                g.drawLine(x + b, y + L, x + L, y + L);
            }
            case West -> {
                g.drawLine(x, y, x, y + a);
                g.drawLine(x, y + b, x, y + L);
            }
            case East -> {
                g.drawLine(x + L, y, x + L, y + a);
                g.drawLine(x + L, y + b, x + L, y + L);
            }
        }
    }
}

class Room extends MapSide {
    private MapSide[] sides = new MapSide[4];
    public int col, row;

    public Room(int col, int row) {
        this.col = col;
        this.row = row;
        setX(col * LENGTH);
        setY(row * LENGTH);
    }

    public void setSide(Direction d, MapSide s) {
        sides[d.ordinal()] = s;
        s.setX(getX());
        s.setY(getY());
    }

    public MapSide getSide(Direction d) {
        return sides[d.ordinal()];
    }

    @Override
    public void draw(Graphics g) {
        for (MapSide s : sides)
            if (s != null) s.draw(g);
    }
}

class Maze {
    private Room[][] rooms;
    private int cols, rows;

    public Maze(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        rooms = new Room[cols][rows];
    }

    public void setRoom(int c, int r, Room room) {
        rooms[c][r] = room;
    }

    public Room getRoom(int c, int r) {
        return rooms[c][r];
    }

    public int getCols() { return cols; }
    public int getRows() { return rows; }

    public void draw(Graphics g, int offsetX, int offsetY) {
        g.translate(offsetX, offsetY);

        for (int x = 0; x < cols; x++)
            for (int y = 0; y < rows; y++)
                rooms[x][y].draw(g);

        g.translate(-offsetX, -offsetY);
    }
}

class MazeFactoryGame {
    public Maze makeMaze(int cols, int rows) {
        return new Maze(cols, rows);
    }

    public Room makeRoom(int col, int row) {
        Room r = new Room(col, row);
        r.setSide(Direction.North, makeWall(Direction.North));
        r.setSide(Direction.East,  makeWall(Direction.East));
        r.setSide(Direction.South, makeWall(Direction.South));
        r.setSide(Direction.West,  makeWall(Direction.West));
        return r;
    }

    public Wall makeWall(Direction d) {
        return new Wall(d);
    }

    public Door makeDoor(Room r1, Room r2, Direction d) {
        return new Door(r1, r2, d);
    }

    public Maze createMaze(int cols, int rows) {
        Maze maze = makeMaze(cols, rows);

        for (int c = 0; c < cols; c++)
            for (int r = 0; r < rows; r++)
                maze.setRoom(c, r, makeRoom(c, r));

        generateMaze(maze);
        openEntranceExit(maze);

        return maze;
    }

    private void generateMaze(Maze maze) {
        int cols = maze.getCols();
        int rows = maze.getRows();
        Random rnd = new Random();

        Stack<Room> stack = new Stack<>();
        HashSet<Room> visited = new HashSet<>();

        Room start = maze.getRoom(0, 0);
        visited.add(start);
        stack.push(start);

        while (!stack.isEmpty()) {
            Room curr = stack.peek();

            java.util.List<Direction> dirs = new java.util.ArrayList<>();
            for (Direction d : Direction.values()) {
                Room n = getNeighbor(maze, curr, d);
                if (n != null && !visited.contains(n))
                    dirs.add(d);
            }

            if (!dirs.isEmpty()) {
                Direction d = dirs.get(rnd.nextInt(dirs.size()));
                Room next = getNeighbor(maze, curr, d);

                Door door = makeDoor(curr, next, d);

                curr.setSide(d, door);
                next.setSide(opposite(d), door);

                visited.add(next);
                stack.push(next);
            } else
                stack.pop();
        }
    }

    private Room getNeighbor(Maze m, Room r, Direction d) {
        int x = r.col;
        int y = r.row;
        switch (d) {
            case North -> y--;
            case South -> y++;
            case East  -> x++;
            case West  -> x--;
        }
        if (x < 0 || x >= m.getCols() || y < 0 || y >= m.getRows())
            return null;
        return m.getRoom(x, y);
    }

    private Direction opposite(Direction d) {
        return switch (d) {
            case North -> Direction.South;
            case South -> Direction.North;
            case East  -> Direction.West;
            case West  -> Direction.East;
        };
    }

    private void openEntranceExit(Maze m) {
        Room a = m.getRoom(0, 0);
        Room b = m.getRoom(m.getCols() - 1, m.getRows() - 1);

        a.setSide(Direction.West, makeDoor(a, a, Direction.West));
        b.setSide(Direction.East, makeDoor(b, b, Direction.East));
    }
}

