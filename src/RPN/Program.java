package RPN;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Stack;

public class Program extends JFrame {

    // data
    LinkedList<String> u = new LinkedList<>();
    LinkedList<String> v = new LinkedList<>();

    private JPanel contentPane;
    private JTextField inputPostfixTxt;
    private JTextField assignTxt;

    private JTable table;
    private JTextField resultTxt;
    private JScrollPane scrollPane;
    private JPanel panel;

    private void infixToPostfix(InFix infix) {

        LinkedList<String> postfix = new LinkedList<>();
        Stack<String> operator = new Stack<>();
        String popped;
        String stackStr = "", postfixStr = "";

        for (int i = 0; i < infix.getTokens().size(); i++) {

            String s = infix.getTokens().get(i);

            if (!PostFix.isOperator(s))
                postfix.add(s);
            else if (s.equals(")"))
                while (!(popped = operator.pop()).equals("("))
                    postfix.add(popped);
            else {
                while (!operator.isEmpty() && !s.equals("(") && PostFix.precedence(operator.peek()) >= PostFix.precedence(s))
                    postfix.add(operator.pop());
                operator.push(s);
            }
            for (int x = 0; x < operator.size(); x++)
                stackStr += operator.get(x) + " ";
            for (int x = 0; x < postfix.size(); x++)
                postfixStr += postfix.get(x) + " ";
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.addRow(new Object[]{s, stackStr, postfixStr});
            stackStr = "";
            postfixStr = "";
        }
        while (!operator.isEmpty())
            postfix.add("" + operator.pop());
        for (int x = 0; x < operator.size(); x++)
            stackStr += operator.get(x) + " ";
        for (int x = 0; x < postfix.size(); x++)
            postfixStr += postfix.get(x) + " ";
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(new Object[]{"", stackStr, postfixStr});
    }

    private boolean isNumeric(String strNum) {
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }


    private void assignStandard(String assign) {

        u = new LinkedList<>();
        v = new LinkedList<>();
        String tempStr = "";
        //Các token postfix cách nhau bởi khoảng trắng
        for (int i = 0; i < assign.length(); i++) {

            char c = assign.charAt(i);

            if (c != '=' && c != ',' && c != ';' && c != '(' && c != ')') {

                tempStr += c;
            } else {

                if (!tempStr.isEmpty()) {

                    if (!isNumeric(tempStr))
                        u.add(tempStr);
                    else if (isNumeric(tempStr))
                        v.add(tempStr);
                }
                tempStr = "";
            }
            if (i == assign.length() - 1)
                if (!tempStr.isEmpty()) {

                    if (!isNumeric(tempStr))
                        u.add(tempStr);
                    else if (isNumeric(tempStr))
                        v.add(tempStr);
                }
        }
    }

    private void assign(Operand o) {

        for (int t = 0; t < u.size(); t++) {
            if (o.getValue().equals(u.get(t))) {
                o.setValue(v.get(t));
            }
        }
    }

