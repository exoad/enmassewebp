package pkg.exoad.enmassewebp.ux;

import java.io.IOException;
import java.io.OutputStream;

public class use_TextOutStream
    extends OutputStream
{
  @Override public void write(byte[] buffer, int offset, int length)
  {
    String content = new String(buffer, offset, length);
    System.err.print(content.replaceAll("<[^>]*>", ""));
    stx_Helper.print(content);
  }

  @Override public void write(int b) throws IOException
  {
    write(new byte[] { (byte) b }, 0, 1);
  }
}