package de.imise.util;

import java.awt.Color;

/**
 * AXS: Das Zeug hier kommt ursprünglich aus Tool3lgmConstants.
 * 
 * TODO:AXS: das sollte sich wohl über das UTF-8-<code>CharSet</code> lösen lassen und dann raus hiermit. 
 * 
 * @author N.N., AXS
 * created on 16.08.2007
 */
public class HTMLConverter {

	/**
	 * Keine Instanzen zulassen.
	 */
	private HTMLConverter() {
	}

	/**
	 * Wandelt Sonderzeichen im übergebenen String HTML-konform um und gibt den String zurück.
	 * @param instr
	 * @return
	 */
	public static final String getHTMLString(String instr) {
		StringBuilder buf = new StringBuilder();
		char character;
		if (instr == null)
			return null;
		int l = instr.length();
		for (int c = 0; c < l; c++) {
			character = instr.charAt(c);
			switch (character) {
				case '<' :
					buf.append("&lt;");
					continue;
				case '>' :
					buf.append("&gt;");
					continue;
				case '&' :
					buf.append("&amp;");
					continue;
				case '\"' :
					buf.append("&quot;");
					continue;
				case '\'' :
					buf.append("&apos;");
					continue;
				case '\\' :
					buf.append("\\");
					continue;
				case 'Ä' :
					buf.append("&#196;");
					continue;
				case 'Ö' :
					buf.append("&#214;");
					continue;
				case 'Ü' :
					buf.append("&#220;");
					continue;
				case 'ß' :
					buf.append("&#223;");
					continue;
				case 'ä' :
					buf.append("&#228;");
					continue;
				case 'ö' :
					buf.append("&#246;");
					continue;
				case 'ü' :
					buf.append("&#252;");
					continue;
				case 'À' :
					buf.append("&#192;");
					continue;
				case 'Á' :
					buf.append("&#193;");
					continue;
				case 'Â' :
					buf.append("&#194;");
					continue;
				case 'Ã' :
					buf.append("&#195;");
					continue;
				case 'Å' :
					buf.append("&#197;");
					continue;
				case 'Æ' :
					buf.append("&#198;");
					continue;
				case 'Ç' :
					buf.append("&#199;");
					continue;
				case 'È' :
					buf.append("&#200;");
					continue;
				case 'É' :
					buf.append("&#201;");
					continue;
				case 'Ê' :
					buf.append("&#202;");
					continue;
				case 'Ë' :
					buf.append("&#203;");
					continue;
				case 'Ì' :
					buf.append("&#204;");
					continue;
				case 'Í' :
					buf.append("&#205;");
					continue;
				case 'Î' :
					buf.append("&#206;");
					continue;
				case 'Ï' :
					buf.append("&#207;");
					continue;
				case 'Ñ' :
					buf.append("&#209;");
					continue;
				case 'Ò' :
					buf.append("&#210;");
					continue;
				case 'Ó' :
					buf.append("&#211;");
					continue;
				case 'Ô' :
					buf.append("&#212;");
					continue;
				case 'Õ' :
					buf.append("&#213;");
					continue;
				case 'Ø' :
					buf.append("&#216;");
					continue;
				case 'Ù' :
					buf.append("&#217;");
					continue;
				case 'Ú' :
					buf.append("&#218;");
					continue;
				case 'Û' :
					buf.append("&#219;");
					continue;
				case 'Ý' :
					buf.append("&#221;");
					continue;
				case 'à' :
					buf.append("&#224;");
					continue;
				case 'á' :
					buf.append("&#225;");
					continue;
				case 'â' :
					buf.append("&#226;");
					continue;
				case 'ã' :
					buf.append("&#227;");
					continue;
				case 'å' :
					buf.append("&#229;");
					continue;
				case 'æ' :
					buf.append("&#230;");
					continue;
				case 'ç' :
					buf.append("&#231;");
					continue;
				case 'è' :
					buf.append("&#232;");
					continue;
				case 'é' :
					buf.append("&#233;");
					continue;
				case 'ê' :
					buf.append("&#234;");
					continue;
				case 'ë' :
					buf.append("&#235;");
					continue;
				case 'ì' :
					buf.append("&#236;");
					continue;
				case 'í' :
					buf.append("&#237;");
					continue;
				case 'î' :
					buf.append("&#238;");
					continue;
				case 'ï' :
					buf.append("&#239;");
					continue;
				case 'ñ' :
					buf.append("&#241;");
					continue;
				case 'ò' :
					buf.append("&#242;");
					continue;
				case 'ó' :
					buf.append("&#243;");
					continue;
				case 'ô' :
					buf.append("&#244;");
					continue;
				case 'õ' :
					buf.append("&#245;");
					continue;
				case 'ø' :
					buf.append("&#248;");
					continue;
				case 'ù' :
					buf.append("&#249;");
					continue;
				case 'ú' :
					buf.append("&#250;");
					continue;
				case 'û' :
					buf.append("&#251;");
					continue;
				case 'ý' :
					buf.append("&#253;");
					continue;
				case 'ÿ' :
					buf.append("&#255;");
					continue;
				case 'µ' :
					buf.append("&#181;");
					continue;
				case '²' :
					buf.append("&#178;");
					continue;
				case '³' :
					buf.append("&#179;");
					continue;
				case '!' :
					buf.append("&#33;");
					continue;
				case '#' :
					buf.append("&#35;");
					continue;
				case '$' :
					buf.append("&#36;");
					continue;
				case '%' :
					buf.append("&#37;");
					continue;
				case '(' :
					buf.append("&#40;");
					continue;
				case ')' :
					buf.append("&#41;");
					continue;
				case '*' :
					buf.append("&#42;");
					continue;
				case '+' :
					buf.append("&#43;");
					continue;
				case ',' :
					buf.append("&#44;");
					continue;
				case '-' :
					buf.append("&#45;");
					continue;
				case '.' :
					buf.append("&#46;");
					continue;
				case '/' :
					buf.append("&#47;");
					continue;
				case ':' :
					buf.append("&#58;");
					continue;
				case ';' :
					buf.append("&#59;");
					continue;
				case '=' :
					buf.append("&#61;");
					continue;
				case '?' :
					buf.append("&#63;");
					continue;
				case '@' :
					buf.append("&#64;");
					continue;
				case '{' :
					buf.append("&#123;");
					continue;
				case '|' :
					buf.append("&#124;");
					continue;
				case '}' :
					buf.append("&#125;");
					continue;
				case '[' :
					buf.append("&#91;");
					continue;
				case ']' :
					buf.append("&#93;");
					continue;
				case '^' :
					buf.append("&#94;");
					continue;
				case '`' :
					buf.append("&#96;");
					continue;
				case '~' :
					buf.append("&#126;");
					continue;
				case '¡' :
					buf.append("&#161;");
					continue;
				case '¢' :
					buf.append("&#162;");
					continue;
				case '£' :
					buf.append("&#163;");
					continue;
				case '€' :
					buf.append("&#8364;");
					continue; //EURO
				case '¤' :
					buf.append("&#164;");
					continue;
				case '¥' :
					buf.append("&#165;");
					continue;
				case '¦' :
					buf.append("&#166;");
					continue;
				case '§' :
					buf.append("&#167;");
					continue;
				case '¨' :
					buf.append("&#168;");
					continue;
				case '©' :
					buf.append("&#169;");
					continue;
				case 'ª' :
					buf.append("&#170;");
					continue;
				case '«' :
					buf.append("&#171;");
					continue;
				case '¬' :
					buf.append("&#172;");
					continue;
				case '­' :
					buf.append("&#173;");
					continue;
				case '®' :
					buf.append("&#174;");
					continue;
				case '¯' :
					buf.append("&#175;");
					continue;
				case '°' :
					buf.append("&#176;");
					continue;
				case '±' :
					buf.append("&#177;");
					continue;
				case '´' :
					buf.append("&#180;");
					continue;
				case '¶' :
					buf.append("&#182;");
					continue;
				case '·' :
					buf.append("&#183;");
					continue;
				case '¸' :
					buf.append("&#184;");
					continue;
				case '¹' :
					buf.append("&#185;");
					continue;
				case 'º' :
					buf.append("&#186;");
					continue;
				case '»' :
					buf.append("&#187;");
					continue;
				case '¼' :
					buf.append("&#188;");
					continue;
				case '½' :
					buf.append("&#189;");
					continue;
				case '¾' :
					buf.append("&#190;");
					continue;
				case '¿' :
					buf.append("&#191;");
					continue;
				case 'Ð' :
					buf.append("&#208;");
					continue;
				case '×' :
					buf.append("&#215;");
					continue;
				case 'Þ' :
					buf.append("&#222;");
					continue;
				case '÷' :
					buf.append("&#247;");
					continue;
				case 'þ' :
					buf.append("&#254;");
					continue;
				case '\n' :
					buf.append("&#10;");
					continue;
				default :
					if (Character.isWhitespace(character) || Character.isLetterOrDigit(character))
						buf.append(instr.charAt(c));
					else if(Character.getNumericValue(character)>-1)
						buf.append("&#" + Character.getNumericValue(character) + ";");
					continue;
			}
		}
		return buf.toString();
	}
	
