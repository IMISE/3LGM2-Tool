/*
 * Created on 29.06.2004
 */
package de.imise.util.swing.component;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import de.imise.util.swing.component.text.ExtendedTextPane;

/**
 * Eine TextPane, die xml-Elemente hervorhebt.
 * Problematisch ist die langsame Verarbeitung bei langen Texten.
 * @author Sebastian Weber, Robert D. Cameron
 */
public class SimpleXMLTextPane extends ExtendedTextPane {
	MutableAttributeSet tagAttributes, elementAttributes, 
	characterAttributes, cdataAttributes;
	Pattern partPattern, namePattern, attributePattern;
	Matcher partMatcher, nameMatcher, attributeMatcher;
	
	/**
	 * Initialisiert die TextPane mit dem übergebenen Text.
	 * @param xmlText	Text der angezeigt werden soll.
	 */
	public SimpleXMLTextPane(String xmlText) {
		super();
		initAttributeSets();
		initPatterns();
		
		this.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {}
			@Override
			public void removeUpdate(DocumentEvent e) {}
			@Override
			public void insertUpdate(DocumentEvent e) {
				Thread parsen = new Thread(){
					@Override
					public void run(){
						format();
					}
				};
				SwingUtilities.invokeLater(parsen);				
			}
		});
		
		if (xmlText != null) this.setText(xmlText);
	}
	
	/**
	 * Konstruktor.
	 */
	public SimpleXMLTextPane() {
		this(null);
	}
	
	/**
	 * Initialisiert AttributeSets für die unterschiedlichen
	 * syntaktischen Bestandteile der XML-Datei.
	 */
	private void initAttributeSets() {
		tagAttributes = new SimpleAttributeSet();
		StyleConstants.setForeground(tagAttributes, Color.blue);

		cdataAttributes = new SimpleAttributeSet();
		StyleConstants.setFontFamily(cdataAttributes, "Courier");

		characterAttributes = new SimpleAttributeSet();
		StyleConstants.setBold(characterAttributes, true);

		elementAttributes = new SimpleAttributeSet();
		StyleConstants.setForeground(elementAttributes, new Color(153,0,0));
	}

	/**
	 * Initialisiert die XML-Patterns.
	 */
	private void initPatterns() {
		// REX/Javascript 1.0 
		// Robert D. Cameron "REX: XML Shallow Parsing with Regular Expressions",
		// Technical Report TR 1998-17, School of Computing Science, Simon Fraser 
		// University, November, 1998.
		// Copyright (c) 1998, Robert D. Cameron. 
		// The following code may be freely used and distributed provided that
		// this copyright and citation notice remains intact and that modifications
		// or additions are clearly identified.
		
		// Angepaßt an java.util.regex.
		
		String TextSE = "[^<]+";
		String UntilHyphen = "[^-]*-";
		String Until2Hyphens = UntilHyphen + "([^-]" + UntilHyphen + ")*-";
		String CommentCE = Until2Hyphens + ">?";
		String UntilRSBs = "[^]]*]([^]]+])*]+";
		String CDATA_CE = UntilRSBs + "([^]>]" + UntilRSBs + ")*>";
		String S = "[ \\n\\t\\r]+";
		String NameStrt = "[A-Za-z_:]|[^\\x00-\\x7F]";
		String NameChar = "[A-Za-z0-9_:.-]|[^\\x00-\\x7F]";
		String Name = "(" + NameStrt + ")(" + NameChar + ")*";
		String QuoteSE = "\"[^\"]" + "*" + "\"" + "|'[^']*'";
		String DT_IdentSE = S + Name + "(" + S + "(" + Name + "|" + QuoteSE + "))*";
		String MarkupDeclCE = "([^]\"'><]+|" + QuoteSE + ")*>";
		String S1 = "[\\n\\r\\t ]";
		String UntilQMs = "[^?]*\\?+";
		String PI_Tail = "\\?>|" + S1 + UntilQMs + "([^>?]" + UntilQMs + ")*>";
		String DT_ItemSE = "<(!(--" + Until2Hyphens + ">|[^-]" + MarkupDeclCE + ")|\\?" + Name + "(" +
		PI_Tail + "))|%" + Name + ";|" + S;
		String DocTypeCE = DT_IdentSE + "(" + S + ")?(\\[(" + DT_ItemSE + ")*](" + S + ")?)?>?";
		String DeclCE = "--(" + CommentCE + ")?|\\[CDATA\\[(" + CDATA_CE + ")?|DOCTYPE(" + DocTypeCE +
		")?";
		String PI_CE = Name + "(" + PI_Tail + ")?";
		String EndTagCE = Name + "(" + S + ")?>?";
		String AttValSE = "\"[^<\"]" + "*" + "\"" + "|'[^<']*'";
		String ElemTagCE = Name + "(" + S + Name + "(" + S + ")?=(" + S + ")?(" + AttValSE + "))*(" + S
		+ ")?/?>?";
		String MarkupSPE = "<(!(" + DeclCE + ")?|\\?(" + PI_CE + ")?|/(" + EndTagCE + ")?|(" +
		ElemTagCE + ")?)";
		String XML_SPE = TextSE + "|" + MarkupSPE;
		
		partPattern = Pattern.compile(XML_SPE);
		namePattern = Pattern.compile(Name);
		attributePattern = Pattern.compile(AttValSE);
	}
	
	/**
	 * Führt die Formatierung des gesamten Textes durch.
	 * Dies kann bei längeren Texten lange dauern.
	 */
	public void format() {
		String text = getText();
		nameMatcher = namePattern.matcher(text);
		partMatcher = partPattern.matcher(text);
		attributeMatcher = attributePattern.matcher(text);

		try {
			while (partMatcher.find()) {
				formatPart(partMatcher);
			}
		} catch (Exception e) {}
	}
	
	/**
	 * Startet den Formatierungsvorgang.
	 * @param partMatcher	der Matcher, der den Text zerteilt.
	 */
	private void formatPart(Matcher partMatcher) {
		String part = partMatcher.group();
		if (!part.startsWith("<")) {
			applyAttributeSet(partMatcher, characterAttributes);
		}
		else {
			applyAttributeSet(partMatcher, tagAttributes);
			if (!part.startsWith("<?") && !part.startsWith("<!")) {
				formatElement(partMatcher);
			}
			if (part.startsWith("<![CDATA[") && part.endsWith("]]>")) {
				applyAttributeSet(partMatcher, cdataAttributes, 9, 12);
			}
		}
	}
	
	/** 
	 Formatiert Namen und UserField in einem XML-Element.
	 */
	private void formatElement(Matcher partMatcher) {
		// Alle Namen innerhalb des Elements werden gefunden und formatiert
		nameMatcher.find(partMatcher.start());
		do {
			applyAttributeSet(nameMatcher, elementAttributes);
			if (! nameMatcher.find()) break;
		}
		while(nameMatcher.end() < partMatcher.end());
		
		// Alle UserField innerhalb des Elements werden gefunden und formatiert
		if (!attributeMatcher.find(partMatcher.start())) return;
		do {
			applyAttributeSet(attributeMatcher, characterAttributes, 1, 1);
			if (! attributeMatcher.find()) break;
		}
		while(attributeMatcher.end() < partMatcher.end());
	}

	/** 
	 Formatiert den Text der aktuellen Group
	 des Matchers mit dem AttributeSet.
	 */
	private void applyAttributeSet(Matcher m, AttributeSet attributeSet) {
		applyAttributeSet(m, attributeSet, 0, 0);
	}

	/** 
	 Formatiert den Text der aktuellen Group (abzüglich bestimmter Ränder links und rechts)
	 des Matchers mit dem AttributeSet.
	 */
	private void applyAttributeSet(Matcher m, AttributeSet attributeSet, int additionalOffset, int lengthReduction) {
		int offset = m.start();
		int length = m.group().length();
		getStyledDocument().
		setCharacterAttributes(offset+additionalOffset, 
				length-lengthReduction, attributeSet, true);
	}
}
