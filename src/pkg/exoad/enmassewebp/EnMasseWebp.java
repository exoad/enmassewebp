// Software created by Jack Meng (AKA exoad). Licensed by the included "LICENSE" file. If this file is not found, the project is fully copyrighted.

package pkg.exoad.enmassewebp;

import java.util.Enumeration;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;

import com.formdev.flatlaf.intellijthemes.FlatMonocaiIJTheme;
import com.formdev.flatlaf.util.SystemInfo;

import pkg.exoad.enmassewebp.ux.ui_App;

public final class EnMasseWebp
{
  public static final long __VERSION__ = 2023_08_14L;

  static
  {
    System.setProperty("sun.java2d.opengl", "True");
    System.setProperty("sun.java2d.trace", "count");
    if (SystemInfo.isLinux || SystemInfo.isWindows_10_orLater)
    {
      System.setProperty("flatlaf.useWindowDecorations", "true");
      System.setProperty("flatlaf.menuBarEmbedded", "true");
      JFrame.setDefaultLookAndFeelDecorated(true);
    }
    else if (SystemInfo.isMacOS)
      System.setProperty("apple.awt.application.appearance", "system");
    try
    {
      UIManager.setLookAndFeel(new FlatMonocaiIJTheme());
      UIManager.put("ScrollBar.showButtons", false);
      UIManager.put("JScrollPane.smoothScrolling", true);
      UIManager.put("SplitPaneDivider.gripDotCount", 4);
      UIManager.put("Button.arc", 10);
      UIManager.put("Component.arc", 10);
      UIManager.put("ProgressBar.arc", 15);

      for (Font f : new Font[] {
          Font.createFont(Font.TRUETYPE_FONT, _1const.assets.file("assets/font/FiraSans-Bold.ttf")).deriveFont(14F),
          Font.createFont(Font.TRUETYPE_FONT, _1const.assets.file("assets/font/FiraSans-BoldItalic.ttf"))
              .deriveFont(14F),
          Font.createFont(Font.TRUETYPE_FONT, _1const.assets.file("assets/font/FiraSans-Italic.ttf")).deriveFont(14F),
          Font.createFont(Font.TRUETYPE_FONT, _1const.assets.file("assets/font/FiraSans-Regular.ttf"))
              .deriveFont(14F) })
      {
        Enumeration< ? > keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements())
        {
          Object key = keys.nextElement();
          Object value = UIManager.get(key);
          if (value instanceof FontUIResource orig)
          {
            Font font = new Font(f.getFontName(), orig.getStyle(), f.getSize());
            UIManager.put(key, new FontUIResource(font));
          }
        }
      }
    } catch (FontFormatException | IOException | UnsupportedLookAndFeelException e)
    {
      e.printStackTrace();
    }

    System.setOut(new PrintStream(new ui_App.use_TextOutStream()));
  }

  public static void main(String... args)
  {
    SwingUtilities.invokeLater(new ui_App());
  }
}