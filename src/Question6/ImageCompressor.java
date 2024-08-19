package Question6;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ImageCompressor {

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
                ImageCompressor window = new ImageCompressor();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ImageCompressor() {
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
        } else {
            statusArea.append("No files selected.\n");
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
                        // Compress and save the image
                        compressImage(file);
                        publish(new ImageConversionProgress(file.getName(), i + 1, totalFiles, "Completed"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        publish(new ImageConversionProgress(file.getName(), i + 1, totalFiles, "Error"));
                    }

                    int progress = (int) (((i + 1) / (double) totalFiles) * 100);
                    setProgress(progress);
                }
                return null;
            }

            @Override
            protected void process(List<ImageConversionProgress> chunks) {
                for (ImageConversionProgress progress : chunks) {
                    statusArea.append(String.format("Image: %s, %s\n", progress.fileName, progress.status));
                }
                overallProgressBar.setValue(getProgress());
            }

            @Override
            protected void done() {
                try {
                    get(); // Ensure that any exception during processing is thrown
                    statusArea.append("All conversions completed.\n");
                } catch (InterruptedException e) {
                    statusArea.append("Conversion interrupted.\n");
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    statusArea.append("Conversion failed.\n");
                    e.getCause().printStackTrace();
                } finally {
                    startButton.setEnabled(true);
                    cancelButton.setEnabled(false);
                }
            }
        };

        worker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                overallProgressBar.setValue((Integer) evt.getNewValue());
            }
        });

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



    private void compressImage(File inputFile) throws IOException {
        if (!inputFile.exists()) {
            throw new IOException("File does not exist: " + inputFile.getAbsolutePath());
        }

        // Check if the file has a supported extension
        String fileName = inputFile.getName().toLowerCase();
        if (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg")) {
            throw new IOException("Unsupported file format: " + fileName);
        }

        try {
            BufferedImage inputImage = ImageIO.read(inputFile);
            if (inputImage == null) {
                throw new IOException("Failed to load image: " + fileName);
            }

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            if (!writers.hasNext()) {
                throw new IOException("No writers found for jpg format");
            }
            ImageWriter writer = writers.next();

            // Ensure the output file has the correct extension
            String outputFileName = fileName.endsWith(".jpg") ? fileName : fileName.replace(".jpeg", ".jpg");
            File outputFile = new File(OUTPUT_DIR + outputFileName);
            ImageOutputStream outputStream = ImageIO.createImageOutputStream(outputFile);
            writer.setOutput(outputStream);

            ImageWriteParam params = writer.getDefaultWriteParam();
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionQuality(0.5f); // Adjust the quality level as needed

            writer.write(null, new IIOImage(inputImage, null, null), params);

            outputStream.close();
            writer.dispose();
        } catch (IOException e) {
            statusArea.append("Error compressing image: " + inputFile.getName() + "\n");
            e.printStackTrace(); // Print the stack trace for debugging
            throw e; // Rethrow to let the SwingWorker handle it
        }
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
