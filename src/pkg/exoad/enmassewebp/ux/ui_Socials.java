package pkg.exoad.enmassewebp.ux;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import pkg.exoad.enmassewebp._1const;

public class ui_Socials
    extends
    JPanel
{
  public ui_Socials()
  {
    setPreferredSize(new Dimension(_2const.WIDTH, 40));
    setMaximumSize(getPreferredSize());
    setLayout(new FlowLayout(FlowLayout.CENTER));
    add(stx_Helper.make(new ImageIcon(stx_Helper.repack(_1const.assets.image("assets/github-logo.png"), 20, 20)),
        "<html>Visit this project on <strong>GitHub!</strong></html>",
        stx_Helper.browse_safe("https://github.com/exoad/enmassewebp")));
    add(stx_Helper.make(new ImageIcon(stx_Helper.repack(_1const.assets.image("assets/wikipedia-logo.png"), 20, 20)),
        "<html>Learn what WebP is about</html>",
        stx_Helper.browse_safe("https://en.wikipedia.org/wiki/WebP")));
    add(stx_Helper.make(new ImageIcon(stx_Helper.repack(_1const.assets.image("assets/google-logo.png"), 20, 20)),
        "<html>Google's WebP specifications</html>",
        stx_Helper.browse_safe("https://developers.google.com/speed/webp")));
  }
}
