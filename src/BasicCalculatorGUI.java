import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BasicCalculatorGUI extends JFrame implements ActionListener {
    private JTextField input;
    private JLabel result;
    private JButton button;
    public BasicCalculatorGUI(){
        setTitle("Basic Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400,600);
        setBackground(Color.BLACK);

        input = new JTextField(10);
        input.setPreferredSize(new Dimension(400,100));
        input.setBackground(Color.BLACK);
        input.setForeground(Color.white);
        input.setFont(new Font("Arial",Font.PLAIN,25));

        result = new JLabel("Result");
        result.setPreferredSize(new Dimension(400, 50));
        result.setForeground(Color.WHITE);
        result.setFont(new Font("Arial", Font.PLAIN, 20));

        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setLayout(new GridLayout(5, 4, 5, 5));


        String[] buttons = {
                "AC", "(", ")", "/",
                "7", "8", "9", "X",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                "0",".","⌫","="
        };

        // Add buttons to the panel
        for (String text : buttons) {
            JButton button = new JButton(text);
            setButtonColor(button, text);
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setPreferredSize(new Dimension(30, 30));
            button.addActionListener(this);
            buttonPanel.add(button);
        }

        setLayout(new BorderLayout());
        add(input,BorderLayout.NORTH);
        add(buttonPanel,BorderLayout.CENTER);
        add(result, BorderLayout.AFTER_LAST_LINE);


        setVisible(true);
    }

    private void setButtonColor(JButton button, String text) {
        if (text.equals("+") || text.equals("-") || text.equals("X") || text.equals("/") || text.equals("=") || text.equals("⌫")) {
            button.setBackground(Color.decode("#189AB4"));
        } else if (text.equals("AC") || text.equals("(") || text.equals(")")) {
            button.setBackground(Color.decode("#D4F1F4"));
        } else if (text.matches("[0-9]") || text.equals(".")) {
            button.setBackground(Color.decode("#75E6DA"));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String currText = input.getText();
        String command = e.getActionCommand();

        switch (command) {
            case "AC":
                input.setText("");
                break;
            case "⌫":
                if (!currText.isEmpty()){
                    input.setText(currText.substring(0, currText.length()-1));
                }
                break;
            default:
                input.setText(currText+command);
                break;


        }


    }


    public static void main(String[] args) {
        new BasicCalculatorGUI();
    }
}
