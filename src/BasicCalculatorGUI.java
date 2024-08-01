import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

public class BasicCalculatorGUI extends JFrame {
    public BasicCalculatorGUI() {
        setTitle("Basic Calculator");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(4, 4));

        JTextField display = new JTextField();
        mainPanel.add(display, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 4, 4));
        JButton calculateButton = new JButton("Calculate");
        JButton backButton = new JButton("⌫");
        JButton clearButton = new JButton("Clear");
        buttonPanel.add(calculateButton);
        buttonPanel.add(backButton);
        buttonPanel.add(clearButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        JLabel resultLabel = new JLabel("Result: ");
        JLabel resultValue = new JLabel("");
        JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        resultPanel.add(resultLabel);
        resultPanel.add(resultValue);
        mainPanel.add(resultPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        pack();
        setVisible(true);

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String infix = display.getText();
                    infix = handleImplicitMultiplication(infix);
                    infix = handleBrackets(infix);
                    String postfix = infixToPostfix(infix);
                    double result = evalPostfix(postfix);
                    resultValue.setText(String.valueOf(result));
                } catch (Exception ex) {
                    resultValue.setText("Error");
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                display.setText("");
                resultValue.setText("");
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String currentText = display.getText();
                if (!currentText.isEmpty()) {
                    display.setText(currentText.substring(0, currentText.length() - 1));
                }
            }
        });
    }

    public static void main(String[] args) {
        new BasicCalculatorGUI();
    }

    private static String handleImplicitMultiplication(String infix) {
        StringBuilder modifiedInfix = new StringBuilder();
        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);
            modifiedInfix.append(c);
            if (i + 1 < infix.length()) {
                char next = infix.charAt(i + 1);
                if (c == ')' && Character.isDigit(next)) {
                    modifiedInfix.append('*');
                }
                if (Character.isDigit(c) && next == '(') {
                    modifiedInfix.append('*');
                }
                if (c == ']' && Character.isDigit(next)) {
                    modifiedInfix.append('*');
                }
                if (Character.isDigit(c) && next == '[') {
                    modifiedInfix.append('*');
                }
                if (c == '}' && Character.isDigit(next)) {
                    modifiedInfix.append('*');
                }
                if (Character.isDigit(c) && next == '{') {
                    modifiedInfix.append('*');
                }
            }
        }
        return modifiedInfix.toString();
    }

    private static String handleBrackets(String infix) {
        infix = infix.replace('[', '(').replace(']', ')');
        infix = infix.replace('{', '(').replace('}', ')');
        return infix;
    }

    public static String infixToPostfix(String infix) {
        StringBuilder postfix = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);

            if (Character.isDigit(c)) {
                postfix.append(c);
                while (i + 1 < infix.length() && Character.isDigit(infix.charAt(i + 1))) {
                    postfix.append(infix.charAt(++i));
                }
                postfix.append(' ');
            } else if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    postfix.append(stack.pop()).append(' ');
                }
                stack.pop();
            } else if (c == ' ') {
                continue;
            } else {
                while (!stack.isEmpty() && precedence(c) <= precedence(stack.peek())) {
                    postfix.append(stack.pop()).append(' ');
                }
                stack.push(c);
            }
        }

        while (!stack.isEmpty()) {
            postfix.append(stack.pop()).append(' ');
        }

        return postfix.toString().trim();
    }

    public static int precedence(char ch) {
        return switch (ch) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            case '^' -> 3;
            default -> -1;
        };
    }

    public static double evalPostfix(String postfix) {
        Stack<Double> stack = new Stack<>();
        String[] postfixChars = postfix.split("\\s+");
        for (String ch : postfixChars) {
            if (ch.isEmpty()) continue;
            char c = ch.charAt(0);

            if (Character.isDigit(c) || (c == '-' && ch.length() > 1)) {
                stack.push(Double.parseDouble(ch));
            }
            else {
                double val1 = stack.pop();
                double val2 = stack.pop();
                switch (c) {
                    case '+':
                        stack.push(val2 + val1);
                        break;
                    case '-':
                        stack.push(val2 - val1);
                        break;
                    case '*':
                        stack.push(val2 * val1);
                        break;
                    case '/':
                        stack.push(val2 / val1);
                        break;
                }
            }
        }
        return stack.pop();
    }
}
