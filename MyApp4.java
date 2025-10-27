import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class MyApp4 extends JFrame {
    private MazePanel mazePanel;
    private Image mazeImage;

    public MyApp4() {
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
        maze.openAndExit();
        maze.draw(g, margin, margin);
        mazePanel.setImage(mazeImage);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MyApp4 app = new MyApp4();
            app.setVisible(true);
            app.drawMaze();
        });
    }
}

class MazePanel extends JPanel {
    private Image image;
    public void setImage(Image image) { this.image = image; }
    public Image getImage() { return image; }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (image != null) g.drawImage(image, 0, 0, this);
    }
}

enum Direction { North, East, South, West }

class MapSide {
    private int positionX;
    private int positionY;
    public static int LENGTH = 30;
    public int getX() { return positionX; }
    public int getY() { return positionY; }
    public void setX(int x) { this.positionX = x; }
    public void setY(int y) { this.positionY = y; }
    public void draw(Image image) { }
}

class Wall extends MapSide {
    private Direction direction;
    private boolean hasDoor = false;

    public Wall(Direction direction) { this.direction = direction; }
    public void setDoor(boolean door) { this.hasDoor = door; }

    @Override
    public void draw(Image image) {
        super.draw(image);
        if (hasDoor) return;
        Graphics g = image.getGraphics();
        g.setColor(Color.BLACK);
        int startX = getX();
        int startY = getY();
        int endX = startX + MapSide.LENGTH;
        int endY = startY + MapSide.LENGTH;

        switch (direction) {
            case North: g.drawLine(startX, startY, endX, startY); break;
            case South: g.drawLine(startX, endY, endX, endY); break;
            case East:  g.drawLine(endX, startY, endX, endY); break;
            case West:  g.drawLine(startX, startY, startX, endY); break;
        }
    }
}

class Room {
    int columnIndex, rowIndex;
    boolean hasNorthWall = true, hasEastWall = true, hasSouthWall = true, hasWestWall = true;
    boolean visited = false;
    Room(int column, int row) { columnIndex = column; rowIndex = row; }
    void removeWall(Direction direction) {
        switch (direction) {
            case North: hasNorthWall = false; break;
            case South: hasSouthWall = false; break;
            case East:  hasEastWall = false; break;
            case West:  hasWestWall = false; break;
        }
    }
}

class Maze {
    private int numberOfColumns, numberOfRows;
    private Room[][] rooms;
    private Random random = new Random();

    Maze(int numberOfColumns, int numberOfRows) {
        this.numberOfColumns = numberOfColumns;
        this.numberOfRows = numberOfRows;
        rooms = new Room[numberOfColumns][numberOfRows];
        for (int col = 0; col < numberOfColumns; col++)
            for (int row = 0; row < numberOfRows; row++)
                rooms[col][row] = new Room(col, row);
    }

    Room getNeighbor(Room room, Direction direction) {
        int neighborColumn = room.columnIndex;
        int neighborRow = room.rowIndex;
        switch (direction) {
            case North: neighborRow--; break;
            case South: neighborRow++; break;
            case East:  neighborColumn++; break;
            case West:  neighborColumn--; break;
        }
        if (neighborColumn < 0 || neighborRow < 0 || neighborColumn >= numberOfColumns || neighborRow >= numberOfRows)
            return null;
        return rooms[neighborColumn][neighborRow];
    }

    void generate() {
        Stack<Room> stack = new Stack<>();
        Room startRoom = rooms[0][0];
        startRoom.visited = true;
        stack.push(startRoom);

        while (!stack.isEmpty()) {
            Room currentRoom = stack.peek();
            java.util.List<Direction> availableDirections = new ArrayList<>();
            for (Direction direction : Direction.values()) {
                Room neighbor = getNeighbor(currentRoom, direction);
                if (neighbor != null && !neighbor.visited)
                    availableDirections.add(direction);
            }
            if (!availableDirections.isEmpty()) {
                Direction chosenDirection = availableDirections.get(random.nextInt(availableDirections.size()));
                Room nextRoom = getNeighbor(currentRoom, chosenDirection);
                currentRoom.removeWall(chosenDirection);
                nextRoom.removeWall(getOpposite(chosenDirection));
                nextRoom.visited = true;
                stack.push(nextRoom);
            } else {
                stack.pop();
            }
        }
    }

    void openAndExit() {
        rooms[0][0].hasWestWall = false;
        rooms[numberOfColumns - 1][numberOfRows - 1].hasEastWall = false;
    }

    Direction getOpposite(Direction direction) {
        switch (direction) {
            case North: return Direction.South;
            case South: return Direction.North;
            case East:  return Direction.West;
            case West:  return Direction.East;
        }
        return Direction.North;
    }

    void draw(Graphics g, int offsetX, int offsetY) {
        g.setColor(Color.BLACK);
        for (int col = 0; col < numberOfColumns; col++) {
            for (int row = 0; row < numberOfRows; row++) {
                Room room = rooms[col][row];
                int x = offsetX + col * MapSide.LENGTH;
                int y = offsetY + row * MapSide.LENGTH;
                if (room.hasNorthWall) g.drawLine(x, y, x + MapSide.LENGTH, y);
                if (room.hasSouthWall) g.drawLine(x, y + MapSide.LENGTH, x + MapSide.LENGTH, y + MapSide.LENGTH);
                if (room.hasWestWall)  g.drawLine(x, y, x, y + MapSide.LENGTH);
                if (room.hasEastWall)  g.drawLine(x + MapSide.LENGTH, y, x + MapSide.LENGTH, y + MapSide.LENGTH);
            }
        }
    }
}

