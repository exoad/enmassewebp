package pkg.exoad.enmassewebp.ux;

import java.awt.Desktop;
import java.awt.FlowLayout;
import java.net.URI;

import javax.swing.JPanel;

import pkg.exoad.enmassewebp._1const;

public class ui_Socials
    extends
    JPanel
{
  public ui_Socials()
  {
    setLayout(new FlowLayout(FlowLayout.LEFT, 5, 4));
    add(stx_Helper.make(_1const.assets.image_icon("assets/github-logo.png"), "<html>Visit this project on <strong>GitHub!</strong></html>", () -> Desktop.getDesktop().browse(new URI())))
  }
}
