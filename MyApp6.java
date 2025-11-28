import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class MyApp6 extends JFrame {
    private MazePanel mazePanel;
    private Image mazeImage;

    public MyApp6() {
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

        int numberOfColumns = Math.max(4, (panelWidth - 2 * margin) / MapSide.LENGTH);
        int numberOfRows = Math.max(4, (panelHeight - 2 * margin) / MapSide.LENGTH);

        Maze maze = new Maze(numberOfColumns, numberOfRows);
        maze.generate();
        maze.openEntranceExit();
        maze.draw(g, margin, margin);

        mazePanel.setImage(mazeImage);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MyApp5 app = new MyApp5();
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
    private Room room1;
    private Room room2;
    private boolean isOpen = false;
    private Direction direction;

    public Door(Room r1, Room r2, Direction dir) {
        this.room1 = r1;
        this.room2 = r2;
        this.direction = dir;
        setX(r1.getX());
        setY(r1.getY());
    }

    public void setDoorState(boolean state) {
        isOpen = state;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.RED);

        int x = getX();
        int y = getY();
        int L = LENGTH;

        int gap1 = L / 3;
        int gap2 = 2 * L / 3;

        switch (direction) {
            case North -> {
                g.drawLine(x, y, x + gap1, y);
                g.drawLine(x + gap2, y, x + L, y);
            }
            case South -> {
                g.drawLine(x, y + L, x + gap1, y + L);
                g.drawLine(x + gap2, y + L, x + L, y + L);
            }
            case West -> {
                g.drawLine(x, y, x, y + gap1);
                g.drawLine(x, y + gap2, x, y + L);
            }
            case East -> {
                g.drawLine(x + L, y, x + L, y + gap1);
                g.drawLine(x + L, y + gap2, x + L, y + L);
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

        sides[Direction.North.ordinal()] = new Wall(Direction.North);
        sides[Direction.East.ordinal()]  = new Wall(Direction.East);
        sides[Direction.South.ordinal()] = new Wall(Direction.South);
        sides[Direction.West.ordinal()]  = new Wall(Direction.West);

        for (MapSide ms : sides) {
            ms.setX(getX());
            ms.setY(getY());
        }
    }

    public void setSite(Direction d, MapSide site) {
        sides[d.ordinal()] = site;
        site.setX(getX());
        site.setY(getY());
    }

    public MapSide getSite(Direction d) {
        return sides[d.ordinal()];
    }

    @Override
    public void draw(Graphics g) {
        for (MapSide ms : sides) ms.draw(g);
    }
}

class Maze {
    private Room[][] rooms;
    private int cols, rows;
    private Random rnd = new Random();

    public Maze(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        rooms = new Room[cols][rows];
        for (int x = 0; x < cols; x++)
            for (int y = 0; y < rows; y++)
                rooms[x][y] = new Room(x, y);
    }

    private Room getNeighbor(Room r, Direction d) {
        int nx = r.col;
        int ny = r.row;

        switch (d) {
            case North -> ny--;
            case South -> ny++;
            case East  -> nx++;
            case West  -> nx--;
        }

        if (nx < 0 || nx >= cols || ny < 0 || ny >= rows)
            return null;

        return rooms[nx][ny];
    }

    public void generate() {
        Stack<Room> stack = new Stack<>();
        Room start = rooms[0][0];
        HashSet<Room> visited = new HashSet<>();

        visited.add(start);
        stack.push(start);

        while (!stack.isEmpty()) {
            Room current = stack.peek();

            java.util.List<Direction> options = new java.util.ArrayList<>();
            for (Direction d : Direction.values()) {
                Room n = getNeighbor(current, d);
                if (n != null && !visited.contains(n))
                    options.add(d);
            }

            if (!options.isEmpty()) {
                Direction d = options.get(rnd.nextInt(options.size()));
                Room next = getNeighbor(current, d);

                Door door = new Door(current, next, d);

                current.setSite(d, door);
                next.setSite(opposite(d), door);

                visited.add(next);
                stack.push(next);
            } else {
                stack.pop();
            }
        }
    }

    public void openEntranceExit() {
        rooms[0][0].setSite(Direction.West, new Door(rooms[0][0], rooms[0][0], Direction.West));
        rooms[cols - 1][rows - 1].setSite(Direction.East, new Door(rooms[cols - 1][rows - 1], rooms[cols - 1][rows - 1], Direction.East));
    }

    private Direction opposite(Direction d) {
        return switch (d) {
            case North -> Direction.South;
            case South -> Direction.North;
            case East -> Direction.West;
            case West -> Direction.East;
        };
    }

    public void draw(Graphics g, int offsetX, int offsetY) {
        g.translate(offsetX, offsetY);
        for (int x = 0; x < cols; x++)
            for (int y = 0; y < rows; y++)
                rooms[x][y].draw(g);
        g.translate(-offsetX, -offsetY);
    }
}

