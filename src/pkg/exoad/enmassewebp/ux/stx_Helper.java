// Software created by Jack Meng (AKA exoad). Licensed by the included "LICENSE" file. If this file is not found, the project is fully copyrighted.

package pkg.exoad.enmassewebp.ux;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public final class stx_Helper
{
  public static JButton make(ImageIcon icon, String tooltip, Runnable r)
  {
    JButton button = new JButton(icon);
    button.addActionListener(ev -> r.run());
    button.setBorderPainted(false);
    button.setFocusPainted(false);
    button.setRolloverEnabled(false);
    button.setToolTipText(tooltip);
    button.setBackground(null);
    button.setAlignmentX(Component.CENTER_ALIGNMENT);
    return button;
  }

  public static Image repack(BufferedImage image, int width, int height)
  {
    return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
  }

  public static Runnable browse_safe(String url)
  {
    return () -> {
      try
      {
        Desktop.getDesktop().browse(new URI(url));
      } catch (IOException | URISyntaxException e)
      {
        e.printStackTrace();
      }
    };
  }
}
