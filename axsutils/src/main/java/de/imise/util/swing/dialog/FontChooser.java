// adapted from salsa.font.FontChooser:

/*
 ** Salsa - Swing Add-On Suite
 ** Copyright (c) 2001, 2002, 2003 by Gerald Bauer
 **
 ** This program is free software.
 **
 ** You may redistribute it and/or modify it under the terms of the GNU
 ** General Public License as published by the Free Software Foundation.
 ** Version 2 of the license should be included with this distribution in
 ** the file LICENSE, as well as License.html. If the license is not
 ** included with this distribution, you may find a copy at the FSF web
 ** site at 'www.gnu.org' or 'www.fsf.org', or you may write to the
 ** Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139 USA.
 **
 ** THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND,
 ** NOT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR
 ** OF THIS SOFTWARE, ASSUMES _NO_ RESPONSIBILITY FOR ANY
 ** CONSEQUENCE RESULTING FROM THE USE, MODIFICATION, OR
 ** REDISTRIBUTION OF THIS SOFTWARE.
 **
 */

package de.imise.util.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import de.imise.util.swing.component.text.ExtendedTextField;

/**
 * Nicht kommentiert.
 * 
 * @author N.N. 
 */
public class FontChooser extends JDialog {

	/**
	 * COMMENTME
	 */
	private JCheckBox _boldCheck;

	//   JComboBox _colorCombo;
	/**
	 * COMMENTME
	 */
	private OpenList _fontNameList;

	/**
	 * COMMENTME
	 */
	private OpenList _fontSizeList;

	/**
	 * COMMENTME
	 */
	private JCheckBox _italicCheck;

	/**
	 * COMMENTME
	 */
	private int _option = JOptionPane.CLOSED_OPTION;

	private JLabel _preview;

	private MutableAttributeSet _styleAttributes;

	//   JCheckBox _strikethroughCheck;
	//   JCheckBox _subscriptCheck;
	//   JCheckBox _superscriptCheck;
	//   JCheckBox _underlineCheck;

	private Font font = null;

	/**
	 * 
	 * @param parent
	 * @param fontNames
	 * @param fontSizes
	 * @param initialFont
	 * @param previewText
	 */
	private FontChooser(JDialog parent, String fontNames[], String fontSizes[], Font initialFont, String previewText) {
		super(parent, true);
		init(fontNames, fontSizes, previewText);
		_fontNameList.setSelected(initialFont.getName());
		_fontSizeList.setSelectedInt(initialFont.getSize());
		_boldCheck.setSelected(initialFont.isBold());
		_italicCheck.setSelected(initialFont.isItalic());
		updatePreview();
	}

	/**
	 * 
	 * @param parent
	 * @param fontNames
	 * @param fontSizes
	 * @param initialFont
	 * @param previewText
	 */
	private FontChooser(JFrame parent, String fontNames[], String fontSizes[], Font initialFont, String previewText) {
		super(parent, true);
		init(fontNames, fontSizes, previewText);
		_fontNameList.setSelected(initialFont.getName());
		_fontSizeList.setSelectedInt(initialFont.getSize());
		_boldCheck.setSelected(initialFont.isBold());
		_italicCheck.setSelected(initialFont.isItalic());
		updatePreview();
	}

