import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MyApp extends JFrame {
    private JTextField screen;
    private JLabel historia;
    private double pierwsza = 0;
    private double druga = 0;
    private String operator = "";
    private boolean newInput = true;

    public MyApp() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        historia = new JLabel(" ");
        screen = new JTextField();
        screen.setEditable(false);
        screen.setHorizontalAlignment(JTextField.RIGHT);
        topPanel.add(historia, BorderLayout.NORTH);
        topPanel.add(screen, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel panelButtons = new JPanel(new GridLayout(4, 4, 5, 5));
        JButton button_7 = new JButton("7");
        JButton button_8 = new JButton("8");
        JButton button_9 = new JButton("9");
        JButton button_divide = new JButton("/");
        JButton button_4 = new JButton("4");
        JButton button_5 = new JButton("5");
        JButton button_6 = new JButton("6");
        JButton button_multiply = new JButton("*");
        JButton button_3 = new JButton("3");
        JButton button_2 = new JButton("2");
        JButton button_1 = new JButton("1");
        JButton button_subtract = new JButton("-");
        JButton button_C = new JButton("C");
        JButton button_0 = new JButton("0");
        JButton button_backspace = new JButton("<-");
        JButton button_add = new JButton("+");
        JButton button_equal = new JButton("=");

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

        panel.add(panelButtons, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(button_equal, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        ActionListener numberListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton klik = (JButton) e.getSource();
                String wartosc = klik.getText();

                if (newInput) {
                    screen.setText("");
                    newInput = false;
                }
                screen.setText(screen.getText() + wartosc);
            }
        };

        button_0.addActionListener(numberListener);
        button_1.addActionListener(numberListener);
        button_2.addActionListener(numberListener);
        button_3.addActionListener(numberListener);
        button_4.addActionListener(numberListener);
        button_5.addActionListener(numberListener);
        button_6.addActionListener(numberListener);
        button_7.addActionListener(numberListener);
        button_8.addActionListener(numberListener);
        button_9.addActionListener(numberListener);

        ActionListener operatorListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton klik = (JButton) e.getSource();
                String text = screen.getText();
                if (!text.isEmpty()) {
                    try {
                        pierwsza = Double.parseDouble(text);
                        operator = klik.getText();
                        screen.setText("");
                        newInput = false;
                    } catch (NumberFormatException ex) {
                        screen.setText("Błąd");
                    }
                }
            }
        };

        button_add.addActionListener(operatorListener);
        button_subtract.addActionListener(operatorListener);
        button_multiply.addActionListener(operatorListener);
        button_divide.addActionListener(operatorListener);

        button_equal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = screen.getText();
                if (!text.isEmpty() && !operator.isEmpty()) {
                    try {
                        druga = Double.parseDouble(text);
                        double wynik = 0;

                        if (operator.equals("+")) {
                            wynik = pierwsza + druga;
                        } else if (operator.equals("-")) {
                            wynik = pierwsza - druga;
                        } else if (operator.equals("*")) {
                            wynik = pierwsza * druga;
                        } else if (operator.equals("/")) {
                            if (druga == 0) {
                                screen.setText("Błąd");
                                return;
                            }
                            wynik = pierwsza / druga;
                        }
                        wynik = Math.round(wynik * 100000.0) / 100000.0;
                        historia.setText(pierwsza + " " + operator + " " + druga + " =");
                        screen.setText(String.valueOf(wynik));
                        operator = "";
                        newInput = true;
                    } catch (NumberFormatException ex) {
                        screen.setText("Błąd");
                    }
                }
            }
        });

        button_C.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                screen.setText("");
                historia.setText(" ");
                pierwsza = 0;
                druga = 0;
                operator = "";
            }
        });

        button_backspace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = screen.getText();
                if (!text.isEmpty()) {
                    screen.setText(text.substring(0, text.length() - 1));
                }
            }
        });
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MyApp();
            }
        });
    }
}

