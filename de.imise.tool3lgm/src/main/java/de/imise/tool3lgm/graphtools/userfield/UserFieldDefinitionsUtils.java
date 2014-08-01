/*
 * Created on 13.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield;


/**
 * @author AXS
 */
public class UserFieldDefinitionsUtils {

	/**
	 * 
	 */
	public UserFieldDefinitionsUtils() {
		super();
	}

	/**
	 * Prüft, ob eine Kreisreferenz in einer Formel vorliegt. Bei Vorliegen einer  Kreisreferenz würde eine Endlosschleife entstehen, 
	 * in dem die Kennzahlformeln immer wieder aufgelöst werden würden.
	 * 
	 * Wenn noch vor dem Einfügen eines <code>UserField</code>s in eine KennzahlFormel geprüft werden soll, ob dann eine Kreisreferenz enstünde, 
	 * ist der zweite Parameter als das zu prüfende zu übergeben.
	 * Wenn  eine fertige Formel überprüft werden soll, kann(muss) als zweites zu vergleichendes <code>UserField</code> null übergeben werden.
	 *   
	 * @param userField : Das Haupt - <code>UserField</code>. Von diesem soll die Kenzahlformel überrüft werden.
	 * @param <ul><li>field	: Das <code>UserField</code>, das vor dem Einfügen in eine KennzahlFormel überprüft werden soll. Wenn in seiner Kennzahlformel und in den Kennzahlformeln seiner UserField das HauptUserField vorkommt, ist eine Kreisreferenz gefunden.</li>
	 * <li>null, wenn fertige Formel geprüft werden soll.</li>
	 * </ul>
	 * 
	 * @return <code>true</code>, wenn Kreisreferenzen gefunden wurden, sonst <code>false</code>.
	 * /
	
	//die Funktion kann so nicht bleiben. Sie braucht nicht das 2te UserField und sollte die Definitions als Parameter mitbekommen und sich nicht vom Selektierten Doc holen
	
	public static boolean _hasCircuitReferences(UserField userField, UserField field) {

		//Dummy-UserField,  falls eine fertige Formel überprüft werden soll.
		if (field == null)
			field = new UserField(null, userField.getDefinitions());

		if (field.equals(userField)) {
			JOptionPane.showMessageDialog(null, Tool3lgmConstants.getErrString("circuit_reference"), Tool3lgmConstants.getResString("fehler"),JOptionPane.ERROR_MESSAGE);
			return true;
		} else {
			if (field.getTargetClass() == null)
				field = userField;
			if (field.getStyle() == UserField.CLASSIFICATION_NUMBER_FORMULA_STYLE) {

				String hashStringOfField = field.getFormula();
				StringTokenizer st = new StringTokenizer(hashStringOfField, " ()+-/*|");
				UserFieldDefinitions definitions = userField.getDefinitions();
				while (st.hasMoreElements()) {
					String hashString = st.nextElement().toString();
					if (hashString.startsWith(UserField.USERFIELD_HASH_STRING_PREFIX)) {
						UserField u = definitions.getUserField(hashString);
						return _hasCircuitReferences(userField, u);
					}
				}
			}
		}
		return false;
	}


	*/
}
