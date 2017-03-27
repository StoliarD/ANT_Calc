package edu.ant_calc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

/**
 * Created by Dmitry on 20.03.2017.
 */
public class MainFrame extends JFrame {
    private JPanel pluginsPanel;
//    private JCheckBox[] checkBoxArray = {new JCheckBox("Plus"),
//        new JCheckBox("Minus"), new JCheckBox("Multiply"), new JCheckBox("Divide"),new JCheckBox("Sqrt")};
    private DecimalFormat decimalFormat;

    private JButton pluginsButton;
    private JButton clearButton;
    private JButton pointButton;
    private JButton equalsButton;

    private boolean keepInput = true;
    private JLabel res;
    private Solver solver;

    private MainFrame() {
        super("ANT_Calc");

        decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ROOT);
        decimalFormat.applyPattern("##################.###");
        decimalFormat.setDecimalSeparatorAlwaysShown(false);

        solver = new Solver();
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        pluginsButton = new JButton("plugins");
        pluginsButton.addActionListener(defaultListener);
        toolBar.add(pluginsButton);
        clearButton = new JButton("clear");
        clearButton.addActionListener(defaultListener);
        toolBar.add(clearButton);
        topPanel.add(toolBar);
        add(topPanel, BorderLayout.NORTH);

        prepareNumericPanel();

        JPanel bottomPanel = new JPanel(new GridLayout(2,1));
        bottomPanel.removeAll();

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        res = new JLabel("0");
        labelPanel.add(res);
        bottomPanel.add(labelPanel);

        pluginsPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        pluginsPanel.add(new JLabel("PluginPanel"));
        bottomPanel.add(pluginsPanel);
        add(bottomPanel,BorderLayout.SOUTH);

        setLocation(Toolkit.getDefaultToolkit().getScreenSize().width/2-250,
                Toolkit.getDefaultToolkit().getScreenSize().height/2-250);
        pack();
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        clear();
//        System.out.println(SwingUtilities.isEventDispatchThread());
    }

    private void prepareNumericPanel() {
        JPanel numbersPanel = new JPanel(new GridLayout(4,3));
        JButton numericButton;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                numericButton = new JButton("" + (i*3 + j+1));
                numericButton.addActionListener(numberButtonListener);
                numbersPanel.add(numericButton);
            }
        }
        numericButton = new JButton("0");
        numericButton.addActionListener(numberButtonListener);
        numbersPanel.add(numericButton);

        pointButton = new JButton(".");
        pointButton.addActionListener(numberButtonListener);
        numbersPanel.add(pointButton);

        equalsButton = new JButton("=");
        equalsButton.addActionListener(defaultListener);
        numbersPanel.add(equalsButton);

        add(numbersPanel,BorderLayout.CENTER);
    }

    private void equalsButtonMethod() {
        keepInput = false;
        if (!res.getText().equals("Error")) {
            double d = solver.calc(Double.parseDouble(res.getText()));
            solver.clear();
            if (Double.isNaN(d) || Double.isInfinite(d)) {
                res.setText("Error");
            } else {
                res.setText(decimalFormat.format(d));
            }
        }
    }

    private void clear() {
        solver.clear();
        res.setText("0");
        keepInput = false;
    }

    private JCheckBox[] preparePluginList() {
        File folder = new File(solver.getPluginsFolder());
        ArrayList<String> pluginNames = new ArrayList<>();
        for (File plugin : folder.listFiles()) {
            String s = plugin.getName();
            if (s.substring(s.length()-4).equals(".jar")) {
                pluginNames.add(s.substring(0,s.length()-4));
            }
        }
        JCheckBox[] checkBoxes = new JCheckBox[pluginNames.size()];
        for (int i = 0; i < pluginNames.size(); i++) {
            checkBoxes[i] = new JCheckBox(pluginNames.get(i));
        }
        return checkBoxes;
    }

    private ActionListener defaultListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            JCheckBox[] checkboxes = preparePluginList();
            if (button == pluginsButton) {
                JOptionPane.showMessageDialog(null, checkboxes, "choose plugins", JOptionPane.INFORMATION_MESSAGE);
                pluginsPanel.removeAll();
                HashSet<String> pluginNames = new HashSet<>();
                for (JCheckBox checkBox : checkboxes) {
                    if (checkBox.isSelected()) {
                        String pluginName = checkBox.getText();
                        JButton pluginButton = new JButton(pluginName);
                        pluginButton.addActionListener(pluginButtonListener);
                        pluginNames.add(pluginName);
                        pluginsPanel.add(pluginButton);
                    }
                }
                try {
                    solver.defineOperations(pluginNames);
                } catch (Exception ex) {
                    res.setText("Failed to load plugins");
                    ex.printStackTrace();
                }
                MainFrame.this.revalidate();
                MainFrame.this.pack();
            } else if (button == equalsButton) {
                equalsButtonMethod();
            } else if (button == clearButton) {
                clear();
            }
        }
    };

    private ActionListener numberButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            if (button == pointButton) {
                if (!res.getText().contains(".")) {
                    if (keepInput) {
                        res.setText(res.getText()+".");
                    } else {
                        res.setText("0.");
                        keepInput = true;
                    }
                }
            } else {
                if (!keepInput) {
                    res.setText(button.getText());
                    keepInput = true;
                } else {
                    String s = res.getText();
                    if (!(s.contains(".") && s.substring(s.indexOf('.'), s.length()).length() > 3)) {
                        if (s.equals("0")) {
                            res.setText("");
                        }
                        if (keepInput)
                            res.setText(res.getText() + button.getText());
                    }
                }
            }
        }
    };

    private ActionListener pluginButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!res.getText().equals("Error")) {
                JButton button = (JButton) e.getSource();
                equalsButtonMethod();
                if (solver.setOperation(button.getText(), Double.parseDouble(res.getText()))) {
                    equalsButtonMethod();
                }
            }
        }
    };

    public static void main(String[] args) {
//        new MainFrame();
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
