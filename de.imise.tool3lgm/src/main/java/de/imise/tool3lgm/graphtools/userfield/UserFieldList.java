package de.imise.tool3lgm.graphtools.userfield;

import java.util.ArrayList;
import java.util.Iterator;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.log.Log;

/**
 * @author Thomas Rudert
 */
public class UserFieldList implements Cloneable, Iterable<UserField>{

	/**
	 * COMMENTME
	 */
	private ArrayList<UserField> userFields = new ArrayList<UserField>();
	
	/**
	 * COMMENTME
	 */
	private Class<? extends UserFieldTarget> targetClass;
	
	/**
	 * @param targetClass
	 */
	public UserFieldList(Class<? extends UserFieldTarget> targetClass) {
		this.targetClass = targetClass;
	}
		
	/**
	 * @param userField
	 * @param index
	 */
	public void insert(UserField userField, int index) {
		userFields.remove(userField);
		userFields.add(index,userField);
	}
	
	/**
	 * @param userField
	 */
	public void add(UserField userField) {
		if (userField.hasStyle(UserField.Style.FORMAT))
			insert(userField, 0);
		if (!userFields.contains(userField)){
			userFields.add(userField);
		}else{
		    // Das hier ermöglich das Importieren von userFields. 
		    // Es werden somit schon bestehende userField und deren Eiegenschaften überschrieben
		    userFields.remove(userField);
		    userFields.add(userField);
		}
	}
	
	/**
	 * @param userField
	 */
	public void remove(UserField userField) {
		userFields.remove(userField);
	}
	
	/**
	 * @return
	 */
	public int getUserFieldsCount() {
		return userFields.size();
	}
	
	/**
	 * @param i
	 * @return
	 */
	public UserField get(int i) {
		if (i < 0 || i >= userFields.size())
			return null;
		return userFields.get(i);
	}
	
	/**
	 * @return
	 */
	public Class<? extends UserFieldTarget> getTargetClass() {
		return targetClass;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
	    UserFieldList collection = null;
		try {
		    collection = (UserFieldList) super.clone();
		} catch (Exception e) {
			Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
			return null;
		}
		collection.targetClass = this.targetClass;
		collection.userFields = new ArrayList<UserField>(userFields);
		return collection;
	}

	/**
	 * @return
	 */
	public String toXMLString() {
		String retVal = new String();
		for (UserField uf : userFields)
			retVal = retVal.concat(uf.toXMLString());
		return retVal;
	}
	
	/**
	 * @param hashString
	 * @return
	 */
	public UserField get(Object hashString) {
		for (UserField uf : userFields)
			if (uf.getHashCode().equals(hashString))
				return uf;
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<UserField> iterator() {
		return userFields.iterator();
	}
}