    private void changeResult(String assign, PostFix postfix) {

        assignStandard(assign);
        Stack<String> result = new Stack<>();
        for (int i = 0; i < postfix.getTokens().size(); i++) {

            String s = postfix.getTokens().get(i);
            //Nếu toán tử là phép toán 2 ngôi
            if (PostFix.precedence(s) == 2 || PostFix.precedence(s) == 3) {

                if (result.size() < 2) break;
                Operand b = new Operand(result.pop());
                assign(b);
                Operand a = new Operand(result.pop());
                assign(a);
                result.add(Operand.evaluate(a, b, s));
            }
            //Nếu toán tử là phép toán 1 ngôi
            else if (PostFix.precedence(s) == 4) {

                if (result.size() < 1) break;
                Operand b = new Operand(result.pop());
                assign(b);
                result.add(Operand.evaluate(b, s));
            } else result.add(s);
        }
        try {
            resultTxt.setText(result.pop());
        } catch (Exception e) {
            resultTxt.setText("NaN");
        }
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Program frame = new Program();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the frame.
     */
    public Program() {

        try {

            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {

                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {

            // If Nimbus is not available, you can set the GUI to another look and feel.
        }

        // window's title
        setTitle("RPN");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1072, 600);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
        gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_contentPane.columnWeights = new double[]{1.0, 1.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
        gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
        contentPane.setLayout(gbl_contentPane);

        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 2;
        gbc_lblNewLabel.gridy = 0;
        contentPane.add(new JLabel("InFix Expression:"), gbc_lblNewLabel);

        JTextField inputInfixTxt = new JTextField();
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.gridwidth = 2;
        gbc_textField.insets = new Insets(0, 0, 5, 5);
        gbc_textField.gridx = 2;
        gbc_textField.gridy = 1;
        contentPane.add(inputInfixTxt, gbc_textField);
        inputInfixTxt.setColumns(20);

        // TODO calculate button
        JButton btnInfixToPostfix = new JButton("InFix to PostFix");
        btnInfixToPostfix.addActionListener((e) -> {
            InFix infix = new InFix(inputInfixTxt.getText());
            PostFix postfix = new PostFix(infix);
            inputInfixTxt.setText(infix.getStr());
            inputPostfixTxt.setText(postfix.getStr());
            resultTxt.setText(postfix.getResult());

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            infixToPostfix(infix);

            String s = new String(assignTxt.getText().replace(" ", ""));
            if (!s.equals("")) changeResult(s, postfix);
        });

        GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
        gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
        gbc_btnNewButton.gridx = 4;
        gbc_btnNewButton.gridy = 1;
        contentPane.add(btnInfixToPostfix, gbc_btnNewButton);

        panel = new JPanel();
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridwidth = 3;
        gbc_panel.insets = new Insets(0, 0, 5, 0);
        gbc_panel.gridx = 2;
        gbc_panel.gridy = 2;
        contentPane.add(panel, gbc_panel);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_panel.rowHeights = new int[]{0, 0, 0};
        gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        panel.setLayout(gbl_panel);

        JButton btnAbs = new JButton("abs");
        btnAbs.addActionListener(e -> {
            inputInfixTxt.requestFocus();
            try {
                inputInfixTxt.getDocument().insertString(inputInfixTxt.getCaretPosition(), "abs()", null);
            } catch (BadLocationException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            inputInfixTxt.setCaretPosition(inputInfixTxt.getCaretPosition() - 1);
        });

        JButton button = new JButton("+");
        button.addActionListener(e -> {
            inputInfixTxt.requestFocus();
            try {
                inputInfixTxt.getDocument().insertString(inputInfixTxt.getCaretPosition(), "+", null);
            } catch (BadLocationException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            inputInfixTxt.setCaretPosition(inputInfixTxt.getCaretPosition());
        });
        GridBagConstraints gbc_button = new GridBagConstraints();
        gbc_button.insets = new Insets(0, 0, 5, 5);
        gbc_button.gridx = 0;
        gbc_button.gridy = 0;
        panel.add(button, gbc_button);

        JButton button_1 = new JButton("-");
        button_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputInfixTxt.requestFocus();
                try {
                    inputInfixTxt.getDocument().insertString(inputInfixTxt.getCaretPosition(), "-", null);
                } catch (BadLocationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                inputInfixTxt.setCaretPosition(inputInfixTxt.getCaretPosition());
            }
        });
        GridBagConstraints gbc_button_1 = new GridBagConstraints();
        gbc_button_1.insets = new Insets(0, 0, 5, 5);
        gbc_button_1.gridx = 1;
        gbc_button_1.gridy = 0;
        panel.add(button_1, gbc_button_1);

        JButton button_2 = new JButton("*");
        button_2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputInfixTxt.requestFocus();
                try {
                    inputInfixTxt.getDocument().insertString(inputInfixTxt.getCaretPosition(), "*", null);
                } catch (BadLocationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                inputInfixTxt.setCaretPosition(inputInfixTxt.getCaretPosition());
            }
        });
        GridBagConstraints gbc_button_2 = new GridBagConstraints();
        gbc_button_2.insets = new Insets(0, 0, 5, 5);
        gbc_button_2.gridx = 2;
        gbc_button_2.gridy = 0;
        panel.add(button_2, gbc_button_2);

        JButton button_3 = new JButton("/");
        button_3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputInfixTxt.requestFocus();
                try {
                    inputInfixTxt.getDocument().insertString(inputInfixTxt.getCaretPosition(), "/", null);
                } catch (BadLocationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                inputInfixTxt.setCaretPosition(inputInfixTxt.getCaretPosition());
            }
        });
        GridBagConstraints gbc_button_3 = new GridBagConstraints();
        gbc_button_3.insets = new Insets(0, 0, 5, 5);
        gbc_button_3.gridx = 3;
        gbc_button_3.gridy = 0;
        panel.add(button_3, gbc_button_3);

