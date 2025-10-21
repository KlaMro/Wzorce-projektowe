import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
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

class CalculatorView extends JFrame {
    JTextField screen = new JTextField();
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

    public CalculatorView() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        screen.setEditable(false);
        screen.setHorizontalAlignment(JTextField.RIGHT);
        topPanel.add(historia, BorderLayout.NORTH);
        topPanel.add(screen, BorderLayout.CENTER);
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

    public void setScreenText(String text) { screen.setText(text); }
    public void appendToScreen(String text) { screen.setText(screen.getText() + text); }
    public String getScreenText() { return screen.getText(); }
    public void setHistoria(String text) { historia.setText(text); }
    public void clear() { screen.setText(""); historia.setText(" "); }
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
                    wynik = Math.round(wynik * 100000.0) / 100000.0;
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

public class MyApp2 {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            CalculatorModel model = new CalculatorModel();
            CalculatorView view = new CalculatorView();
            new CalculatorController(model, view);
            view.setVisible(true);
        });
    }
}
