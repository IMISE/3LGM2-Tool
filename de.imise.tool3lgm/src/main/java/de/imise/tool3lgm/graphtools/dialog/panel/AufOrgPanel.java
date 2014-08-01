package de.imise.tool3lgm.graphtools.dialog.panel;
/**
 * @author AXS
 * created on 20.07.2007
 */
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.ActionNotDefinedForClassException;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.dialog.action.LGMTreeSelectionListener;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.LGMDragNDropTree;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer.DragNDropActionChain;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.AufOrgKombination;
import de.imise.tool3lgm.graphtools.elements.node.Organisationseinheit;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;
/**
 * Mit diesem Panel zeigen
 * <ul>
 * <li>Aufgaben ihre Organisationseinheiten</li>
 * <li>Organisationseinheiten ihre Aufgaben</li>
 * </ul><br>
 * an. Hinzufügen von Assoziationen zw. den Elementen und Neuanlegen
 * von Aufgaben bzw. Organisatiosneinheiten geht auch.
 * 
 * Dieses Panel ersetzt das <code>AufOrgPanel</code> und das <code>OrgAufPanel</code>.
 * Wenn es sich bewährt, kann man die anderen beiden Panels löschen und diesen
 * Kommentar entfernen.
 */
public class AufOrgPanel extends LGMDragNDropPanel {
	private Class<? extends ModelElement> elementClass;
	private LGMDragNDropTree ltree;
	private LGMDragNDropTree rtree;
	private DefaultTreeModel lmodel, rmodel;
	private JScrollPane spl, spr;
	private LGMTreeNode lroot, rroot;
	private JPanel buttonpanel;
	private JLabel orglabel;
	private boolean mw = true;
	
	private LGMAction addAction;
	private LGMAction removeAction;
	private LGMAction newElementAction;

	public AufOrgPanel(Class<? extends ModelElement> elementClass, ElementPropertyDialog dl, boolean mitnew) {
		super(dl);
		
		this.elementClass = elementClass;
		mw = mitnew;

		//		setPreferredSize(new Dimension(550,350));
		GridBagLayout gbl = new GridBagLayout();
		setLayout(gbl);
		GridBagConstraints constraints = new GridBagConstraints();

		String searchKnotElementString;
		//für Aufgaben werden OEs gesucht
		if (elementClass==Organisationseinheit.class)
			searchKnotElementString = Tool3lgmConstants.getResString("Organisationseinheit_p");
		//für OEs werden Aufgaben gesucht
		else
			searchKnotElementString = Tool3lgmConstants.getResString("Aufgabe_p");
		orglabel = new JLabel(searchKnotElementString);

		lroot = new LGMTreeNode(searchKnotElementString, false);
		lmodel = new DefaultTreeModel(lroot);
		ltree = new LGMDragNDropTree(lmodel, mainDoc);
		ltree.setRootVisible(false);
		ltree.setShowsRootHandles(true);
		ltree.setCellRenderer(treeRenderer);
		ltree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		spl = new JScrollPane(ltree);

		rroot = new LGMTreeNode(searchKnotElementString, false);
		rmodel = new DefaultTreeModel(rroot);
		rtree = new LGMDragNDropTree(rmodel, mainDoc);
		rtree.setRootVisible(false);
		rtree.setShowsRootHandles(true);
		rtree.setCellRenderer(treeRenderer);
		rtree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		spr = new JScrollPane(rtree);

		constraints.anchor = GridBagConstraints.EAST;
		constraints.ipadx = -30;
		constraints.ipady = -10;
		add(this, viewButton, constraints, 0, 2, 1, 1);
		constraints.ipadx = 0;
		constraints.ipady = 0;
		constraints.anchor = GridBagConstraints.WEST;
		JLabel label = new JLabel(searchKnotElementString);
		add(this, label, constraints, 0, 0, 1, 1);
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 100;
		constraints.weighty = 100;
		add(this, spl, constraints, 0, 1, 1, 1);
		
		/*
		 * Start: TreeSelectionListener erstellen und an Trees anhängen
		 * ...
		 */
		LGMAction ltreeSelectionAction = LGMActionLibrary.getTreeSelectionAction(ltree,this);
		LGMAction rtreeSelectionAction = LGMActionLibrary.getTreeSelectionAction(rtree,this);
		
		ltree.addTreeSelectionListener(new LGMTreeSelectionListener(ltreeSelectionAction));
		rtree.addTreeSelectionListener(new LGMTreeSelectionListener(rtreeSelectionAction));
		/*
		 * ...
		 * End: TreeSelectionListener erstellen und an Trees anhängen
		 */
		
		/*
		 * Start: MouseListener erstellen und an Trees anhängen
		 * ...
		 */
		LGMAction ltreeMouseAction = LGMActionLibrary.getMouseAction(ltree,this);
		LGMAction rtreeMouseAction = LGMActionLibrary.getMouseAction(rtree,this);

		ltree.addMouseListener(new LGMMouseListener(null,null,null,ltreeMouseAction, null));
		rtree.addMouseListener(new LGMMouseListener(null,null,null,rtreeMouseAction, null));
		/*
		 * ...
		 * End: MouseListener erstellen und an Trees anhängen
		 */
		
		/*
		 * Start: Buttons & Actions erstellen und registrieren
		 * ...
		 */
		JButton addButton = new JButton();
		JButton removeButton = new JButton();
		JButton newElementButton = new JButton();
		
		try {
		this.addAction = LGMActionLibrary.getAddElementAction(this.rtree,this.ltree,this,true);
		this.removeAction = LGMActionLibrary.getDisconnectAction(this.ltree,this.rtree,this,false);
		this.newElementAction = LGMActionLibrary.getNewElementAction(this,this.elementClass);
		}
		// TODO: FST: Für alle Panels übernehmen
		catch (ActionNotDefinedForClassException e) {
			Log.log(Log.DEBUG,e.getMessage());
		}
		addButton.setAction(this.addAction);
		removeButton.setAction(this.removeAction);
		newElementButton.setAction(this.newElementAction);
		/*
		 * ...
		 * end: Buttons & Actions erstellen und registrieren
		 */
		
		buttonpanel = new JPanel();
		buttonpanel.setSize(30, 250);
		buttonpanel.setLayout(new GridLayout(3, 1));
		buttonpanel.add(addButton);
		buttonpanel.add(removeButton);
		
		if (mw)
			buttonpanel.add(newElementButton);

		init();
	}