	/**
	 * 
	 * @param fontNames
	 * @param fontSizes
	 * @param previewText
	 */
	private void init(String fontNames[], String fontSizes[], String previewText) {

		DialogResourceHandler drh = new DialogResourceHandler(FontChooser.class);

		setTitle(drh.getString("font"));
		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		JPanel p = new JPanel(new GridLayout(1, 2, 10, 2));
		p.setBorder(new TitledBorder(new EtchedBorder(), drh.getString("font")));
		_fontNameList = new OpenList(fontNames, drh.getString("fonttype"));
		p.add(_fontNameList);
		_fontSizeList = new OpenList(fontSizes, drh.getString("fontsize"));
		p.add(_fontSizeList);
		getContentPane().add(p);
		p = new JPanel(new GridLayout(2, 3, 10, 5));
		p.setBorder(new TitledBorder(new EtchedBorder(), drh.getString("fontstyle")));
		_boldCheck = new JCheckBox(drh.getString("bold"));
		p.add(_boldCheck);
		_italicCheck = new JCheckBox(drh.getString("italic"));
		p.add(_italicCheck);

		//      _underlineCheck = new JCheckBox( "Underline" );
		//      p.add( _underlineCheck );

		//      _strikethroughCheck = new JCheckBox( "Strikethrough" );
		//      p.add( _strikethroughCheck );

		//      _subscriptCheck = new JCheckBox( "Subscript" );
		//      p.add( _subscriptCheck );

		//      _superscriptCheck = new JCheckBox( "Superscript" );
		//      p.add( _superscriptCheck );

		getContentPane().add(p);

		getContentPane().add(Box.createVerticalStrut(5));
		/*
		 p = new JPanel();
		 p.setLayout( new BoxLayout( p, BoxLayout.X_AXIS ) );
		 p.add( Box.createHorizontalStrut( 10 ) );
		 p.add( new JLabel( "Color:" ) );
		 p.add( Box.createHorizontalStrut( 20 ) );

		 _colorCombo = new JComboBox();
		 int values[] = new int[]{0, 128, 192, 255};
		 for( int r = 0; r < values.length; r++ )
		 for( int g = 0; g < values.length; g++ )
		 for( int b = 0; b < values.length; b++ )
		 {
		 Color color = new Color( values[r], values[g], values[b] );
		 _colorCombo.addItem( color );
		 }

		 _colorCombo.setRenderer( new ColorListCellRenderer() );
		 p.add( _colorCombo );
		 p.add( Box.createHorizontalStrut( 10 ) );
		 getContentPane().add( p );
		 */

		p = new JPanel(new BorderLayout());
		p.setBorder(new TitledBorder(new EtchedBorder(), drh.getString("preview")));
		_preview = new JLabel("<html><body>" + previewText + "</html></body>", JLabel.CENTER);
		_preview.setBackground(Color.WHITE);
		_preview.setForeground(Color.BLACK);
		_preview.setOpaque(true);
		_preview.setBorder(new LineBorder(Color.BLACK));
		_preview.setPreferredSize(new Dimension(180, 80));
		p.add(_preview, BorderLayout.CENTER);
		getContentPane().add(p);

		JButton okButton = new JButton(drh.getString("ok"));
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				_option = JOptionPane.OK_OPTION;
				setVisible(false);
			}
		});

		JButton cancelButton = new JButton(drh.getString("cancel"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				_option = JOptionPane.CANCEL_OPTION;
				setVisible(false);
			}
		});

		p = new JPanel(new FlowLayout());
		JPanel p1 = new JPanel(new GridLayout(1, 2, 10, 2));
		p1.add(okButton);
		p1.add(cancelButton);
		p.add(p1);
		getContentPane().add(p);

		pack();
		setResizable(false);

		ListSelectionListener listSelListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent ev) {
				updatePreview();
			}
		};

		_fontNameList.addListSelectionListener(listSelListener);
		_fontSizeList.addListSelectionListener(listSelListener);

		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				updatePreview();
			}
		};

		_boldCheck.addActionListener(actionListener);
		_italicCheck.addActionListener(actionListener);

		//      _colorCombo.addActionListener( actionListener );
	}

	public void setAttributes(AttributeSet attr) {
		_styleAttributes = new SimpleAttributeSet(attr);

		String fontName = StyleConstants.getFontFamily(attr);
		_fontNameList.setSelected(fontName);

		int fontSize = StyleConstants.getFontSize(attr);
		_fontSizeList.setSelectedInt(fontSize);

		_boldCheck.setSelected(StyleConstants.isBold(attr));
		_italicCheck.setSelected(StyleConstants.isItalic(attr));
		//      _underlineCheck.setSelected( StyleConstants.isUnderline( attr ) );
		//      _strikethroughCheck.setSelected( StyleConstants.isStrikeThrough( attr ) );
		//      _subscriptCheck.setSelected( StyleConstants.isSubscript( attr ) );
		//      _superscriptCheck.setSelected( StyleConstants.isSuperscript( attr ) );

		//      _colorCombo.setSelectedItem( StyleConstants.getForeground( attr ) );
		updatePreview();
	}

	public AttributeSet getAttributes() {
		if (_styleAttributes == null)
			return null;

		StyleConstants.setFontFamily(_styleAttributes, _fontNameList.getSelected());

		StyleConstants.setFontSize(_styleAttributes, _fontSizeList.getSelectedInt());

		StyleConstants.setBold(_styleAttributes, _boldCheck.isSelected());
		StyleConstants.setItalic(_styleAttributes, _italicCheck.isSelected());
		//      StyleConstants.setUnderline( _styleAttributes, _underlineCheck.isSelected() );
		//      StyleConstants.setStrikeThrough( _styleAttributes, _strikethroughCheck.isSelected() );
		//      StyleConstants.setSubscript( _styleAttributes, _subscriptCheck.isSelected() );
		//      StyleConstants.setSuperscript( _styleAttributes, _superscriptCheck.isSelected() );
		//      StyleConstants.setForeground( _styleAttributes, ( Color ) _colorCombo.getSelectedItem() );

		return _styleAttributes;
	}

	public int getOption() {
		return _option;
	}

	private void updatePreview() {
		String fontName = _fontNameList.getSelected();
		int fontSize = _fontSizeList.getSelectedInt();
		if (fontSize <= 0)
			return;

		int fontStyle = Font.PLAIN;
		if (_boldCheck.isSelected())
			fontStyle |= Font.BOLD;
		if (_italicCheck.isSelected())
			fontStyle |= Font.ITALIC;

		font = new Font(fontName, fontStyle, fontSize);
		_preview.setFont(font);

		//     Color color = ( Color ) _colorCombo.getSelectedItem();
		//     _preview.setForeground( color );
		_preview.repaint();
	}

	/* (non-Javadoc)
	 * @see java.awt.Component#getFont()
	 */
	@Override
	public Font getFont() {
		return font;
	}

	/**
	 * 
	 * @param parent
	 * @param initialFont
	 * @param previewText
	 * @return
	 */
	public static Font chooseFont(JDialog parent, Font initialFont, String previewText) {
		return chooseFont(parent, getSystemFontNames(), getSystemFontSizes(), initialFont, previewText);
	}

	/**
	 * 
	 * @param parent
	 * @param fontNames
	 * @param fontSizes
	 * @param initialFont
	 * @param previewText
	 * @return
	 */
	public static Font chooseFont(JDialog parent, String fontNames[], String fontSizes[], Font initialFont, String previewText) {
		FontChooser fc = new FontChooser(parent, fontNames, fontSizes, initialFont, previewText);
		fc.setVisible(true);
		if (fc.getOption() == JOptionPane.OK_OPTION) {
			return fc.getFont();
		}
		return null;
	}

	/**
	 * 
	 * @param parent
	 * @param initialFont
	 * @param previewText
	 * @return
	 */
	public static Font chooseFont(JFrame parent, Font initialFont, String previewText) {
		return chooseFont(parent, getSystemFontNames(), getSystemFontSizes(), initialFont, previewText);
	}

	/**
	 * 
	 * @param parent
	 * @param fontNames
	 * @param fontSizes
	 * @param initialFont
	 * @param previewText
	 * @return
	 */
	public static Font chooseFont(JFrame parent, String fontNames[], String fontSizes[], Font initialFont, String previewText) {
		FontChooser fc = new FontChooser(parent, fontNames, fontSizes, initialFont, previewText);
		fc.setVisible(true);
		if (fc.getOption() == JOptionPane.OK_OPTION) {
			return fc.getFont();
		}
		return null;
	}

	///////////////////////////////////////////////////////
	// Statische Klassen zum Zugriff auf die Systemfonts //
	///////////////////////////////////////////////////////

	/**
	 * @return All installed system fonts 
	 */
	public static Font[] getSystemFonts() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	}

	/**
	 * @return Names of all installed systems fonts
	 */
	public static String[] getSystemFontNames() {
		Font[] fonts = getSystemFonts();
		String[] retVal = new String[fonts.length];

		for (int i = 0; i < fonts.length; i++) {
			retVal[i] = fonts[i].getName();
		}

		return retVal;
	}

	/**
	 * @return Available sizes for all installed system fonts
	 */
	public static String[] getSystemFontSizes() {
		String[] retVal = { "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "18", "20", "22", "24", "26", "28", "30", "32", "34", "36", "38", "40", "42", "44", "46", "48", "56", "64", "72", "80", "88", "96" };
		return retVal;
	}

	
	/**
	 *  OpenList combines a label, text field and a list.
	 */
	public class OpenList extends JPanel implements ListSelectionListener, ActionListener {
		JList _list;

		JScrollPane _scroll;

		ExtendedTextField _text;

		JLabel _title;

		public OpenList(String data[], String title) {
			setLayout(null);

			_title = new JLabel(title, JLabel.LEFT);
			add(_title);

			_text = new ExtendedTextField();
			_text.addActionListener(this);
			add(_text);

			_list = new JList(data);
			_list.setVisibleRowCount(4);
			_list.addListSelectionListener(this);

			_scroll = new JScrollPane(_list);
			add(_scroll);
		}

		/**
		 * @param value
		 */
		public void setSelected(String value) {
			_list.setSelectedValue(value, true);
			_text.setText(value);
		}

		/**
		 * @param value
		 */
		public void setSelectedInt(int value) {
			setSelected("" + value);
		}

		/* (non-Javadoc)
		 * @see javax.swing.JComponent#getMaximumSize()
		 */
		@Override
		public Dimension getMaximumSize() {
			Insets ins = getInsets();
			Dimension d1 = _title.getMaximumSize();
			Dimension d2 = _text.getMaximumSize();
			Dimension d3 = _scroll.getMaximumSize();

			int w = Math.max(Math.max(d1.width, d2.width), d3.width);
			int h = d1.height + d2.height + d3.height;

			return new Dimension(w + ins.left + ins.right, h + ins.top + ins.bottom);
		}

		/* (non-Javadoc)
		 * @see javax.swing.JComponent#getMinimumSize()
		 */
		@Override
		public Dimension getMinimumSize() {
			Insets ins = getInsets();
			Dimension d1 = _title.getMinimumSize();
			Dimension d2 = _text.getMinimumSize();
			Dimension d3 = _scroll.getMinimumSize();

			int w = Math.max(Math.max(d1.width, d2.width), d3.width);
			int h = d1.height + d2.height + d3.height;

			return new Dimension(w + ins.left + ins.right, h + ins.top + ins.bottom);
		}

		/* (non-Javadoc)
		 * @see javax.swing.JComponent#getPreferredSize()
		 */
		@Override
		public Dimension getPreferredSize() {
			Insets ins = getInsets();
			Dimension d1 = _title.getPreferredSize();
			Dimension d2 = _text.getPreferredSize();
			Dimension d3 = _scroll.getPreferredSize();

			int w = Math.max(Math.max(d1.width, d2.width), d3.width);
			int h = d1.height + d2.height + d3.height;

			return new Dimension(w + ins.left + ins.right, h + ins.top + ins.bottom);
		}

		/**
		 * @return
		 */
		public String getSelected() {
			return _text.getText();
		}

		/**
		 * @return
		 */
		public int getSelectedInt() {
			try {
				return Integer.parseInt(getSelected());
			} catch (NumberFormatException nex) {
				return -1;
			}
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent ev) {
			// sync listbox with textbox

			ListModel model = _list.getModel();
			String key = _text.getText().toLowerCase();
			for (int i = 0; i < model.getSize(); i++) {
				String data = (String) model.getElementAt(i);
				if (data.toLowerCase().startsWith(key)) {
					_list.setSelectedValue(data, true);
					break;
				}
			}
		}

		/**
		 * @param l
		 */
		public void addListSelectionListener(ListSelectionListener l) {
			_list.addListSelectionListener(l);
		}

		/* (non-Javadoc)
		 * @see java.awt.Container#doLayout()
		 */
		@Override
		public void doLayout() {
			Insets ins = getInsets();
			Dimension d = getSize();
			int x = ins.left;
			int y = ins.top;
			int w = d.width - ins.left - ins.right;
			int h = d.height - ins.top - ins.bottom;

			Dimension d1 = _title.getPreferredSize();
			_title.setBounds(x, y, w, d1.height);
			y += d1.height;

			Dimension d2 = _text.getPreferredSize();
			_text.setBounds(x, y, w, d2.height);
			y += d2.height;

			_scroll.setBounds(x, y, w, h - y);
		}

		/* (non-Javadoc)
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		@Override
		public void valueChanged(ListSelectionEvent ev) {
			// sync textbox with listbox
			Object obj = _list.getSelectedValue();
			if (obj != null)
				_text.setText(obj.toString());
		}
	}


}