package uk.co.lamsey.score5al.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicLabelUI;

import uk.co.lamsey.score5al.model.Team;

/**
 * A custom JLabel with a gradient background and optional text/border scaling.
 */
public class GradientLabel extends JLabel {

	/**
	 * The starting (top) colour for the background gradient.
	 */
	private Color gradientStart;

	/**
	 * The ending (bottom) colour for the background gradient.
	 */
	private Color gradientEnd;

	/**
	 * Creates a new GradientLabel with a default grey background and the
	 * specified alignment and text style.
	 * 
	 * @param horizontalAlignment
	 *            The horizontal alignment as defined in {@link SwingConstants}.
	 * @param fontStyle
	 *            The font style as defined in {@link Font}.
	 */
	public GradientLabel(int horizontalAlignment, int fontStyle) {

		super();
		setHorizontalAlignment(horizontalAlignment);
		setFont(getFont().deriveFont(fontStyle));

		// set a default background color
		setBackground(Color.LIGHT_GRAY);

		// add default padding
		setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
	}

	/**
	 * Sets the background to be a gradient around the specified color.
	 */
	@Override
	public void setBackground(Color color) {
		setGradient(color, true, true);
	}

	/**
	 * Sets the background to be a gradient through the specified colors.
	 */
	public void setGradient(Color color, boolean brighten, boolean darken) {
		gradientStart = brighten ? brighten(color) : color;
		gradientEnd = darken ? darken(color) : color;
		super.setBackground(color);
	}

	/**
	 * Sets up an abbreviation substitution for when the label's text is too
	 * long.
	 * 
	 * @param target
	 *            The portion of the label's text which may be abbreviated.
	 * @param replacement
	 *            The abbreviation which may be substituted for the target text.
	 */
	public void setAbbreviation(String target, String replacement) {
		if (target != null && replacement != null && !target.isEmpty()
				&& !replacement.isEmpty()) {
			setUI(new AbbrevationUI(target, replacement));
		} else {
			setUI(new BasicLabelUI());
		}
	}

	/**
	 * Sets up this label to show the specified Team's name and number using its
	 * colours.
	 * 
	 * @param brighten
	 *            If true, the background gradient will be brightened at the
	 *            top.
	 * @param darken
	 *            If true, the background gradient will be darkened at the
	 *            bottom.
	 */
	public void showTeam(Team team, boolean brighten, boolean darken) {
		if (team != null) {
			setGradient(team.getBgColour(), brighten, darken);
			setForeground(team.getFgColour());
			setAbbreviation(team.getName(), team.getAbbreviation());
			setText(team.getNumber() + " - " + team.getName());
		} else {
			setGradient(new Color(255, 96, 96), brighten, darken);
			setForeground(Color.BLACK);
			setAbbreviation("No team selected", "No team");
			setText("No team selected");
		}
	}

	/**
	 * Scales the label's font to the specified size.
	 */
	public void scaleFont(float textHeight) {
		setFont(getFont().deriveFont(textHeight));
	}

	/**
	 * Paints the gradient background before painting as normal.
	 */
	@Override
	protected void paintComponent(Graphics g) {

		// create a gradient from top to bottom
		int width = getWidth();
		int height = getHeight();
		GradientPaint paint = new GradientPaint(0, 0, gradientStart, 0, height,
				gradientEnd);

		// paint the background
		Graphics2D g2d = (Graphics2D) g;
		g2d.setPaint(paint);
		g2d.fillRect(0, 0, width, height);

		// let the label do the rest
		setOpaque(false);
		super.paintComponent(g);
		setOpaque(true);
	}

	/**
	 * Returns a brighter version of the passed colour.
	 */
	private Color brighten(Color color) {
		return adjustColor(color, 0.2f);
	}

	/**
	 * Returns a darker version of the passed colour.
	 */
	private Color darken(Color color) {
		return adjustColor(color, -0.2f);
	}

	/**
	 * Adjusts the passed colour's brightness by the passed amount.
	 */
	private Color adjustColor(Color color, float amount) {
		float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(),
				color.getBlue(), null);
		hsb[2] = Math.max(Math.min(hsb[2] + amount, 1), 0);
		return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
	}

	/**
	 * A label UI object which can perform predetermined abbreviations.
	 */
	private static class AbbrevationUI extends BasicLabelUI {

		/**
		 * The portion of the label's text which may be abbreviated.
		 */
		private String target;

		/**
		 * The abbreviation which may be substituted for abbrTarget in the
		 * label's text.
		 */
		private String replacement;

		/**
		 * Creates an AbbrevationUI to perform the specified replacement.
		 */
		public AbbrevationUI(String target, String replacement) {
			super();
			this.target = target;
			this.replacement = replacement;
		}

		/**
		 * Override which attempts to use the custom abbreviation before falling
		 * back to JLabel's normal abbreviation.
		 */
		@Override
		protected String layoutCL(JLabel label, FontMetrics fontMetrics,
				String text, Icon icon, Rectangle viewR, Rectangle iconR,
				Rectangle textR) {
			String replaced = super.layoutCL(label, fontMetrics, text, icon,
					viewR, iconR, textR);
			if (!replaced.equals(text)) {
				replaced = super.layoutCL(label, fontMetrics,
						text.replace(target, replacement), icon, viewR, iconR,
						textR);
			}
			return replaced;
		}
	}
}