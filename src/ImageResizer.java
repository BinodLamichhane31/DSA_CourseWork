import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ImageResizer {

    private final JFrame frame;
    private final JProgressBar overallProgressBar;
    private final JTextArea statusArea;
    private final JButton startButton;
    private final JButton cancelButton;
    private final JFileChooser fileChooser;
    private File[] selectedFiles;
    private SwingWorker<Void, ImageConversionProgress> worker;
    private static final String OUTPUT_DIR = "converted_images/";

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ImageResizer window = new ImageResizer();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ImageResizer() {
        frame = new JFrame();
        frame.setTitle("Image Converter");
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.add(panel, BorderLayout.CENTER);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "png", "jpg", "jpeg"));
        JButton selectFilesButton = new JButton("Select Images");
        selectFilesButton.addActionListener(e -> selectFiles());
        panel.add(selectFilesButton);

        startButton = new JButton("Start Conversion");
        startButton.addActionListener(e -> startConversion());
        panel.add(startButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(e -> cancelConversion());
        panel.add(cancelButton);

        overallProgressBar = new JProgressBar(0, 100);
        overallProgressBar.setStringPainted(true);
        panel.add(new JLabel("Overall Progress:"));
        panel.add(overallProgressBar);

        statusArea = new JTextArea();
        statusArea.setEditable(false);
        panel.add(new JScrollPane(statusArea));

        frame.setVisible(true);
    }

    private void selectFiles() {
        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFiles = fileChooser.getSelectedFiles();
            statusArea.append("Selected images:\n");
            for (File file : selectedFiles) {
                statusArea.append(file.getAbsolutePath() + "\n");
            }
        }
    }

    private void startConversion() {
        if (selectedFiles == null || selectedFiles.length == 0) {
            JOptionPane.showMessageDialog(frame, "No images selected!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        startButton.setEnabled(false);
        cancelButton.setEnabled(true);
        overallProgressBar.setValue(0);
        statusArea.append("Starting conversion...\n");

        new File(OUTPUT_DIR).mkdirs();

        worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                int totalFiles = selectedFiles.length;
                for (int i = 0; i < totalFiles; i++) {
                    if (isCancelled()) {
                        return null;
                    }

                    File file = selectedFiles[i];
                    publish(new ImageConversionProgress(file.getName(), i + 1, totalFiles, "Processing"));

                    try {
                        // Load image
                        BufferedImage image = ImageIO.read(file);

                        // Convert image (resize example)
                        BufferedImage resizedImage = resizeImage(image, 800, 600); // Resize to 800x600

                        // Save converted image
                        File outputFile = new File(OUTPUT_DIR + file.getName());
                        ImageIO.write(resizedImage, "png", outputFile);

                        publish(new ImageConversionProgress(file.getName(), i + 1, totalFiles, "Completed"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        publish(new ImageConversionProgress(file.getName(), i + 1, totalFiles, "Error"));
                    }
                }
                return null;
            }

            @Override
            protected void process(List<ImageConversionProgress> chunks) {
                for (ImageConversionProgress progress : chunks) {
                    statusArea.append(String.format("Image: %s, %s\n", progress.fileName, progress.status));
                }
                // Update overall progress bar
                int progress = (int) ((getProgress() / 100.0) * 100);
                overallProgressBar.setValue(progress);
            }

            @Override
            protected void done() {
                try {
                    get(); // Ensure that any exception during processing is thrown
                    statusArea.append("All conversions completed.\n");
                } catch (InterruptedException | ExecutionException e) {
                    statusArea.append("Conversion interrupted or failed.\n");
                } finally {
                    startButton.setEnabled(true);
                    cancelButton.setEnabled(false);
                }
            }
        };

        worker.execute();
    }

    private void cancelConversion() {
        if (worker != null) {
            worker.cancel(true);
            statusArea.append("Conversion cancelled.\n");
        }
        startButton.setEnabled(true);
        cancelButton.setEnabled(false);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image tmp = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resizedImage;
    }

    private static class ImageConversionProgress {
        String fileName;
        int currentFile;
        int totalFiles;
        String status;

        ImageConversionProgress(String fileName, int currentFile, int totalFiles, String status) {
            this.fileName = fileName;
            this.currentFile = currentFile;
            this.totalFiles = totalFiles;
            this.status = status;
        }
    }
}
