// Software created by Jack Meng (AKA exoad). Licensed by the included "LICENSE" file. If this file is not found, the project is fully copyrighted.

package pkg.exoad.enmassewebp.ux;

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
    return button;
  }
}
