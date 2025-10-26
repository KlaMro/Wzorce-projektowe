import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

class CalculatorModel {
    double pierwsza;
    double druga;
    String operator;

    public void setPierwsza(double pierwsza) { this.pierwsza = pierwsza; }
    public void setDruga(double druga) { this.druga = druga; }
    public void setOperator(String operator) { this.operator = operator; }
    public String getOperator() { return operator; }
    public double calculate() throws ArithmeticException {
        switch (operator) {
            case "+": return pierwsza + druga;
            case "-": return pierwsza - druga;
            case "*": return pierwsza * druga;
            case "/":
                if (druga == 0) throw new ArithmeticException("Nie dziel przez zero");
                return pierwsza / druga;
            default: throw new IllegalArgumentException("Nieznany operator");
        }
    }
}

interface IView {
    void setText(String data);
}

class CalculatorView extends JFrame implements IView {
    JLabel historia = new JLabel(" ");
    JButton button_0 = new JButton("0");
    JButton button_1 = new JButton("1");
    JButton button_2 = new JButton("2");
    JButton button_3 = new JButton("3");
    JButton button_4 = new JButton("4");
    JButton button_5 = new JButton("5");
    JButton button_6 = new JButton("6");
    JButton button_7 = new JButton("7");
    JButton button_8 = new JButton("8");
    JButton button_9 = new JButton("9");
    JButton button_add = new JButton("+");
    JButton button_subtract = new JButton("-");
    JButton button_multiply = new JButton("*");
    JButton button_divide = new JButton("/");
    JButton button_equal = new JButton("=");
    JButton button_C = new JButton("C");
    JButton button_backspace = new JButton("<-");
    CalculatorGraphics graphicsPanel = new CalculatorGraphics();

    public CalculatorView() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(historia, BorderLayout.NORTH);
        topPanel.add(graphicsPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JPanel panelButtons = new JPanel(new GridLayout(4, 4, 5, 5));
        panelButtons.add(button_7);
        panelButtons.add(button_8);
        panelButtons.add(button_9);
        panelButtons.add(button_divide);
        panelButtons.add(button_4);
        panelButtons.add(button_5);
        panelButtons.add(button_6);
        panelButtons.add(button_multiply);
        panelButtons.add(button_3);
        panelButtons.add(button_2);
        panelButtons.add(button_1);
        panelButtons.add(button_subtract);
        panelButtons.add(button_C);
        panelButtons.add(button_0);
        panelButtons.add(button_backspace);
        panelButtons.add(button_add);

        add(panelButtons, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(button_equal, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
    }

    public void setScreenText(String text) {
        graphicsPanel.setText(text);
    }
    public void appendToScreen(String text) {
        graphicsPanel.setText(graphicsPanel.getText() + text);
        }
    public String getScreenText() {
        return graphicsPanel.getText();
    }
    public void setHistoria(String text) {
        historia.setText(text);
    }
    public void clear() {
        historia.setText(" "); graphicsPanel.setText("0");
    }

    @Override
    public void setText(String data) {
        graphicsPanel.setText(data);
    }
}

class MyGraphicsView extends JPanel implements IView {
    private String text = "";

    public MyGraphicsView() {
        setPreferredSize(new Dimension(250, 80));
        setBackground(Color.WHITE);
    }

    @Override
    public void setText(String data) {
        this.text = data;
        repaint();
    }

    public String getText() {
        return text;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        double value;
        try { value = Double.parseDouble(text); }
        catch (NumberFormatException e) { value = 0.0; }

        String formatted = String.format("%06.2f", value);
        String[] parts = formatted.split("\\.");
        String integerPart = parts[0];
        String fractionalPart = parts.length > 1 ? "." + parts[1] : "";

        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font("Monospaced", Font.BOLD, 36));
        int y = getHeight() / 2 + 12;
        int x = 25;
        g2d.setColor(Color.BLUE);
        g2d.drawString(integerPart, x, y);
        int intWidth = g2d.getFontMetrics().stringWidth(integerPart);
        int dotWidth = g2d.getFontMetrics().stringWidth(".");
        g2d.setColor(Color.BLACK);
        g2d.drawString(".", x + intWidth, y);
        g2d.setColor(Color.RED);
        g2d.drawString(fractionalPart.substring(1), x + intWidth + dotWidth, y);
    }
}



class CalculatorController {
    private final CalculatorModel model;
    private final CalculatorView view;
    private boolean newInput = true;

    public CalculatorController(CalculatorModel model, CalculatorView view) {
        this.model = model;
        this.view = view;

        ActionListener numberListener = e -> {
            if (newInput) { view.setScreenText(""); newInput = false; }
            view.appendToScreen(((JButton)e.getSource()).getText());
        };
        view.button_0.addActionListener(numberListener);
        view.button_1.addActionListener(numberListener);
        view.button_2.addActionListener(numberListener);
        view.button_3.addActionListener(numberListener);
        view.button_4.addActionListener(numberListener);
        view.button_5.addActionListener(numberListener);
        view.button_6.addActionListener(numberListener);
        view.button_7.addActionListener(numberListener);
        view.button_8.addActionListener(numberListener);
        view.button_9.addActionListener(numberListener);

        ActionListener operatorListener = e -> {
            String text = view.getScreenText();
            if (!text.isEmpty()) {
                try {
                    model.setPierwsza(Double.parseDouble(text));
                    model.setOperator(((JButton)e.getSource()).getText());
                    newInput = true;
                } catch (NumberFormatException ex) {
                    view.setScreenText("Błąd");
                }
            }
        };
        view.button_add.addActionListener(operatorListener);
        view.button_subtract.addActionListener(operatorListener);
        view.button_multiply.addActionListener(operatorListener);
        view.button_divide.addActionListener(operatorListener);

        view.button_equal.addActionListener(e -> {
            String text = view.getScreenText();
            if (!text.isEmpty() && model.getOperator() != null) {
                try {
                    model.setDruga(Double.parseDouble(text));
                    double wynik = model.calculate();
                    wynik = Math.round(wynik * 100.0) / 100.0;
                    view.setHistoria(model.pierwsza + " " + model.operator + " " + model.druga + " =");
                    view.setScreenText(String.valueOf(wynik));
                    model.operator = "";
                    newInput = true;
                } catch (ArithmeticException ex) {
                    view.setScreenText("Błąd: " + ex.getMessage());
                } catch (NumberFormatException ex) {
                    view.setScreenText("Błąd");
                }
            }
        });

        view.button_C.addActionListener(e -> {
            view.clear();
            model.pierwsza = 0;
            model.druga = 0;
            model.operator = "";
        });

        view.button_backspace.addActionListener(e -> {
            String text = view.getScreenText();
            if (!text.isEmpty()) view.setScreenText(text.substring(0, text.length() - 1));
        });
    }
}

public class MyApp3 {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            CalculatorModel model = new CalculatorModel();
            CalculatorView view = new CalculatorView();
            new CalculatorController(model, view);
            view.setVisible(true);
        });
    }
}