        JButton button_4 = new JButton("^");
        button_4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputInfixTxt.requestFocus();
                try {
                    inputInfixTxt.getDocument().insertString(inputInfixTxt.getCaretPosition(), "^", null);
                } catch (BadLocationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                inputInfixTxt.setCaretPosition(inputInfixTxt.getCaretPosition());
            }
        });
        GridBagConstraints gbc_button_4 = new GridBagConstraints();
        gbc_button_4.insets = new Insets(0, 0, 5, 5);
        gbc_button_4.gridx = 4;
        gbc_button_4.gridy = 0;
        panel.add(button_4, gbc_button_4);

        JButton button_5 = new JButton("( )");
        button_5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputInfixTxt.requestFocus();
                try {
                    inputInfixTxt.getDocument().insertString(inputInfixTxt.getCaretPosition(), "()", null);
                } catch (BadLocationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                inputInfixTxt.setCaretPosition(inputInfixTxt.getCaretPosition() - 1);
            }
        });
        GridBagConstraints gbc_button_5 = new GridBagConstraints();
        gbc_button_5.insets = new Insets(0, 0, 5, 5);
        gbc_button_5.gridx = 5;
        gbc_button_5.gridy = 0;
        panel.add(button_5, gbc_button_5);
        GridBagConstraints gbc_btnAbs = new GridBagConstraints();
        gbc_btnAbs.insets = new Insets(0, 0, 0, 5);
        gbc_btnAbs.gridx = 0;
        gbc_btnAbs.gridy = 1;
        panel.add(btnAbs, gbc_btnAbs);

        JButton btnSqrt = new JButton("sqrt");
        btnSqrt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputInfixTxt.requestFocus();
                try {
                    inputInfixTxt.getDocument().insertString(inputInfixTxt.getCaretPosition(), "sqrt()", null);
                } catch (BadLocationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                inputInfixTxt.setCaretPosition(inputInfixTxt.getCaretPosition() - 1);
            }
        });
        GridBagConstraints gbc_btnSqrt = new GridBagConstraints();
        gbc_btnSqrt.insets = new Insets(0, 0, 0, 5);
        gbc_btnSqrt.gridx = 1;
        gbc_btnSqrt.gridy = 1;
        panel.add(btnSqrt, gbc_btnSqrt);

        JButton btnSin = new JButton("sin");
        btnSin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputInfixTxt.requestFocus();
                try {
                    inputInfixTxt.getDocument().insertString(inputInfixTxt.getCaretPosition(), "sin()", null);
                } catch (BadLocationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                inputInfixTxt.setCaretPosition(inputInfixTxt.getCaretPosition() - 1);
            }
        });
        GridBagConstraints gbc_btnSin = new GridBagConstraints();
        gbc_btnSin.insets = new Insets(0, 0, 0, 5);
        gbc_btnSin.gridx = 2;
        gbc_btnSin.gridy = 1;
        panel.add(btnSin, gbc_btnSin);

        JButton btnCos = new JButton("cos");
        btnCos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputInfixTxt.requestFocus();
                try {
                    inputInfixTxt.getDocument().insertString(inputInfixTxt.getCaretPosition(), "cos()", null);
                } catch (BadLocationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                inputInfixTxt.setCaretPosition(inputInfixTxt.getCaretPosition() - 1);
            }
        });
        GridBagConstraints gbc_btnCos = new GridBagConstraints();
        gbc_btnCos.insets = new Insets(0, 0, 0, 5);
        gbc_btnCos.gridx = 3;
        gbc_btnCos.gridy = 1;
        panel.add(btnCos, gbc_btnCos);

        JButton btnTan = new JButton("tan");
        btnTan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputInfixTxt.requestFocus();
                try {
                    inputInfixTxt.getDocument().insertString(inputInfixTxt.getCaretPosition(), "tan()", null);
                } catch (BadLocationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                inputInfixTxt.setCaretPosition(inputInfixTxt.getCaretPosition() - 1);
            }
        });
        GridBagConstraints gbc_btnTan = new GridBagConstraints();
        gbc_btnTan.insets = new Insets(0, 0, 0, 5);
        gbc_btnTan.gridx = 4;
        gbc_btnTan.gridy = 1;
        panel.add(btnTan, gbc_btnTan);

        JButton btnLn = new JButton("ln");
        btnLn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputInfixTxt.requestFocus();
                try {
                    inputInfixTxt.getDocument().insertString(inputInfixTxt.getCaretPosition(), "ln()", null);
                } catch (BadLocationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                inputInfixTxt.setCaretPosition(inputInfixTxt.getCaretPosition() - 1);
            }
        });
        GridBagConstraints gbc_btnLn = new GridBagConstraints();
        gbc_btnLn.insets = new Insets(0, 0, 0, 5);
        gbc_btnLn.gridx = 5;
        gbc_btnLn.gridy = 1;
        panel.add(btnLn, gbc_btnLn);

        JButton btnLog = new JButton("log");
        btnLog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputInfixTxt.requestFocus();
                try {
                    inputInfixTxt.getDocument().insertString(inputInfixTxt.getCaretPosition(), "log()", null);
                } catch (BadLocationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                inputInfixTxt.setCaretPosition(inputInfixTxt.getCaretPosition() - 1);
            }
        });
        GridBagConstraints gbc_btnLog = new GridBagConstraints();
        gbc_btnLog.insets = new Insets(0, 0, 0, 5);
        gbc_btnLog.gridx = 6;
        gbc_btnLog.gridy = 1;
        panel.add(btnLog, gbc_btnLog);

        JButton btnExp = new JButton("exp");
        btnExp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputInfixTxt.requestFocus();
                try {
                    inputInfixTxt.getDocument().insertString(inputInfixTxt.getCaretPosition(), "exp()", null);
                } catch (BadLocationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                inputInfixTxt.setCaretPosition(inputInfixTxt.getCaretPosition() - 1);
            }
        });
        GridBagConstraints gbc_btnExp = new GridBagConstraints();
        gbc_btnExp.gridx = 7;
        gbc_btnExp.gridy = 1;
        panel.add(btnExp, gbc_btnExp);

        GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
        gbc_lblNewLabel_3.anchor = GridBagConstraints.WEST;
        gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel_3.gridx = 2;
        gbc_lblNewLabel_3.gridy = 3;
        contentPane.add(new JLabel("Assign:"), gbc_lblNewLabel_3);

        assignTxt = new JTextField();
        GridBagConstraints gbc_textField_3 = new GridBagConstraints();
        gbc_textField_3.insets = new Insets(0, 0, 5, 5);
        gbc_textField_3.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField_3.gridx = 3;
        gbc_textField_3.gridy = 3;
        contentPane.add(assignTxt, gbc_textField_3);
        assignTxt.setColumns(10);

        GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
        gbc_lblNewLabel_4.anchor = GridBagConstraints.WEST;
        gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel_4.gridx = 3;
        gbc_lblNewLabel_4.gridy = 4;
        contentPane.add(new JLabel("Ex: a=1, b=2, .."), gbc_lblNewLabel_4);

        GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
        gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
        gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel_1.gridx = 2;
        gbc_lblNewLabel_1.gridy = 5;
        contentPane.add(new JLabel("PostFix Expression:"), gbc_lblNewLabel_1);

        JButton btnPostfixToInfix = new JButton("PostFix to InFix");
        btnPostfixToInfix.addActionListener(e -> {
            PostFix postfix = new PostFix(inputPostfixTxt.getText().trim());
            InFix infix = new InFix(postfix);
            inputInfixTxt.setText(infix.getStr());
            inputPostfixTxt.setText(postfix.getStr());
            resultTxt.setText(postfix.getResult());

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
        });

        inputPostfixTxt = new JTextField();
        GridBagConstraints gbc_textField_1 = new GridBagConstraints();
        gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField_1.gridwidth = 2;
        gbc_textField_1.insets = new Insets(0, 0, 5, 5);
        gbc_textField_1.gridx = 2;
        gbc_textField_1.gridy = 6;
        contentPane.add(inputPostfixTxt, gbc_textField_1);
        inputPostfixTxt.setColumns(10);
        GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
        gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 0);
        gbc_btnNewButton_1.gridx = 4;
        gbc_btnNewButton_1.gridy = 6;
        contentPane.add(btnPostfixToInfix, gbc_btnNewButton_1);

        GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
        gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
        gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel_2.gridx = 2;
        gbc_lblNewLabel_2.gridy = 7;
        contentPane.add(new JLabel("Result:"), gbc_lblNewLabel_2);

        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputInfixTxt.setText("");
                inputPostfixTxt.setText("");
                resultTxt.setText("");
                assignTxt.setText("");
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);
            }
        });

        resultTxt = new JTextField();
        GridBagConstraints gbc_textField_2 = new GridBagConstraints();
        gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField_2.gridwidth = 2;
        gbc_textField_2.insets = new Insets(0, 0, 5, 5);
        gbc_textField_2.gridx = 2;
        gbc_textField_2.gridy = 8;
        contentPane.add(resultTxt, gbc_textField_2);
        resultTxt.setColumns(10);
        GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
        gbc_btnNewButton_2.insets = new Insets(0, 0, 5, 0);
        gbc_btnNewButton_2.gridx = 4;
        gbc_btnNewButton_2.gridy = 8;
        contentPane.add(btnReset, gbc_btnNewButton_2);

        table = new JTable();
        table.setFillsViewportHeight(true);
        table.setShowHorizontalLines(false);
        table.setColumnSelectionAllowed(true);
        table.setCellSelectionEnabled(true);
        table.setModel(new DefaultTableModel(
                new Object[][]{
                },
                new String[]{
                        "Token", "Stack", "Postfix"
                }
        ) {
            /**
             *
             */
            private static final long serialVersionUID = 1L;
            boolean[] columnEditables = new boolean[]{
                    false, false, false
            };

            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        });
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(250);

        scrollPane = new JScrollPane(table);
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.gridwidth = 2;
        gbc_scrollPane.gridheight = 10;
        gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 0;
        contentPane.add(scrollPane, gbc_scrollPane);

        JPanel panel_1 = new JPanel();
        panel_1.add(new JLabel(new ImageIcon(Program.class.getResource("/RPN/gif-in-java-4.gif"))));
        GridBagConstraints gbc_panel_1 = new GridBagConstraints();
        gbc_panel_1.anchor = GridBagConstraints.SOUTHEAST;
        gbc_panel_1.gridwidth = 3;
        gbc_panel_1.insets = new Insets(0, 0, 0, 5);
        gbc_panel_1.gridx = 2;
        gbc_panel_1.gridy = 9;
        contentPane.add(panel_1, gbc_panel_1);


    }
}