	ArrayList<ElementContainer> childrenToExcludeFromRtree = new ArrayList<ElementContainer>(5000);
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.dialog.panel.LGMDragNDropPanel#init()
	 */
	@Override
	public void init() {
		super.init();
		remove(orglabel);
		remove(buttonpanel);
		remove(spr);
		childrenToExcludeFromRtree.clear();
		ltree.saveExpansion();
		ltree.saveSelection();
		lroot.removeAllChildren();
		ltree.reset();
		ModelElement modelElement = getModelElement();
		ArrayList<ElementContainer> aufOrgKombis = modelElement.getConnectedContainer(AufOrgKombination.class, mainDoc);
		//nur Knoten für ELemente in der all-Liste bis zur Größe der direkt vrbundenen dürfen am Ende
		//selektierbar sein
		int firstNonSelectableIndex = aufOrgKombis.size();
		if (UserProperties.isSearchParts())
			aufOrgKombis.addAll(modelElement.getPartConnectedContainer(AufOrgKombination.class, mainDoc));
		if (UserProperties.isSearchParents())
			aufOrgKombis.addAll(((Knoten) modelElement).getParentConnectedContainer(AufOrgKombination.class, mainDoc));
		//Oe sollen für Aufgaben dargestellt werden
		for (int m = 0; m < aufOrgKombis.size(); m++) {
			for (ElementContainer ec : aufOrgKombis.get(m).getElement().getConnectedContainer(elementClass, mainDoc)) {
				LGMTreeNode node = ltree.addObject(ec, lroot, null, true, false, false);
				if (node != null) { 
					node.setUserObject(aufOrgKombis.get(m), 1);
					//alle Elemente die von den Parts oder Parents kamen, nichtselktierbar setzen
					if (m>=firstNonSelectableIndex)
						node.setSelectable(false);
				}
				childrenToExcludeFromRtree.add(ec);
			}
		}
		lmodel.reload();
		ltree.restoreExpansion();
		revalidate();
		repaint();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.dialog.panel.ElementDialogPanel#showFullDialog()
	 */
	@Override
	public void showFullDialog() {
		super.showFullDialog();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.NONE;
		add(this, buttonpanel, constraints, 1, 1, 1, 1);
		constraints.anchor = GridBagConstraints.WEST;
		add(this, orglabel, constraints, 2, 0, 1, 1);
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 100;
		constraints.weighty = 100;
		add(this, spr, constraints, 2, 1, 1, 1);

		rtree.saveExpansion();
		rtree.saveSelection();
		
		rroot.removeAllChildren();
		rtree.reset();
		for (ElementContainer ec : mainDoc.getElementContainer(elementClass))
			rtree.addObject(ec, rroot, childrenToExcludeFromRtree, false, true);

		rmodel.reload();
		rtree.restoreExpansion();

		revalidate();
		repaint();
	}

	/**
	 * @return
	 */
	public Class<? extends ModelElement> getElementClass() {
		return this.elementClass;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.dialog.panel.LGMDragNDropPanel#collectDragNDropActionChains()
	 */
	@Override
	protected DragNDropActionChain[] collectDragNDropActionChains() {
		DragNDropActionChain tac1 = DragNDropInitializer.createNewDragNDropActionChain(this.rtree, this.ltree,this.addAction);
		DragNDropActionChain tac2 = DragNDropInitializer.createNewDragNDropActionChain(this.ltree, this.rtree,this.removeAction);
		return new DragNDropActionChain[] {tac1,tac2};
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.dialog.panel.LGMDragNDropPanel#getAllDragNDropTrees()
	 */
	@Override
	public LGMDragNDropTree[] getAllDragNDropTrees() {
		return new LGMDragNDropTree[] {this.rtree, this.ltree};
	}
}
