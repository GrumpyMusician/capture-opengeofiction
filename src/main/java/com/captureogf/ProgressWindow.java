
package com.captureogf;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class ProgressWindow extends JPanel {

    private final JProgressBar progressBar;
    private final JLabel progressLabel;
    private final JTextArea taskOutput;

    private final Timer dotTimer;
    private String animatedText;
    private int animatedLineStart;
    private int dotCount = 1;

    public ProgressWindow() {
        super(new BorderLayout(5, 5));

        progressLabel = new JLabel("");
        progressLabel.setHorizontalAlignment(SwingConstants.LEFT);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        JPanel progressPanel = new JPanel(new BorderLayout(0, 3));
        progressPanel.add(progressLabel, BorderLayout.NORTH);
        progressPanel.add(progressBar, BorderLayout.CENTER);

        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5, 5, 5, 5));
        taskOutput.setEditable(false);
        taskOutput.setLineWrap(true);
        taskOutput.setWrapStyleWord(true);

        add(progressPanel, BorderLayout.NORTH);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        dotTimer = new Timer(400, e -> animateDots());
    }

    public void setLabel(String text) {
        progressLabel.setText(text);
    }

    public void progress(int progress) {
        progressBar.setValue(progress);
    }

    public void progress(int progress, int maximum) {
        progressBar.setMaximum(maximum);
        progressBar.setValue(progress);
    }

    public void notify(String text){
        addOutput(text, true);
    }

    public void report(String text) {
        addOutput(text + ".", false);
    }

    public void buffer(){
        addOutput("", false);
    }

    private void addOutput(String text, boolean animate) {
        finishAnimation();

        if (animate) {
            animatedText = text;
            dotCount = 1;
            animatedLineStart = taskOutput.getDocument().getLength();

            taskOutput.append(text + ".\n");
            dotTimer.start();
        } else {
            animatedText = null;
            taskOutput.append(text + "\n");
        }

        taskOutput.setCaretPosition(taskOutput.getDocument().getLength());
    }

    private void animateDots() {
        if (animatedText == null) {
            dotTimer.stop();
            return;
        }

        dotCount = dotCount % 3 + 1;

        try {
            Document doc = taskOutput.getDocument();

            int end = doc.getLength() - 1;
            int length = end - animatedLineStart;

            doc.remove(animatedLineStart, length);
            doc.insertString(
                animatedLineStart,
                animatedText + ".".repeat(dotCount),
                null
            );

            taskOutput.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            dotTimer.stop();
            animatedText = null;
        }
    }

    private void finishAnimation() {
        if (animatedText == null) {
            return;
        }

        dotTimer.stop();

        try {
            Document doc = taskOutput.getDocument();

            int end = doc.getLength() - 1;
            int length = end - animatedLineStart;

            doc.remove(animatedLineStart, length);
            doc.insertString(
                animatedLineStart,
                animatedText + "...",
                null
            );

            taskOutput.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }

        animatedText = null;
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Progress");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new ProgressWindow());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProgressWindow::createAndShowGUI);
    }
}