	/**
	 * Gibt den HTML-Farbcode der uebergebenen Farbe in der Form "#rrggbb" zurueck.
	 * @param col
	 * @return
	 */
	public static String getHTMLColor(Color col){
		//in der Funktion steht zwar 6 mal dieselbe switch-Anweisung, aber so ist es am schnellsten!
		StringBuilder sb = new StringBuilder("#");

		//Rot
		int val = col.getRed();
		int i = (val/16);
		if (i<10)
			sb.append(i);
		else switch (i){
			case 10: sb.append('A'); break;
			case 11: sb.append('B'); break;
			case 12: sb.append('C'); break;
			case 13: sb.append('D'); break;
			case 14: sb.append('E'); break;
			case 15: sb.append('F');
		}
		i = (val%16);
		if (i<10)
			sb.append(i);
		else switch (i){
			case 10: sb.append('A'); break;
			case 11: sb.append('B'); break;
			case 12: sb.append('C'); break;
			case 13: sb.append('D'); break;
			case 14: sb.append('E'); break;
			case 15: sb.append('F');
		}

		//Gruen
		val = col.getGreen();
		i = (val/16);
		if (i<10)
			sb.append(i);
		else switch (i){
			case 10: sb.append('A'); break;
			case 11: sb.append('B'); break;
			case 12: sb.append('C'); break;
			case 13: sb.append('D'); break;
			case 14: sb.append('E'); break;
			case 15: sb.append('F');
		}
		i = (val%16);
		if (i<10)
			sb.append(i);
		else switch (i){
			case 10: sb.append('A'); break;
			case 11: sb.append('B'); break;
			case 12: sb.append('C'); break;
			case 13: sb.append('D'); break;
			case 14: sb.append('E'); break;
			case 15: sb.append('F');
		}

		//Blau
		val = col.getBlue();
		i = (val/16);
		if (i<10)
			sb.append(i);
		else switch (i){
			case 10: sb.append('A'); break;
			case 11: sb.append('B'); break;
			case 12: sb.append('C'); break;
			case 13: sb.append('D'); break;
			case 14: sb.append('E'); break;
			case 15: sb.append('F');
		}
		i = (val%16);
		if (i<10)
			sb.append(i);
		else switch (i){
			case 10: sb.append('A'); break;
			case 11: sb.append('B'); break;
			case 12: sb.append('C'); break;
			case 13: sb.append('D'); break;
			case 14: sb.append('E'); break;
			case 15: sb.append('F');
		}
		return sb.toString();
	}

	

}
