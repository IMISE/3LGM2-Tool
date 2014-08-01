/*
 * Created on 05.11.2003
 *
 */
package de.imise.tool3lgm.graphtools.dialog;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;

import de.imise.util.swing.component.TabbedPane;
import de.imise.util.swing.component.text.ExtendedTextPane;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.log.Log;
/**
 * Eigenschaftendialog für ein Modell
 * 
 * @author AXS
 */
public class ModelPropertyDialogOld extends PropertyDialog {

	TabbedPane tabbedPane = new TabbedPane();
	ExtendedTextPane textPane;
	GraphDocument lastActiveDoc=null;
	MyScrollPane scrollPane;
	
	static int lastWidth=-1, lastHeight=-1, lastPositionX=-1, lastPositionY=-1;

	/**
	 * @param String gdcoll
	 * @throws java.awt.HeadlessException
	 */
	public ModelPropertyDialogOld(GDCollection gdcoll) throws HeadlessException {
		super(gdcoll);

		addComponentListener(new ComponentListener(){
			@Override
			public void componentHidden(ComponentEvent e){} 
			@Override
			public void componentMoved(ComponentEvent e){dialogPositionOrSizeChanged();} 
			@Override
			public void componentResized(ComponentEvent e){dialogPositionOrSizeChanged();} 
			@Override
			public void componentShown(ComponentEvent e){} 
		});

		try {
		  init();
		}
		catch(Exception e) {
		  Log.show(Log.FATAL, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
		}


	}


	private void init() throws Exception {

		scrollPane = new MyScrollPane(doc);
		textPane = new ExtendedTextPane();
		getContentPane().setLayout(new BorderLayout());
		if (lastWidth == -1) {
			lastWidth = 600;
			lastHeight = 400;
			lastPositionX=0;
			lastPositionY=0;
		}
		setLocation(lastPositionX, lastPositionY);
		setSize(lastWidth,lastHeight);

		textPane.setText(gdcoll.getMainGraphDocument().getDescription());
		
		textPane.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
		
		lastActiveDoc = gdcoll.getMainGraphDocument();

		tabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				tabbedPane_stateChanged(e);
			}
		});

		tabbedPane.add(scrollPane, Tool3lgmConstants.getResString("uebersicht"));

		for (int i=0; i<gdcoll.getNumberOfSzenarios(); i++){
			scrollPane = new MyScrollPane(gdcoll.getSzenario(i));
			tabbedPane.add(scrollPane, gdcoll.getSzenario(i).getTitle());
		}

		getContentPane().add(tabbedPane,  BorderLayout.CENTER);
		
		setTitle(Tool3lgmConstants.getResString("description")
			+ " - " 
			+ gdcoll.getName() 
			+ " - " 
			+ Tool3lgmConstants.getResString("uebersicht")); 


		textPane.setText(gdcoll.getMainGraphDocument().getDescription());

	}

	/**
	 * @param doc
	 * @return
	 */
	private int getTabIndex(GraphDocument doc) {
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			if (((MyScrollPane)tabbedPane.getComponentAt(i)).getGraphDocument() == doc)
				return i;
		}
		return -1;
	}
	
	@Override
	public void setVisible(boolean b){
		if (b) {
			int tabIndex = getTabIndex(gdcoll.getSelectedDoc());
			if (tabIndex < 0)
				tabIndex = 0;
			tabbedPane.setSelectedIndex(tabIndex);
		}
		super.setVisible(b);
	}
	
	public boolean removeTab(GraphDocument doc){
		int tabIndex = getTabIndex(doc);
		if (tabIndex < 0)
			return false;
		tabbedPane.remove(tabIndex);
		tabbedPane_stateChanged(new ChangeEvent(this));
		return true;
	}

	public void addTab(GraphDocument doc){
		scrollPane = new MyScrollPane(doc);
		tabbedPane.add(scrollPane);
	}
	
	public void renameTab(GraphDocument doc){
		if (removeTab(doc))
			addTab(doc);
	}

	@Override
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			gdcoll.showDescriptionFrame(false);
			lastActiveDoc.setDescription(textPane.getText());
		}
		if (e.getID() == WindowEvent.WINDOW_DEACTIVATED) {
			lastActiveDoc.setDescription(textPane.getText());
		}
	}
	

	public void actualizeFrameTitle(){
		if (tabbedPane.getSelectedIndex()>0)
			setTitle(Tool3lgmConstants.getResString("description")
				+ " - " 
				+ gdcoll.getName() 
				+ " - " 
				+ gdcoll.getSzenario(tabbedPane.getSelectedIndex()-1).getTitle());
			else
				setTitle(Tool3lgmConstants.getResString("description")
					+ " - " 
					+ gdcoll.getName() 
					+ " - " 
					+ Tool3lgmConstants.getResString("uebersicht"));
	}
	
	void tabbedPane_stateChanged(ChangeEvent e) {
		actualizeFrameTitle();
		lastActiveDoc.setDescription(textPane.getText());
		((JScrollPane)tabbedPane.getSelectedComponent()).getViewport().add(textPane, null);
		if (tabbedPane.getSelectedIndex()==0){
			lastActiveDoc = gdcoll.getMainGraphDocument();
			textPane.setText(gdcoll.getMainGraphDocument().getDescription());
		}
		else{
			lastActiveDoc = gdcoll.getSzenario(tabbedPane.getSelectedIndex()-1);
			textPane.setText(gdcoll.getSzenario(tabbedPane.getSelectedIndex()-1).getDescription());
		}
	}


	private void dialogPositionOrSizeChanged(){
		lastWidth = getWidth();
		lastHeight = getHeight();
		lastPositionX = getX();
		lastPositionY = getY();
	}
	
	/**
	 * @author astruebi
	 * @create 15.02.2012
	 */
	private class MyScrollPane extends JScrollPane {
		
		private GraphDocument doc;
		
		private MyScrollPane(GraphDocument doc) {
			super();
			this.doc = doc;
		}

		/* (non-Javadoc)
		 * @see java.awt.Component#getName()
		 */
		@Override
		public String getName() {
			return doc.getTitle();
		}
		
		/**
		 * @return
		 */
		public GraphDocument getGraphDocument() {
			return doc;
		}
	}
	
}