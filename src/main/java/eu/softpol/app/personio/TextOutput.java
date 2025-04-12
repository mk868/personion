package eu.softpol.app.personio;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TextOutput implements Closeable {

  private final FileWriter fileWriter;

  public TextOutput(File file) throws IOException {
    this.fileWriter = new FileWriter(file);
  }

  public void write(String text) throws IOException {
    fileWriter.write(text + "\n");
    fileWriter.flush();
  }


  public void write(int indent, String text) throws IOException {
    write("  ".repeat(indent) + text);
  }


  @Override
  public void close() throws IOException {
    fileWriter.close();
  }
}
